package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Func1;

import net.minecraft.world.level.block.Block;
import net.minecraft.client.color.block.BlockColor;

public record BlockColorHandlerRegistrationInfo(
        Func1<Block[]> blocks,
        BlockColor block_color
) {
    public BlockColorHandlerRegistrationInfo {
        ArgumentNullException.ThrowIfNull(blocks, "blocks");
        ArgumentNullException.ThrowIfNull(block_color, "block_color");
    }
}
