package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.RequiresDynamicCode;

import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.integration.clothconfig.ClothConfigScreenCreator;

import net.minecraft.client.gui.screens.Screen;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides an interface to instantiate a Cloth Config API screen. <br />
 * This is implicitly implemented by the Cloth Config API integration.
 * @since 1.0.18
 * @apiNote This API is subject to break and/or change in next versions. <br />
 * This is only implicitly implemented internally and mods should not depend on this. <br />
 * For a stable communication interface, see the {@link DefaultConfigurationScreenFactory} class.
 */
@ApiStatus.Internal
public interface IClothConfigScreenCreator
{
    /**
     * Creates an instance of the Cloth Config API screen and returns it cast to a Minecraft Screen.
     * @param parent The parent Minecraft screen to return to.
     * @param config The configuration file to open the screen for.
     * @return The {@link Screen} object to be later shown to the user as this is the constructed Cloth Config API screen.
     */
    Screen Create(@AllowNull Screen parent, @DisallowNull IModConfig config);

    /**
     * Adds a custom config value handler for type {@link T}.
     * @param handler The configuration value handler. Must be an instance of the {@link IClothConfigTransformableValue} interface.
     * @param <T> The type of the actual value for which a handler is registered.
     * @throws ArgumentNullException {@code handler} is {@code null}.
     * @throws InvalidOperationException A handler has been already specified for the specified type.
     * @since 1.0.26
     */
    <T> void AddCustomConfigValueHandler(IClothConfigTransformableValue<T> handler) throws ArgumentNullException, InvalidOperationException;

    /**
     * Creates a new instance of the {@link IClothConfigScreenCreator} interface.
     * @return A new instance of the {@link IClothConfigScreenCreator} interface.
     */
    @RequiresDynamicCode(Message = "Cloth Config API may be missing or it's creation signature may have been modified")
    static IClothConfigScreenCreator CreateInstance() throws NoClassDefFoundError { return new ClothConfigScreenCreator(); }
}
