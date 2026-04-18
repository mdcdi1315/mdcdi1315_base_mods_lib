package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.client.gui.InformationalDialogScreen;

import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

/**
 * Provides a default class implementation of the {@link ConfigurationScreenFactory} class by using the Cloth Config API as the implementation of the screen.
 * @param <TCFG> The class type of the configuration file that this factory instance will manipulate. (Unused since BML 1.0.26)
 */
public final class DefaultConfigurationScreenFactory<TCFG extends IModConfig>
    extends ConfigurationScreenFactory<Screen>
{
    /**
     * Initializes a new instance of the {@link DefaultConfigurationScreenFactory} class. <br />
     * You will need to initialize the config to display by using the {@link #SetConfigData(IModConfig)} method.
     * @since 1.0.26
     */
    public DefaultConfigurationScreenFactory() { super(); }

    /**
     * Initializes a new instance of the {@link DefaultConfigurationScreenFactory} class.
     * @param config The {@link IModConfig} instance to display on UI.
     * @since 1.0.26
     */
    public DefaultConfigurationScreenFactory(TCFG config)
    {
        super();
        SetConfigData(config);
    }

    /**
     * Initializes a new instance of the {@link DefaultConfigurationScreenFactory} class.
     * @param config The {@link TCFG} instance to initialize the configuration file to load.
     * @param mod_id The ID of the mod. No longer required.
     * @deprecated This constructor alternative was used to support older versions of the mods depended on this mechanism.
     * No longer required.
     * Use instead the {@link #DefaultConfigurationScreenFactory(IModConfig)} constructor.
     */
    @Deprecated(since = "1.0.26", forRemoval = true)
    public DefaultConfigurationScreenFactory(TCFG config, String mod_id) { this(config); }

    @Override
    public Screen Create(Screen parent)
    {
        InformationalDialogScreen ids;
        try {
            return ClothConfigIntegrationHandler.GetCreator().Create(parent, GetConfigData());
        } catch (NoClassDefFoundError e) {
            ids = new InformationalDialogScreen(new String[] {
                    "Cloth config layer class could not be found.",
                    "This may suggest that the Base Mods Library itself is broken.",
                    "Please report this issue to mdcdi1315.",
                    "Report URL: https://github.com/mdcdi1315/mdcdi1315_base_mods_lib/issues"
            }, parent);
        } catch (ClothConfigAPINotSupportedException e) {
            ids = new InformationalDialogScreen(
                    new String[] {
                            Component.translatable("mdcdi1315_base_mods_lib.config.no_cloth_config_api.line_1").getString(),
                            Component.translatable("mdcdi1315_base_mods_lib.config.no_cloth_config_api.line_2").getString()
                    },
                    parent
            );
        } catch (Exception e) {
            ids = new InformationalDialogScreen(new String[] {
                    "Unexpected exception was occurred: ",
                    e.toString()
            }, parent);
        }
        return ids;
    }

    public <T> void AddClothConfigTransformableValue(IClothConfigTransformableValue<T> handler)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(handler, "handler");
        IClothConfigScreenCreator c;
        if ((c = ClothConfigIntegrationHandler.GetCreator()) != null) {
            c.AddCustomConfigValueHandler(handler);
        }
    }
}
