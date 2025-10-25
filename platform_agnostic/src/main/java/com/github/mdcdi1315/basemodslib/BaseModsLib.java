package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientStartedEvent;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientStoppingEvent;

import com.github.mdcdi1315.basemodslib.mods.IModInstance;
import com.github.mdcdi1315.basemodslib.eventapi.EventManager;
import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.eventapi.mods.ModLoadingCompleteEvent;

import org.jetbrains.annotations.ApiStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class for accessing the base mods library API. <br />
 * From this class you initialize your multiplatform mods.
 */
public final class BaseModsLib
{
    private static List<IModInstance> mod_instances;
    private static EventManager events_manager;
    private static IModLoaderLayer layer;
    public static Logger LOGGER;

    static {
        layer = null;
        LOGGER = LoggerFactory.getLogger("mdcdi1315's Base Mods Lib logger");
        LOGGER.info("Now initializing mdcdi1315's Base Mods Library!!!");
    }

    private BaseModsLib() {}

    /**
     * Initializes the base mods library.
     * @param mod_loader_layer The mod loader layer to use for initializing the library components.
     * @throws ArgumentNullException {@code layer} was {@code null}.
     * @throws InvalidOperationException The library has been successfully initialized before.
     */
    public static void InitializeBaseModsLibrary(IModLoaderLayer mod_loader_layer)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(mod_loader_layer, "mod_loader_layer");
        if (layer != null) {
            throw new InvalidOperationException("The base mods library has already been initialized successfully.");
        }
        mod_instances = new List<>();
        events_manager = new EventManager();
        layer = mod_loader_layer;
        ModdingEnvironment env = layer.GetEnvironment();
        if (env == ModdingEnvironment.CLIENT) {
            SetupClientBaseModsLibrary();
        }
        LOGGER.info("mdcdi1315's Base Mods Library initialized on {} mod loader of version {}, with Minecraft version {} and distribution type {}.", layer.GetModLoaderBranding(), layer.GetModLoaderVersion() , layer.GetMinecraftVersion() , env);
    }

    private static void SetupClientBaseModsLibrary()
    {
        LOGGER.info("Setting up client initialization data.");
        events_manager.AddEvent(ClientStartedEvent.class);
        events_manager.AddEvent(ClientStoppingEvent.class);
    }

    /**
     * Initializes the specified mod instance on the server side of Minecraft. <br />
     * You should initialize your mod with this and when you are initializing in client side.
     * @param instance The mod instance to initialize.
     * @param mod_object The mod object which will initialize your mod. <br />
     * What this is depends on the mod loader. <br />
     * On (Neo)Forge, it must be the IEventBus object associated with your mod.
     * @throws ArgumentNullException {@code instance} was {@code null}.
     */
    public static void InitializeServerSideMod(IServerModInstance instance, Object mod_object)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(instance, "instance");
        ArgumentNullException.ThrowIfNull(mod_object, "mod_object");
        try {
            instance.Initialize();

            layer.InitializeServerModInstance(instance, mod_object);

            instance.OnInitializeEnd();

            mod_instances.Add(instance); // The instance is made known to other mods after the mod has completed initialization.
        } catch (Exception e) {
            var id = instance.GetModId();
            LOGGER.error("BASEMODSLIB: Cannot initialize server-side mod id {}!\nRethrowing the exception to the underlying mod." , id);
            throw new ModInitializationException(id, e);
        }
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

            layer.InitializeClientModInstance(instance, mod_object);

            instance.OnInitializeEnd();

            mod_instances.Add(instance); // The instance is made known to other mods after the mod has completed initialization.
        } catch (Exception e) {
            var id = instance.GetModId();
            LOGGER.error("BASEMODSLIB: Cannot initialize client-side mod id {}!\nRethrowing the exception to the underlying mod." , id);
            throw new ModInitializationException(id, e);
        }
    }

    /**
     * Gets the event manager for mods.
     * @return The event manager.
     */
    @NotNull
    public static EventManager GetEventsManager()  {
        return events_manager;
    }

    /**
     * Gets an enumerable of mod instances currently registered.
     * @return The registered mod instances.
     */
    @NotNull
    public static IEnumerable<IModInstance> GetModInstances() {
        return mod_instances;
    }

    /**
     * Gets a value whether the mod with the specified ID is loaded in this Minecraft instance.
     * @param mod_id The ID of the mod to query.
     * @return A value whether the specified mod is loaded.
     * @throws ArgumentNullException {@code mod_id} is {@code null}.
     */
    public static boolean IsModLoaded(String mod_id)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mod_id , "mod_id");
        return layer.IsModLoaded(mod_id);
    }

    /**
     * Gets a list of the loaded mods in this Minecraft instance.
     * @return A list of the loaded mods in this Minecraft instance.
     */
    @NotNull
    public static java.util.List<String> GetLoadedMods() {
        return layer.GetLoadedMods();
    }

    /**
     * Gets the branding of the underlying mod loader where the library is initialized to. <br />
     * Commonly, it is the name of the mod loader.
     * @return The mod loader branding.
     */
    @NotNull
    public static String GetModLoaderBranding() {
        return layer.GetModLoaderBranding();
    }

    /**
     * Gets the version of the underlying mod loader where the library is initialized to. <br />
     * @return The mod loader version.
     */
    @NotNull
    public static Version GetModLoaderVersion() {
        return layer.GetModLoaderVersion();
    }

    /**
     * Gets the Minecraft version under which the mod loader runs.
     * @return The Minecraft version.
     */
    @NotNull
    public static Version GetMinecraftVersion() {
        return layer.GetMinecraftVersion();
    }

    /**
     * Called by the mod loader when mod loading is complete. <br />
     * Destroys data structures used by the library.
     */
    @ApiStatus.Internal
    public static void Destroy() {
        LOGGER.info("Mod loading complete. Dispatching mod loading complete event to implementing mods.");
        events_manager.FireEvent(new ModLoadingCompleteEvent());
        events_manager.DestroyDestroyableEvents();
    }

    /**
     * Called by Minecraft when it shuts down, do not call this by your code!!
     */
    @ApiStatus.Internal
    public static void DestroySelf() {
        IModInstance mi;
        IEnumerator<IModInstance> i = null;
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
        // Additional disposal code to be run.
        mod_instances = null;
        events_manager.DestroyManager();
        events_manager = null;
        layer = null;
    }
}
