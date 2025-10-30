package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.config.IModConfig;

import net.minecraft.client.gui.screens.Screen;

/**
 * Provides a default class implementation of the {@link ConfigurationScreenFactory} class by using as a screen the {@link DefaultConfigurationScreen} class.
 * @param <TCFG> The class type of the configuration file that this factory instance will manipulate.
 */
public final class DefaultConfigurationScreenFactory<TCFG extends IModConfig>
    extends ConfigurationScreenFactory<DefaultConfigurationScreen<TCFG>>
{
    private TCFG config;
    private final String mod_id;

    public DefaultConfigurationScreenFactory(TCFG config, String mod_id)
    {
        ArgumentNullException.ThrowIfNull(config, "config");
        ArgumentNullException.ThrowIfNullOrEmpty(mod_id, "mod_id");
        this.config = config;
        this.mod_id = mod_id;
    }

    @Override
    public DefaultConfigurationScreen<TCFG> Create(Screen parent) {
        return new DefaultConfigurationScreen<>(config, mod_id, parent);
    }

    public TCFG GetConfigData() {
        return config;
    }

    public void SetConfigData(TCFG config) {
        ArgumentNullException.ThrowIfNull(config, "config");
        this.config = config;
    }
}
