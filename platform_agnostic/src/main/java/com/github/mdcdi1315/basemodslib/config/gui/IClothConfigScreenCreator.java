package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.RequiresDynamicCode;
import com.github.mdcdi1315.basemodslib.config.IModConfig;

import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.InvocationTargetException;

/**
 * Provides an interface to instantiate a Cloth Config API screen. <br />
 * This is implicitly implemented by the Cloth Config API integration.
 * @since 1.0.18
 */
public interface IClothConfigScreenCreator
{
    /**
     * Creates an instance of the Cloth Config API screen and returns it cast to a Minecraft Screen.
     * @param parent The parent Minecraft screen to return to.
     * @return The {@link Screen} object to be later shown to the user as this is the constructed Cloth Config API screen.
     */
    Screen Create(@AllowNull Screen parent);

    /**
     * Creates a new instance of the {@link IClothConfigScreenCreator} interface.
     * @param config The {@link IModConfig} instance that is used to modify the configuration values.
     * @return A new instance of the {@link IClothConfigScreenCreator} interface.
     */
    @RequiresDynamicCode(Message = "Cloth Config API may be missing or it's creation signature may have been modified")
    static IClothConfigScreenCreator CreateInstance(IModConfig config)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
        return (IClothConfigScreenCreator) (Class.forName("com.github.mdcdi1315.basemodslib.integration.clothconfig.ClothConfigScreenCreator").getConstructor(IModConfig.class)).newInstance(config);
    }
}
