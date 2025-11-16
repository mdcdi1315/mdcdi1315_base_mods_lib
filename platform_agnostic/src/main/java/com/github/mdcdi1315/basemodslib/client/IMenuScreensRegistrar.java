package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.client.gui.screens.inventory.MenuAccess;

/**
 * Provides a way to register new menu screens to Minecraft. <br />
 * This is typically provided after Minecraft itself is loaded.
 * @since 1.0.7
 */
public interface IMenuScreensRegistrar
{
    /**
     * Registers a new menu screen for the specified menu type.
     * @param type A function providing the type of the menu to register.
     * @param constructor The menu screen constructor that will be used to create a menu screen of this type.
     * @param <M> The type of the container menu to be created.
     * @param <U> The type of the {@link Screen} that will be created in response of creating a menu of type {@link M}.
     * @throws ArgumentNullException {@code type} and/or {@code constructor} were {@code null}.
     */
    <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void RegisterMenuScreen(Func1<MenuType<? extends M>> type, MenuScreenConstructor<M , U> constructor) throws ArgumentNullException;
}
