package com.github.mdcdi1315.basemodslib.eventapi.mods.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.registries.IModLoaderRegistry;
import com.github.mdcdi1315.basemodslib.eventapi.mods.RegistryFinalizedEvent;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;

/**
 * The event that is fired when the block registry is considered finalized by the underlying mod loader.
 * @since 1.0.9
 */
public final class BlockRegistryFinalizedEvent
    extends RegistryFinalizedEvent<Block>
{
    /**
     * Constructs a new instance of the {@link BlockRegistryFinalizedEvent} class by providing the mod loader registry that was finalized.
     * @param registry The mod loader registry that was finalized.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public BlockRegistryFinalizedEvent(IModLoaderRegistry<Block> registry) throws ArgumentNullException { super(registry); }

    /**
     * Constructs a new instance of the {@link BlockRegistryFinalizedEvent} class by providing the registry that was finalized.
     * @param registry The registry that was finalized.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public BlockRegistryFinalizedEvent(Registry<Block> registry) throws ArgumentNullException { super(registry); }
}
