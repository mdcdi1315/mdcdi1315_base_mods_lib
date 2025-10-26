package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.config.ConfigManager;
import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientStartedEvent;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientStoppingEvent;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides services for initializing client-side mods with the Base Mods Library.
 */
public final class BaseModsLibClient
{
    private static IClientModLoaderLayer layer;
    private static List<IClientModInstance> mod_instances;

    static {
        layer = null;
        mod_instances = null;
        if (BaseModsLib.GetEnvironment() != ModdingEnvironment.CLIENT) {
            // Perpetually crash Minecraft to avoid having weird crash reports due to class loading.
            // We will have a crash report anyway; just this will save some time finding bugs...
            throw new InvalidOperationException("Base mods library client was touched but it should not!");
        }
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
        BaseModsLib.GetEventsManager().AddEvent(ClientStartedEvent.class);
        BaseModsLib.GetEventsManager().AddEvent(ClientStoppingEvent.class);
    }

    /**
     * Initializes the specified mod instance on the client side of Minecraft. <br />
     * Only call this when you are initializing in a usual Minecraft client.
     * @param instance The mod instance to initialize.
     * @throws ArgumentNullException {@code instance} was {@code null}.
     */
    public static void InitializeClientSideMod(IClientModInstance instance, Object mod_object)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(instance, "instance");
        ArgumentNullException.ThrowIfNull(mod_object, "mod_object");
        try {
            instance.Initialize();

            instance.SetupConfigurationFiles(ConfigManager.INSTANCE);

            layer.InitializeClientModInstance(instance, mod_object);

            instance.OnInitializeEnd();

            mod_instances.Add(instance); // The instance is made known to other mods after the mod has completed initialization.
        } catch (Exception e) {
            var id = instance.GetModId();
            BaseModsLib.LOGGER.error("BASEMODSLIB: Cannot initialize client-side mod id {}!\nRethrowing the exception to the underlying mod." , id);
            throw new ModInitializationException(id, e);
        }
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
        layer = null;
    }
}
