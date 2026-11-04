package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public record BlockEntityRendererRegistrationInfo<T extends BlockEntity, S extends BlockEntityRenderState>(
        Func1<BlockEntityType<T>> type,
        BlockEntityRendererProvider<? super T, S> provider
)
{
    public BlockEntityRendererRegistrationInfo {
        ArgumentNullException.ThrowIfNull(type, "type");
        ArgumentNullException.ThrowIfNull(provider,"provider");
    }
}
