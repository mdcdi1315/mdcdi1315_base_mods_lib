package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import net.minecraft.client.gui.screens.Screen;

/**
 * Provides a factory class for creating screens for your configuration files.
 * @param <TS> The derived {@link Screen} object to create.
 */
public abstract class ConfigurationScreenFactory<TS extends Screen>
{
    /**
     * Create a new instance of the specified screen, and returns it. <br />
     * A parameter does also specify the parent screen for the created screen.
     * @param parent The parent screen to use.
     * @return The created screen object.
     */
    public abstract TS Create(@DisallowNull Screen parent);
}
