package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Stopwatch;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.*;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.eventapi.client.*;
import com.github.mdcdi1315.basemodslib.config.ConfigManager;
import com.github.mdcdi1315.basemodslib.utils.EmptyEnumerable;
import com.github.mdcdi1315.basemodslib.utils.ThreadSafeObject;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;
import com.github.mdcdi1315.basemodslib.utils.annotations.MixinUnsafe;
import com.github.mdcdi1315.basemodslib.mods.ClientModInstanceCollection;
import com.github.mdcdi1315.basemodslib.utils.annotations.MaybeNullInMixin;
import com.github.mdcdi1315.basemodslib.eventapi.mods.ModLoadingCompleteEvent;
import com.github.mdcdi1315.basemodslib.config.gui.ConfigurationScreenFactory;
import com.github.mdcdi1315.basemodslib.config.gui.ClothConfigIntegrationHandler;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

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
    private static ClientModInstanceCollection mod_instances;
    // The below field is created after the layer has been initialized.
    // Once mod loading completes and no factories are actually registered, this is then assigned to null.
    private static final ThreadSafeObject<SingleLinkedListBasedRegister<Pair<String, ConfigurationScreenFactory<?>>>> config_factories;

    static {
        if (BaseModsLib.GetEnvironment() == ModdingEnvironment.SERVER) {
            // Perpetually crash Minecraft to avoid having weird crash reports due to class loading.
            // We will have a crash report anyway; just this will save some time finding bugs...
            throw new InvalidOperationException("Base mods library client was touched but it should not!");
        }
        // Note: when env returns UNKNOWN the lib cannot assume the environment, so I assume it is OK and init continues normally.
        layer = null;
        initialized = false;
        mod_instances = null;
        config_factories = new ThreadSafeObject<>();
    }

    // Do not let anyone instantiate this class.
    private BaseModsLibClient() {}

    /**
     * Initializes the Base Mods library for the Minecraft client distribution. <br />
     * Remarks: <br />
     * Although that this is documented, this API should not be called by consumers of the library but only by the library itself. <br />
     * As the modding needs are evolving, the method requirements, and it's signature are subject to change without notice.
     * @param client_layer_constructor The client mod loader layer method reference providing the mod loader layer to initialize the base mods library from.
     * @throws ArgumentNullException {@code client_layer} is {@code null}.
     * @throws InvalidOperationException The library has been successfully initialized before.
     * @throws CriticalLibraryInitializationException A critical initialization error has been realized by the library. Execution cannot continue.
     */
    @ApiStatus.Internal
    @SuppressWarnings("resource")
    public static void InitializeBaseModsLibClient(Func1<IClientModLoaderLayer> client_layer_constructor)
            throws ArgumentNullException, InvalidOperationException, CriticalLibraryInitializationException
    {
        ArgumentNullException.ThrowIfNull(client_layer_constructor, "client_layer_constructor");
        if (layer != null) {
            throw new InvalidOperationException("The base mods library has already been initialized successfully.");
        } else {
            BaseModsLib.LOGGER.info("Initializing mdcdi1315's Base Mods Library for the client distribution...");
            Stopwatch sw = Stopwatch.StartNew();
            try {
                layer = client_layer_constructor.function();
                if (layer == null) {
                    throw new InvalidOperationException("Returned an empty client mod loader layer through the mod loader layer constructor. This is unexpected.");
                } else {
                    mod_instances = new ClientModInstanceCollection();
                    config_factories.SetValue(new SingleLinkedListBasedRegister<>());
                    BaseModsLib.GetEventsManager().AddEventListener(ModLoadingCompleteEvent.class, BaseModsLibClient::OnModLoadingCompleted);
                    ClothConfigIntegrationHandler.Instantiate();
                    sw.Stop();
                    BaseModsLib.LOGGER.info("The library for the client distribution took {} seconds to initialize." , sw.GetElapsed().GetTotalSeconds());
                }
            } catch (Throwable th) {
                sw.Stop();
                initialized = true;
                BaseModsLib.LOGGER.error("Library failed to be initialized after {} seconds! Inspecting exception and throwing back." , sw.GetElapsed().GetTotalSeconds());
                Exception e = BaseModsLib.TranslateException(th);
                if (e == null) {
                    throw th;
                } else {
                    throw new CriticalLibraryInitializationException(e);
                }
            }
            initialized = true;
        }
    }

    // This is registered after the mod loader layer constructor has been invoked, so we can safely execute the below.
    private static void OnModLoadingCompleted(ModLoadingCompleteEvent event)
    {
        boolean null_cfg_factories = false;
        try (var ctx = config_factories.Acquire())
        {
            if (!ctx.GetValue().HasItems()) { null_cfg_factories = true; }
        }
        if (null_cfg_factories) {
            config_factories.SetValue(null);
        }
        mod_instances.Freeze();
    }

    /**
     * Initializes the specified mod instance on the client side of Minecraft. <br />
     * Only call this when you are initializing in a usual Minecraft client.
     * @param instance The mod instance to initialize.
     * @param mod_object The mod object, provided by the mod loader. What will this object be depends on the mod loader that is being used.
     * @throws ArgumentNullException {@code instance} was {@code null}.
     * @throws ModInitializationException The mod instance passed failed to be initialized. Check error log for more information.
     */
    public static void InitializeClientSideMod(IClientModInstance instance, Object mod_object)
            throws ArgumentNullException, ModInitializationException
    {
        ArgumentNullException.ThrowIfNull(instance, "instance");
        ArgumentNullException.ThrowIfNull(mod_object, "mod_object");
        Stopwatch sw = Stopwatch.StartNew();
        String mod_id = null;
        try {
            mod_id = instance.GetModId();
            instance.Initialize();

            while (!initialized) { Thread.onSpinWait(); } // Wait until the library is fully initialized.

            if (layer == null) {
                // This indicates a failure or a bug in the library. The mod instance will be ignored completely.
                BaseModsLib.LOGGER.info("Critical library bug realized, cowardly refusing to continue initialization!");
                return;
            }

            instance.SetupConfigurationFiles(ConfigManager.INSTANCE);

            // Initialize event handling - may be needed so early to assure that all events will be properly fired later.
            instance.RegisterEvents(BaseModsLib.GetEventsManager());

            // After the events have been initialized, initialize everything required for the proxy objects.
            instance.RegisterProxyObjects(BaseModsLib.GetProxyManager());

            layer.InitializeClientModInstance(instance, mod_object);

            var config_screen = instance.RegisterConfigurationScreenFactory();

            if (config_screen != null)
            {
                try (var ctx = config_factories.Acquire())
                {
                    ctx.GetValue().Register(new Pair<>(mod_id, config_screen));
                }
            }

            instance.OnInitializeEnd();

            sw.Stop();

            BaseModsLib.LOGGER.info("BASEMODSLIB: Client mod instance with ID {} initialized successfully after {} seconds." , instance.GetModId() , sw.GetElapsed().GetTotalSeconds());

            mod_instances.Add(instance);
        } catch (Throwable th) {
            sw.Stop();
            BaseModsLib.LOGGER.info("BASEMODSLIB: Mod instance with ID {} failed after {} seconds." , mod_id, sw.GetElapsed().GetTotalSeconds());
            BaseModsLib.LOGGER.error("BASEMODSLIB: Cannot initialize client-side mod id {}!\nRethrowing the exception to the underlying mod.", mod_id);
            Exception e = BaseModsLib.TranslateException(th);
            if (e == null) {
                throw th;
            } else {
                throw new ModInitializationException(mod_id, e);
            }
        }
    }

    /**
     * Gets the player that has initialized this Minecraft instance.
     * @return The {@link Player} that has created this particular Minecraft instance.
     */
    @Pure
    @MaybeNull
    public static Player GetLoggedInPlayer() { return Minecraft.getInstance().player; }

    /**
     * Gets an enumerable implementation that enumerates through the available configuration screens detected by the library.
     * @return An enumerable implementation containing configuration screen factories.
     */
    @Pure
    @NotNull
    public static IEnumerable<Pair<String, ConfigurationScreenFactory<?>>> GetConfigurationScreens()
    {
        if (config_factories.IsNull()) {
            return new EmptyEnumerable<>();
        } else {
            try (var ctx = config_factories.Acquire())
            {
                return ctx.GetValue();
            }
        }
    }

    /**
     * Returns a list of all the client-side BML mods currently discovered and registered by the BML.
     * @return The {@link ClientModInstanceCollection} containing all the discovered mods.
     * @since 1.0.37
     */
    @Pure
    @NotNull
    @MixinUnsafe
    @MaybeNullInMixin
    public static ClientModInstanceCollection GetModInstances() { return mod_instances; }

    /**
     * Gets the associated {@link IClientModInstance} for the specified mod with the specified ID. <br />
     * This is provided because a mod can provide multiple sub-mods that need to be interconnected, or
     * for accessing API for an external mod that is provided through it's {@link IClientModInstance}. <br />
     * This method will return {@code null} if the mod exists but is not registered with BML, and will return {@code null} if the specified mod is not loaded at all.
     * @param mod_id The ID of the mod to retrieve it's {@link IClientModInstance} declaration.
     * @return The declared instance of the mod with the specified ID, or {@code null} if the mod does not exist.
     * @throws ArgumentNullException {@code mod_id} is {@code null}.
     * @since 1.0.13
     */
    @MaybeNull
    public static IClientModInstance GetBMLModInstance(String mod_id)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mod_id, "mod_id");
        if (mod_id.isBlank()) { return null; }
        ByRefParameter<IClientModInstance> instance = new ByRefParameter<>();
        instance.Value = null;
        mod_instances.TryGetValue(mod_id, instance);
        return instance.Value;
    }

    /**
     * Called by Minecraft when it shuts down, do not call this by your code!!
     */
    @ApiStatus.Internal
    public static void DestroySelf()
    {
        // Sync on the BML class object, so that disposal timing in both BML server/client instances is serialized.
        synchronized (BaseModsLib.class)
        {
            BaseModsLib.LOGGER.info("Destroying mdcdi1315's Base Mods Library of client distribution side.");
            if (layer != null)
            {
                IClientModInstance mi;
                try (IEnumerator<IClientModInstance> i = mod_instances.GetEnumerator())
                {
                    // Invoke to all mod instances the Dispose method.
                    while (i.MoveNext())
                    {
                        mi = i.getCurrent();
                        try {
                            mi.Dispose();
                        } catch (Exception e) {
                            BaseModsLib.LOGGER.error("BASEMODSLIB: Cannot dispose mod with ID {} due to an exception: {}" , mi.GetModId() , e);
                        }
                    }
                } catch (Exception e) {
                    BaseModsLib.LOGGER.error("BASEMODSLIB: Cannot run disposer due to an underlying exception." , e);
                }
                layer.Dispose();
            }
            layer = null;
            mod_instances = null;
            config_factories.SetValue(null);
            ClothConfigIntegrationHandler.Destroy();
        }
    }
}
