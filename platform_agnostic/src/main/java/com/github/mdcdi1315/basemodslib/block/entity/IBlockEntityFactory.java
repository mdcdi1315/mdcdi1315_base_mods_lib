package com.github.mdcdi1315.basemodslib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Provides the way for creating block entity instances to Minecraft.
 * @param <T> The type of the block entity to create.
 */
public interface IBlockEntityFactory<T extends BlockEntity>
{
    /**
     * Creates a new block entity instance at the specified position.
     * @param position The exact world coordinates to create this block entity to.
     * @param associated_state The block state to associate this block entity with.
     * @return The created block entity instance.
     */
    T Create(BlockPos position, BlockState associated_state);

    /**
     * Gets the blocks that this block entity is allowed to be associated with.
     * @return The blocks that the block entity is allowed to be associated with.
     */
    Block[] GetBlocks();
}
