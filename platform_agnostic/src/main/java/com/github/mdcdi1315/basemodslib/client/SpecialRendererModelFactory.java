package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.CompositeBlockModel;
import net.minecraft.client.renderer.block.model.BlockStateModelWrapper;

import java.util.Optional;

/**
 * A {@link RendererModelFactory} extendant for special renderer models.
 */
@FunctionalInterface
public interface SpecialRendererModelFactory
        extends RendererModelFactory
{
    @NotNull
    default BlockModel.Unbaked Create(@DisallowNull BlockColors colors, @DisallowNull BlockState state)
    {
        return new CompositeBlockModel.Unbaked(
                new BlockStateModelWrapper.Unbaked(
                        state,
                        colors.getTintSources(state),
                        Optional.empty()
                ),
                CreateSpecial(state),
                Optional.empty()
        );
    }

    /**
     * Create a special, unbaked, block model for the given state.
     * @param state The block state to create an unbaked model for it.
     * @return The specially created unbaked block model.
     */
    @NotNull
    BlockModel.Unbaked CreateSpecial(@DisallowNull BlockState state);
}

