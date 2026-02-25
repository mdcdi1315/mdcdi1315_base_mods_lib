package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.client.gui.InformationalDialogScreen;

import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.InvocationTargetException;

/**
 * Provides a default class implementation of the {@link ConfigurationScreenFactory} class by using as a screen the {@link DefaultConfigurationScreen} class.
 * @param <TCFG> The class type of the configuration file that this factory instance will manipulate.
 */
public final class DefaultConfigurationScreenFactory<TCFG extends IModConfig>
    extends ConfigurationScreenFactory<Screen>
{
    private TCFG config;

    public DefaultConfigurationScreenFactory(TCFG config, String mod_id)
    {
        ArgumentNullException.ThrowIfNull(config, "config");
        this.config = config;
    }

    @Override
    public Screen Create(Screen parent)
    {
        if (BaseModsLib.IsModLoaded("cloth_config")) {
            return InstantiateClothConfig(parent);
        } else {
            return new InformationalDialogScreen(
                    new String[] {
                            Component.translatable("mdcdi1315_base_mods_lib.config.no_cloth_config_api.line_1").getString(),
                            Component.translatable("mdcdi1315_base_mods_lib.config.no_cloth_config_api.line_2").getString()
                    },
                    parent
            );
        }
    }

    private Screen InstantiateClothConfig(Screen parent)
    {
        InformationalDialogScreen ids;
        try {
            return IClothConfigScreenCreator.CreateInstance(config).Create(parent);
        } catch (InstantiationException e) {
            ids = new InformationalDialogScreen(new String[] { "Cannot instantiate Cloth Config: ", e.toString() }, parent);
        } catch (IllegalAccessException e) {
            ids = new InformationalDialogScreen(new String[] { "Cannot access the cloth config constructor: ", e.toString() }, parent);
        } catch (InvocationTargetException e) {
            ids = new InformationalDialogScreen(new String[] { "Cloth config constructor threw an exception: ", e.getTargetException().toString() }, parent);
        } catch (NoSuchMethodException e) {
            ids = new InformationalDialogScreen(new String[] { "Cloth config constructor could not be found: ", e.toString() }, parent);
        } catch (ClassNotFoundException e) {
            ids = new InformationalDialogScreen(new String[] {
                    "Cloth config layer class could not be found.",
                    "This may suggest that the mod itself is broken.",
                    "Please report this issue to mdcdi1315."
            }, parent);
        }
        return ids;
    }

    public TCFG GetConfigData() { return config; }

    public void SetConfigData(TCFG config) {
        ArgumentNullException.ThrowIfNull(config, "config");
        this.config = config;
    }
}
