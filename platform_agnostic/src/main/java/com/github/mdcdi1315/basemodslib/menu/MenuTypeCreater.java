package com.github.mdcdi1315.basemodslib.menu;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Provides a safe, documented, and public way to register to provide a container menu supplier.
 * @param <T> The type of the container menu to be returned.
 * @since 1.0.6
 */
@FunctionalInterface
public interface MenuTypeCreater<T extends AbstractContainerMenu>
{
    /**
     * Creates a new container menu instance.
     * @param container_id An incrementing number indicating the opened menu.
     * @param inventory The inventory of the player to additionally show, if required to by the menu type.
     * @return The created container menu object.
     */
    @NotNull
    T Create(int container_id, Inventory inventory);
}
