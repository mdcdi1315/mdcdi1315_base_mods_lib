package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

/**
 * CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY <br />
 * Provides a way for registering block entity renderers to Minecraft.
 */
public interface IBlockEntityRendererRegistrar
{
    /**
     * Registers a block entity renderer to Minecraft client.
     * @param info The block entity renderer registration information.
     * @param <T> The type of the block entity this renderer applies to.
     * @param <S> The type of the block entity render state type to use.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T extends BlockEntity, S extends BlockEntityRenderState> void Register(BlockEntityRendererRegistrationInfo<T, S> info) throws ArgumentNullException;
}
