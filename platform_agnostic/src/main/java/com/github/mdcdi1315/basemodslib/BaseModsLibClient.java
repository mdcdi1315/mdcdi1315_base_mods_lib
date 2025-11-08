package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Stopwatch;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.config.ConfigManager;
import com.github.mdcdi1315.basemodslib.utils.EmptyEnumerable;
import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientStartedEvent;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientStoppingEvent;
import com.github.mdcdi1315.basemodslib.config.gui.ConfigurationScreenFactory;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientConnectedToServerEvent;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientDisconnectedFromServerEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides services for initializing client-side mods with the Base Mods Library.
 */
@ClientOnlyEnvironment
public final class BaseModsLibClient
{
    private static IClientModLoaderLayer layer;
    private static volatile boolean initialized;
    private static List<IClientModInstance> mod_instances;
    // The below field is created lazily on first registration.
    // Even if the method that should call this calls in but remains null, it will keep it as null.
    private static List<Pair<String, ConfigurationScreenFactory<?>>> config_factories;

    static {
        if (BaseModsLib.GetEnvironment() != ModdingEnvironment.CLIENT) {
            // Perpetually crash Minecraft to avoid having weird crash reports due to class loading.
            // We will have a crash report anyway; just this will save some time finding bugs...
            throw new InvalidOperationException("Base mods library client was touched but it should not!");
        }
        layer = null;
        initialized = false;
        mod_instances = null;
        config_factories = null;
    }

    private BaseModsLibClient() {}

    /**
     * Initializes the Base Mods library for the Minecraft client distribution.
     * @param client_layer The client mod loader layer to initialize the base mods library from.
     * @throws ArgumentNullException {@code client_layer} is {@code null}.
     */
    public static void InitializeBaseModsLibClient(IClientModLoaderLayer client_layer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(client_layer, "client_layer");
        if (layer != null) {
            throw new InvalidOperationException("The base mods library has already been initialized successfully.");
        }
        BaseModsLib.LOGGER.info("Setting up base mods library for the client.");
        layer = client_layer;
        mod_instances = new List<>();
        var em = BaseModsLib.GetEventsManager();
        em.AddEvent(ClientStartedEvent.class);
        em.AddEvent(ClientStoppingEvent.class);
        em.AddEvent(ClientConnectedToServerEvent.class);
        em.AddEvent(ClientDisconnectedFromServerEvent.class);
        initialized = true;
    }

    /**
     * Initializes the specified mod instance on the client side of Minecraft. <br />
     * Only call this when you are initializing in a usual Minecraft client.
     * @param instance The mod instance to initialize.
     * @param mod_object The mod object, provided by the mod loader. What will this object be depends on the mod loader that is being used.
     * @throws ArgumentNullException {@code instance} was {@code null}.
     */
    public static void InitializeClientSideMod(IClientModInstance instance, Object mod_object)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(instance, "instance");
        ArgumentNullException.ThrowIfNull(mod_object, "mod_object");
        Stopwatch sw = Stopwatch.StartNew();
        try {
            instance.Initialize();

            while (!initialized) { Thread.onSpinWait(); } // Wait until the library is fully initialized.

            instance.SetupConfigurationFiles(ConfigManager.INSTANCE);

            // Initialize event handling - may be needed so early to assure that all events will be properly fired later.
            instance.RegisterEvents(BaseModsLib.GetEventsManager());

            layer.InitializeClientModInstance(instance, mod_object);

            var config_screen = instance.RegisterConfigurationScreenFactory();

            if (config_screen != null) {
                AddConfigScreenFactory(instance.GetModId(), config_screen);
            }

            instance.OnInitializeEnd();

            sw.Stop();

            BaseModsLib.LOGGER.info("BASEMODSLIB: Client mod instance with ID {} initialized successfully after {} seconds." , instance.GetModId() , sw.GetElapsed().GetTotalSeconds());

            mod_instances.Add(instance); // The instance is made known to other mods after the mod has completed initialization.
        } catch (Exception e) {
            var id = instance.GetModId();
            sw.Stop();
            BaseModsLib.LOGGER.info("BASEMODSLIB: Mod instance with ID {} failed after {} seconds." , id, sw.GetElapsed().GetTotalSeconds());
            BaseModsLib.LOGGER.error("BASEMODSLIB: Cannot initialize client-side mod id {}!\nRethrowing the exception to the underlying mod." , id);
            throw new ModInitializationException(id, e);
        }
    }

    private static void AddConfigScreenFactory(String mod_id, ConfigurationScreenFactory<?> fact)
    {
        if (config_factories == null) {
            config_factories = new List<>();
        }
        config_factories.Add(new Pair<>(mod_id, fact));
    }

    @MaybeNull
    public static Player GetLoggedInPlayer() {
        return Minecraft.getInstance().player;
    }

    /**
     * Gets an enumerable implementation that enumerates through the available configuration screens detected by the library.
     * @return An enumerable implementation containing configuration screen factories.
     */
    @NotNull
    public static IEnumerable<Pair<String, ConfigurationScreenFactory<?>>> GetConfigurationScreens() {
        return (config_factories == null) ? new EmptyEnumerable<>() : config_factories;
    }

    /**
     * Called by Minecraft when it shuts down, do not call this by your code!!
     */
    @ApiStatus.Internal
    public static void DestroySelf() {
        IClientModInstance mi;
        IEnumerator<IClientModInstance> i = null;
        try {
            i = mod_instances.GetEnumerator();
            while (i.MoveNext())
            {
                mi = i.getCurrent();
                try {
                    // Invoke to all mod instances the Dispose method.
                    mi.Dispose();
                } catch (Exception e) {
                    BaseModsLib.LOGGER.error("BASEMODSLIB: Cannot dispose mod with ID {} due to an exception: {}" , mi.GetModId() , e);
                }
            }
        } catch (Exception e) {
            BaseModsLib.LOGGER.error("BASEMODSLIB: Cannot run disposer due to an underlying exception." , e);
        } finally {
            if (i != null) { i.Dispose(); }
        }
        mod_instances = null;
        layer.Dispose();
        layer = null;
    }
}
