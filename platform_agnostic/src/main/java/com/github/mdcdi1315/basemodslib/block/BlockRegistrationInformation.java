package com.github.mdcdi1315.basemodslib.block;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Func3;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.minecraft.resources.ResourceLocation;

/**
 * Defines registration information on blocks. <br />
 * It provides two functions, one is for loading the block itself,
 * and the other is for registering the block as an item too.
 */
public record BlockRegistrationInformation(
        Func2<ResourceLocation, Block> block_getter,
        @MaybeNull Func3<Block, ResourceLocation , Item> item_for_block_getter
) {
    public BlockRegistrationInformation {
        ArgumentNullException.ThrowIfNull(block_getter, "block_getter");
    }

    public BlockRegistrationInformation(Func2<ResourceLocation, Block> block_getter) {
        this(block_getter, null);
    }
}
