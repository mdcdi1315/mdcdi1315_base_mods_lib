package com.github.mdcdi1315.basemodslib.item;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Special interface for items that are block entities. <br />
 * This is provided because items associated with block entities are rendered by their block renderers. <br />
 * This should be implemented on the {@code BlockItem} class to override the Minecraft renderer so that your item can be in fact rendered. <br />
 * Minecraft then calls in the renderer you have registered with the passed block entity through the {@code ItemRenderer} class. <br />
 * Although that this interface is useful only in client environments, it is not defined as client-only so that you can define it in server distributions (thus, everywhere).
 * @since 1.0.8
 */
public interface IBlockEntityItem
{
    /**
     * Gets the block entity to render.
     * @return The block entity to render. Must be non-{@code null} and should not throw any exceptions as this is called from the rendering loop.
     * @deprecated This method has a limited number of options to render. <br />
     * If you need more intricate rendering based on the item stack, use the {@link #GetBlockEntity(ItemStack)} method. <br />
     * This method declaration will remain, however, for binary compatibility. Otherwise, this interface is absent after 1.21.1.
     */
    @NotNull
    @Deprecated(since = "1.0.13")
    default BlockEntity GetBlockEntity() { return null; }

    /**
     * Gets the block entity to render.
     * @param stack The item stack to be rendered.
     * @return The block entity to render. Must be non-{@code null} and should not throw any exceptions as this is called from the rendering loop.
     */
    @NotNull
    default BlockEntity GetBlockEntity(@DisallowNull ItemStack stack) { return GetBlockEntity(); }
}
