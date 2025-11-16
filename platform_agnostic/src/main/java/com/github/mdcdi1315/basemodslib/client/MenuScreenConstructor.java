package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.client.gui.screens.inventory.MenuAccess;

/**
 * Provides the means for creating a screen for a container menu.
 * @param <M> The type of the container menu that is created.
 * @param <U> The {@link Screen} representing the container menu of type {@link M}.
 */
@FunctionalInterface
public interface MenuScreenConstructor<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>>
{
    /**
     * Creates a new {@link Screen} of type {@link U} for the passed in container menu.
     * @param menu The container menu to create the screen from.
     * @param inventory The player's inventory to use.
     * @param title The title of the menu.
     * @return The created {@link Screen}.
     */
    @NotNull
    U Create(@DisallowNull M menu, @DisallowNull Inventory inventory, @DisallowNull Component title);
}
