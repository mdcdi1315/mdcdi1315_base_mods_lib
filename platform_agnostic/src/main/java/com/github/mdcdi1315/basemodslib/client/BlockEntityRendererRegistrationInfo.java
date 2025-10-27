package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Func1;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public record BlockEntityRendererRegistrationInfo<T extends BlockEntity>(
        Func1<BlockEntityType<T>> type,
        BlockEntityRendererProvider<? super T> provider
)
{
    public BlockEntityRendererRegistrationInfo {
        ArgumentNullException.ThrowIfNull(type, "type");
        ArgumentNullException.ThrowIfNull(provider,"provider");
    }
}
