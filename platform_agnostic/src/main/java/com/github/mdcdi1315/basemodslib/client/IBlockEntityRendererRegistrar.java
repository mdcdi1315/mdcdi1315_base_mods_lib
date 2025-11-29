package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.Contract;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY <br />
 * Provides a way for registering block entity renderers to Minecraft.
 */
@Contract
public interface IBlockEntityRendererRegistrar
{
    /**
     * Registers a block entity renderer to Minecraft client.
     * @param info The block entity renderer registration information.
     * @param <T> The type of the block entity this renderer applies to.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T extends BlockEntity> void Register(BlockEntityRendererRegistrationInfo<T> info) throws ArgumentNullException;
}
