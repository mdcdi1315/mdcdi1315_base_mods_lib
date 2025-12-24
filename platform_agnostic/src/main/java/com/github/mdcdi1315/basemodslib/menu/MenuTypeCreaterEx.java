package com.github.mdcdi1315.basemodslib.menu;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Complements the {@link MenuProviderEx} interface by reading back the screen opening data written through the {@link MenuProviderEx#WriteScreenOpeningData(ServerPlayer, FriendlyByteBuf)} method. <br />
 * This was provided now because there was not found a way to implement this properly.
 * @param <T> The type of the container menu to create.
 * @since 1.0.13
 */
@FunctionalInterface
public interface MenuTypeCreaterEx<T extends AbstractContainerMenu>
    extends MenuTypeCreater<T>
{
    @NotNull
    @Override
    default T Create(int container_id, Inventory inventory) { return Create(container_id , inventory , null); }

    /**
     * Creates a new container menu instance.
     * @param container_id An incrementing number indicating the opened menu.
     * @param buffer The network buffer containing the additional data provided through the {@link MenuProviderEx#WriteScreenOpeningData(ServerPlayer, FriendlyByteBuf)} method. Can be {@code null} if the opening packet could not be realized.
     * @param inventory The inventory of the player to additionally show, if required to by the menu type.
     * @return The created container menu object.
     */
    @NotNull
    T Create(int container_id, Inventory inventory, @AllowNull FriendlyByteBuf buffer);
}
