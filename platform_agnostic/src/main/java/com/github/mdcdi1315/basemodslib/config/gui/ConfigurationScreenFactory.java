package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.config.IModConfig;

import net.minecraft.client.gui.screens.Screen;

/**
 * Provides a factory class for creating screens for your configuration files.
 * @param <TS> The derived {@link Screen} object to create.
 */
public abstract class ConfigurationScreenFactory<TS extends Screen>
{
    private IModConfig config_data;

    /**
     * Initializes a new and empty instance of the {@link ConfigurationScreenFactory} class.
     */
    public ConfigurationScreenFactory() { config_data = null; }

    /**
     * Create a new instance of the specified screen, and returns it. <br />
     * A parameter does also specify the parent screen for the created screen.
     * @param parent The parent screen to use.
     * @return The created screen object.
     */
    public abstract TS Create(@DisallowNull Screen parent);

    /**
     * Gets the currently bounded configuration data to this screen factory.
     * @return The bounded config file.
     * @since 1.0.26
     */
    @NotNull
    public final IModConfig GetConfigData()
    {
        if (config_data == null) {
            throw new InvalidOperationException("Cannot get the configuration data while not those have been assigned!");
        } else {
            return config_data;
        }
    }

    /**
     * Sets new configuration data to this screen factory.
     * @param config The configuration data to assign.
     * @throws ArgumentNullException {@code config} is {@code null}.
     */
    public final void SetConfigData(IModConfig config)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(config, "config");
        config_data = config;
    }
}
