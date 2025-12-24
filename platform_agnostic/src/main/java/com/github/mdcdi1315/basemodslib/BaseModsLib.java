package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Stopwatch;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.eventapi.*;
import com.github.mdcdi1315.basemodslib.config.ConfigManager;
import com.github.mdcdi1315.basemodslib.mods.proxy.ProxyManager;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.eventapi.mods.ModLoadingCompleteEvent;

import org.jetbrains.annotations.ApiStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.lang.Exception;

/**
 * The main class for accessing the base mods library API. <br />
 * From this class you initialize your multiplatform mods.
 */
public final class BaseModsLib
{
    /**
     * The ID of the library. <br />
     * This is used for the mod loader and represents the library as a mod to it.
     * @since 1.0.5
     */
    public static final String MOD_ID = "mdcdi1315_base_mods_lib";

    private static IModLoaderLayer layer;
    private static ProxyManager proxy_manager;
    private static EventManager events_manager;
    private static volatile boolean initialized;
    private static List<IServerModInstance> mod_instances;

    public static Logger LOGGER;

    static {
        layer = null;
        initialized = false;
        proxy_manager = null; // Initialized once the layer is ready.
        events_manager = new EarlyEventsManager();
        LOGGER = LoggerFactory.getLogger("mdcdi1315's Base Mods Lib logger");
        LOGGER.info("Now initializing mdcdi1315's Base Mods Library!!!");
    }

    // Do not let anyone instantiate this class.
    private BaseModsLib() {}

    /**
     * Initializes the Base Mods library generically. <br />
     * Remarks: <br />
     * Although that this is documented, this API should not be called by consumers of the library but only by the library itself. <br />
     * As the modding needs are evolving, the method requirements, and it's signature are subject to change without notice.
     * @param mod_loader_layer_constructor The mod loader layer method reference to use for initializing the library components.
     * @throws ArgumentNullException {@code layer} was {@code null}.
     * @throws InvalidOperationException The library has been successfully initialized before.
     * @throws CriticalLibraryInitializationException A critical initialization error has been realized by the library. Execution cannot continue.
     */
    @ApiStatus.Internal
    public static void InitializeBaseModsLibrary(Func1<IModLoaderLayer> mod_loader_layer_constructor)
            throws ArgumentNullException, InvalidOperationException, CriticalLibraryInitializationException
    {
        ArgumentNullException.ThrowIfNull(mod_loader_layer_constructor, "mod_loader_layer_constructor");
        if (layer != null) {
            throw new InvalidOperationException("The base mods library has already been initialized successfully.");
        }
        LOGGER.info("Initializing mdcdi1315's Base Mods Library...");
        Stopwatch sw = Stopwatch.StartNew();
        try {
            layer = mod_loader_layer_constructor.function();
            if (layer == null) {
                throw new InvalidOperationException("Returned an empty mod loader layer through the mod loader layer constructor. This is unexpected.");
            }
            boolean dev_env = layer.IsDevelopmentEnvironmentBuild();
            if (dev_env) {
                LOGGER.info("Detected a development environment instance. Library will enter the dev env mode.");
            }
            // OK. Now hand out everything defined from the early events manager to the normal events manager (Or to the debug one if running on dev env)
            LOGGER.debug("Handing out registered events from early initialization to the normal events manager.");
            if (dev_env) {
                DebugEventsManager dem = new DebugEventsManager();
                dem.HandEventsFromEarly((EarlyEventsManager) events_manager);
                events_manager = dem;
            } else {
                NormalEventsManager nem = new NormalEventsManager();
                nem.HandEventsFromEarly((EarlyEventsManager) events_manager);
                events_manager = nem;
            }
            LOGGER.debug("Hand out completed.");
            mod_instances = new List<>();
            proxy_manager = new ProxyManager();
            LOGGER.info("mdcdi1315's Base Mods Library initialized on {} mod loader of version {}, with Minecraft version {} and distribution type {}.", layer.GetModLoaderBranding(), layer.GetModLoaderVersion(), layer.GetMinecraftVersion(), layer.GetEnvironment());
            sw.Stop();
            LOGGER.info("The library took {} seconds to initialize.", sw.GetElapsed().GetTotalSeconds());
        } catch (Exception e) {
            layer = null;
            initialized = true;
            sw.Stop();
            LOGGER.error("Library failed to be initialized after {} seconds! Inspecting exception and throwing back." , sw.GetElapsed().GetTotalSeconds());
            throw new CriticalLibraryInitializationException(e);
        }
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
     * @throws ModInitializationException The mod instance passed failed to be initialized. Check error log for more information.
     */
    public static void InitializeServerSideMod(IServerModInstance instance, Object mod_object)
            throws ArgumentNullException, ModInitializationException
    {
        ArgumentNullException.ThrowIfNull(instance, "instance");
        ArgumentNullException.ThrowIfNull(mod_object, "mod_object");
        Stopwatch sw = Stopwatch.StartNew();
        try {
            instance.Initialize();

            while (!initialized) { Thread.onSpinWait(); } // Wait until the library is fully initialized.

            if (layer == null) {
                // This indicates a failure or a bug in the library. The mod instance will be ignored completely.
                BaseModsLib.LOGGER.info("Critical library bug realized, cowardly refusing to continue initialization!");
                return;
            }

            instance.SetupConfigurationFiles(ConfigManager.INSTANCE);

            // Initialize event handling - may be needed so early to assure that all events will be properly fired later.
            instance.RegisterEvents(events_manager);

            // After the events have been initialized, initialize everything required for the proxy objects.
            instance.RegisterProxyObjects(proxy_manager);

            layer.InitializeServerModInstance(instance, mod_object);

            instance.OnInitializeEnd();

            sw.Stop();

            LOGGER.info("BASEMODSLIB: Server mod instance with ID {} initialized successfully after {} seconds." , instance.GetModId() , sw.GetElapsed().GetTotalSeconds());

            synchronized (mod_instances) {
                mod_instances.Add(instance); // The instance is made known to other mods after the mod has completed initialization.
            }
        } catch (Exception e) {
            var id = instance.GetModId();
            sw.Stop();
            LOGGER.info("BASEMODSLIB: Mod instance with ID {} failed after {} seconds." , id, sw.GetElapsed().GetTotalSeconds());
            LOGGER.error("BASEMODSLIB: Cannot initialize server-side mod id {}!\nRethrowing the exception to the underlying mod." , id);
            throw new ModInitializationException(id, e);
        }
    }

    /**
     * Gets the event manager for mods. <br />
     * Note: Do not attempt to access this on early time. <br />
     * If you do that, you risk losing your event's registration. <br />
     * Instead, wait until the {@link com.github.mdcdi1315.basemodslib.mods.IModInstance#RegisterEvents(EventManager)} method is called to your mod instance.
     * @return The event manager.
     */
    @NotNull
    public static EventManager GetEventsManager() { return events_manager; }

    /**
     * Gets the proxy manager for mods that use this. <br />
     * Note: Do not attempt to access this on early time. <br />
     * This is only valid once the library itself has been successfully initialized. <br />
     * Instead, wait until the {@link com.github.mdcdi1315.basemodslib.mods.IModInstance#RegisterProxyObjects(ProxyManager)} method is called to your mod instance.
     * @return The proxy manager to be used by mods that use this API.
     */
    @MaybeNull
    public static ProxyManager GetProxyManager() { return proxy_manager; }

    /**
     * Gets an enumerable of mod instances currently registered.
     * @return The registered mod instances.
     */
    @NotNull
    public static IEnumerable<IServerModInstance> GetModInstances() { return mod_instances; }

    /**
     * Gets the associated {@link IServerModInstance} for the specified mod with the specified ID. <br />
     * This is provided because a mod can provide multiple sub-mods that need to be interconnected, or
     * for accessing API for an external mod that is provided through it's {@link IServerModInstance}. <br />
     * This method will return {@code null} if the mod exists but is not registered with BML, and will return {@code null} if the specified mod is not loaded at all.
     * @param mod_id The ID of the mod to retrieve it's {@link IServerModInstance} declaration.
     * @return The declared instance of the mod with the specified ID, or {@code null} if the mod does not exist.
     * @throws ArgumentNullException {@code mod_id} is {@code null}.
     * @since 1.0.13
     */
    @MaybeNull
    public static IServerModInstance GetBMLModInstance(String mod_id)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mod_id);
        if (mod_id.isBlank()) { return null; }
        IServerModInstance smi;
        IEnumerator<IServerModInstance> en = mod_instances.GetEnumerator();
        try {
            while (en.MoveNext()) {
                smi = en.getCurrent();
                if (smi.GetModId().equals(mod_id)) {
                    return smi;
                }
            }
            return null;
        } finally {
            en.Dispose();
        }
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
    public static java.util.List<String> GetLoadedMods() { return layer.GetLoadedMods(); }

    /**
     * Gets the modding environment under which the library itself runs.
     * @return The modding environment that the library is running into.
     */
    @NotNull
    public static ModdingEnvironment GetEnvironment() { return layer.GetEnvironment(); }

    /**
     * Gets the branding of the underlying mod loader where the library is initialized to. <br />
     * Commonly, it is the name of the mod loader.
     * @return The mod loader branding.
     */
    @NotNull
    public static String GetModLoaderBranding() { return layer.GetModLoaderBranding(); }

    /**
     * Gets the value returned by the {@link #GetModLoaderBranding()} method as one of the constants defined in the {@link CommonModLoaderBranding} enumeration.
     * @return The mod loader branding constant mapped by the result of invoking the {@link #GetModLoaderBranding()} method.
     * @throws FormatException The mod loader branding returned by the {@link #GetModLoaderBranding()} method could not be mapped to one of the common mod loader constants.
     * @since 1.0.11
     */
    public static CommonModLoaderBranding GetCommonModLoaderBranding()
        throws FormatException
    {
        return CommonModLoaderBranding.Parse(layer.GetModLoaderBranding());
    }

    /**
     * Gets the version of the underlying mod loader where the library is initialized to. <br />
     * @return The mod loader version.
     */
    @NotNull
    public static Version GetModLoaderVersion() { return layer.GetModLoaderVersion(); }

    /**
     * Gets the Minecraft version under which the mod loader runs.
     * @return The Minecraft version.
     */
    @NotNull
    public static Version GetMinecraftVersion() { return layer.GetMinecraftVersion(); }

    /**
     * Gets the directory path where all the mod configuration files are stored.
     * @return The configuration directory.
     */
    @NotNull
    public static Path GetModConfigurationDirectory() { return layer.GetConfigurationDirectory(); }

    /**
     * Gets the directory where Minecraft is running. <br />
     * This is commonly referred to as the 'game working directory'.
     * @return The directory where the game runs from.
     * @since 1.0.5
     */
    @NotNull
    public static Path GetMinecraftDirectory() { return layer.GetMinecraftDirectory(); }

    /**
     * Gets a value whether this Minecraft build originates from a development environment.
     * @return A value whether the current instance originates as a result of running from a development environment.
     * @since 1.0.11
     */
    // Check for non-null before calling, because it may be called by the mods too early and will cause them to fail.
    public static boolean IsDevelopmentEnvironment() { return layer != null && layer.IsDevelopmentEnvironmentBuild(); }

    /**
     * Called by the mod loader when mod loading is complete. <br />
     * Destroys data structures used by the library.
     */
    @ApiStatus.Internal
    public static void Destroy() {
        LOGGER.info("Mod loading complete. Dispatching mod loading complete event to implementing mods.");
        events_manager.FireEvent(new ModLoadingCompleteEvent());
        events_manager.DestroyDestroyableEvents();
        if (!proxy_manager.Lock()) {
            BaseModsLib.LOGGER.info("BASEMODSLIB: Proxy manager is not used by any mods, deallocating it.");
            proxy_manager.Dispose();
            proxy_manager = null;
        }
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
        if (events_manager != null) {
            events_manager.DestroyManager();
            events_manager = null;
        }
        if (proxy_manager != null) {
            proxy_manager.Dispose();
            proxy_manager = null;
        }
        if (layer != null) {
            layer.Dispose();
            layer = null;
        }
    }
}
