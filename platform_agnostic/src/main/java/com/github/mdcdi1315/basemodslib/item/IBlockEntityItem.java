package com.github.mdcdi1315.basemodslib.item;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Special interface for items that are block entities. <br />
 * This is provided because items associated with block entities are rendered by their block renderers. <br />
 * This should be implemented on the {@code BlockItem} class to override the Minecraft renderer so that your item can be in fact rendered. <br />
 * Minecraft then calls in the renderer you have registered with the passed block entity through the {@code BlockEntityWithoutLevelRenderer} class. <br />
 * Although that this interface is useful only in client environments, it is not defined as client-only so that you can define it in server distributions (thus, everywhere). <br /> <br />
 * Remarks: To better clarify, you need to derive from the {@code BlockItem} class so that the stated overriding can work.
 * @since 1.0.8
 */
public interface IBlockEntityItem
{
    /**
     * Gets the block entity to render.
     * @return The block entity to render. Must be non-{@code null} and should not throw any exceptions as this is called from the rendering loop.
     */
    @NotNull
    BlockEntity GetBlockEntity();
}
