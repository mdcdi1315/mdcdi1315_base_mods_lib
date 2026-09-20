package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.world.level.block.Block;

/**
 * Provides information for registering a new special model renderer to Minecraft. <br />
 * Typically it is used for saving data for rendered block entities.
 * @param block The function that provides the block to render, once is called.
 * @param unbaked_model_factory The unbaked special model renderer to use.
 * @since 1.0.11
 */
public record SpecialModelRendererRegistrationInfo(
        @NotNull Func1<Block> block,
        @NotNull RendererModelFactory unbaked_model_factory
) {
    /**
     * Constructs a new instance of the {@link SpecialModelRendererRegistrationInfo} class.
     * @param block The function that provides the block to render, once is called.
     * @param unbaked_model_factory The unbaked special model renderer factory to use.
     * @throws ArgumentNullException {@code block} and/or {@code unbaked_model_factory} are {@code null}.
     */
    public SpecialModelRendererRegistrationInfo
    {
        ArgumentNullException.ThrowIfNull(block, "block");
        ArgumentNullException.ThrowIfNull(unbaked_model_factory , "unbaked_model_factory");
    }
}
