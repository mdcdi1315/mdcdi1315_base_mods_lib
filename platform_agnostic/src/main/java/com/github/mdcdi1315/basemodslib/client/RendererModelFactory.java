package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BlockModel;

/**
 * Factory function for creating new block unbaked renderers.
 * For registering a special model renderer, see the {@link net.minecraft.client.renderer.block.model.SpecialBlockModelWrapper} class and
 * {@link SpecialRendererModelFactory}.
 */
@FunctionalInterface
public interface RendererModelFactory
{
    @NotNull
    BlockModel.Unbaked Create(@DisallowNull BlockColors colors, @DisallowNull BlockState state);
}