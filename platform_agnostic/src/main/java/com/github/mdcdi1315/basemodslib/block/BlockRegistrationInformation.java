package com.github.mdcdi1315.basemodslib.block;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Func3;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.resources.ResourceLocation;

/**
 * Defines registration information on blocks. <br />
 * It provides two functions, one is for loading the block itself,
 * and the other is for registering the block as an item too. <br />
 * @param block_getter Defines the function that gets the block to register.
 * @param item_for_block_getter Defines the block item to register for the newly created block instance.
 * @param creative_mode_tabs_for_item Defines the creative mode tabs to register the newly created item. The item mapping function must be valid and be defined for this to work.
 */
public record BlockRegistrationInformation(
        Func2<ResourceLocation, Block> block_getter,
        @MaybeNull Func3<Block, ResourceLocation , Item> item_for_block_getter,
        CreativeModeTab... creative_mode_tabs_for_item
) {
    /**
     * Creates a new block registration information instance from the specified function that gets the block,
     * the function that is registering an associated item for the in question block, and the creative mode tabs
     * to register the block to.
     * @param block_getter The function that upon invoking, it gets the block to be registered.
     * @param item_for_block_getter The function that upon invoking, it gets the associated item for the block. It is the representation of the block in inventories.
     * @param creative_mode_tabs_for_item Defines the creative mode tabs that the block will be placed under. This requires that the item supplying function {@code item_for_block_getter} has been defined.
     * @throws ArgumentNullException {@code block_getter} and/or {@code item_for_block_getter} and/or {@code creative_mode_tabs_for_item} are {@code null}.
     */
    public BlockRegistrationInformation {
        ArgumentNullException.ThrowIfNull(block_getter, "block_getter");
    }

    /**
     * Creates a new block registration information instance from the specified function that gets the block.
     * @param block_getter The function that upon invoking, it gets the block to be registered.
     * @throws ArgumentNullException {@code block_getter} is {@code null}.
     */
    public BlockRegistrationInformation(Func2<ResourceLocation, Block> block_getter)
        throws ArgumentNullException
    {
        this(block_getter, null, new CreativeModeTab[0]);
    }
}
