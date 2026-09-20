package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.world.level.block.Block;
import net.minecraft.client.color.block.BlockTintSource;

public record BlockColorHandlerRegistrationInfo(
        Func1<Block[]> blocks,
        BlockTintSource block_color
) {
    public BlockColorHandlerRegistrationInfo {
        ArgumentNullException.ThrowIfNull(blocks, "blocks");
        ArgumentNullException.ThrowIfNull(block_color, "block_color");
    }
}
