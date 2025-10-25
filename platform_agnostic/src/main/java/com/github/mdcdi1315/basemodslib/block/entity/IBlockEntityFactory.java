package com.github.mdcdi1315.basemodslib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IBlockEntityFactory<T extends BlockEntity>
{
    T Create(BlockPos position, BlockState associated_state);

    Block[] GetBlocks();
}
