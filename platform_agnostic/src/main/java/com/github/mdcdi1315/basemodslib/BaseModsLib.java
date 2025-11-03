package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Stopwatch;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.config.ConfigManager;
import com.github.mdcdi1315.basemodslib.eventapi.EventManager;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.eventapi.mods.ModLoadingCompleteEvent;

import org.jetbrains.annotations.ApiStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * The main class for accessing the base mods library API. <br />
 * From this class you initialize your multiplatform mods.
 */
public final class BaseModsLib
{
    private static List<IServerModInstance> mod_instances;
    private static EventManager events_manager;
    private static IModLoaderLayer layer;
    private static volatile boolean initialized;
    public static Logger LOGGER;

    static {
        layer = null;
        initialized = false;
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
        if (events_manager == null) {
            events_manager = new EventManager();
        }
        mod_instances = new List<>();
        layer = mod_loader_layer;
        LOGGER.info("mdcdi1315's Base Mods Library initialized on {} mod loader of version {}, with Minecraft version {} and distribution type {}.", layer.GetModLoaderBranding(), layer.GetModLoaderVersion() , layer.GetMinecraftVersion() , layer.GetEnvironment());
        initialized = true;
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
        Stopwatch sw = Stopwatch.StartNew();
        try {
            instance.Initialize();

            while (!initialized) { Thread.onSpinWait(); } // Wait until the library is fully initialized.

            instance.SetupConfigurationFiles(ConfigManager.INSTANCE);

            // Initialize event handling - may be needed so early to assure that all events will be properly fired later.
            instance.RegisterEvents(GetEventsManager());

            layer.InitializeServerModInstance(instance, mod_object);

            instance.OnInitializeEnd();

            sw.Stop();

            LOGGER.info("BASEMODSLIB: Mod instance with ID {} initialized successfully after {} seconds." , instance.GetModId() , sw.GetElapsed().GetTotalSeconds());

            mod_instances.Add(instance); // The instance is made known to other mods after the mod has completed initialization.
        } catch (Exception e) {
            var id = instance.GetModId();
            sw.Stop();
            LOGGER.info("BASEMODSLIB: Mod instance with ID {} failed after {} seconds." , id, sw.GetElapsed().GetTotalSeconds());
            LOGGER.error("BASEMODSLIB: Cannot initialize server-side mod id {}!\nRethrowing the exception to the underlying mod." , id);
            throw new ModInitializationException(id, e);
        }
    }

    /**
     * Gets the event manager for mods.
     * @return The event manager.
     */
    @NotNull
    public static EventManager GetEventsManager()  {
        if (events_manager == null) {
            events_manager = new EventManager();
        }
        return events_manager;
    }

    /**
     * Gets an enumerable of mod instances currently registered.
     * @return The registered mod instances.
     */
    @NotNull
    public static IEnumerable<IServerModInstance> GetModInstances() {
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
     * Gets the modding environment under which the library itself runs.
     * @return The modding environment that the library is running into.
     */
    @NotNull
    public static ModdingEnvironment GetEnvironment() {
        return layer.GetEnvironment();
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
     * Gets the directory path where all the mod configuration files are stored.
     * @return The configuration directory.
     */
    @NotNull
    public static Path GetModConfigurationDirectory() {
        return layer.GetConfigurationDirectory();
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
        IServerModInstance mi;
        IEnumerator<IServerModInstance> i = null;
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
