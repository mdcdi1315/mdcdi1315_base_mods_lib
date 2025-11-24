package com.github.mdcdi1315.basemodslib.mods;

import com.github.mdcdi1315.DotNetLayer.System.IDisposable;
import com.github.mdcdi1315.basemodslib.config.ConfigManager;
import com.github.mdcdi1315.basemodslib.eventapi.EventManager;
import com.github.mdcdi1315.basemodslib.mods.proxy.ProxyManager;

/**
 * Defines the base interface for mod instances. <br />
 * Every mod instance is expected to be initialized and finally destroyed by using the {@link #Dispose()} method.
 */
public interface IModInstance
    extends IDisposable
{
    /**
     * Initializes the mod instance.
     * Runs initial data not specific somehow to Minecraft itself.
     */
    default void Initialize() {}

    /**
     * Called when mod's initialization has been completed.
     */
    default void OnInitializeEnd() {}

    /**
     * Provides the configuration manager to mod instances to configure their mod configuration files. <br />
     * Override this in your mod instance to set up your configuration files.
     * @param manager The configuration files manager.
     */
    default void SetupConfigurationFiles(ConfigManager manager) {}

    /**
     * Registers events to be listened on the current mod instance.
     * @param manager The events manager object to use.
     */
    default void RegisterEvents(EventManager manager) {}

    /**
     * Registers proxy objects to be used later for the current mod instance.
     * @param manager The proxy manager object to use.
     * @since 1.0.11
     */
    default void RegisterProxyObjects(ProxyManager manager) {}

    /**
     * Gets the current mod id for this mod instance. <br />
     * Required for the base services to register data.
     * @return The mod ID that this instance is currently attributed to.
     */
    String GetModId();
}
