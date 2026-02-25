package com.github.mdcdi1315.basemodslib.world;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;

/**
 * Provides a way to store a position in a world. <br />
 * It fully describes where the position is in a world. <br />
 * Equivalently the same as {@link net.minecraft.core.GlobalPos},
 * this is just provided as it's version-safe alternative.
 * @param level The dimension where the {@code position} refers to.
 * @param position The block position inside the dimension stored in {@link #level}.
 * @since 1.0.18
 */
public record GlobalPosition(@NotNull ResourceKey<Level> level, @NotNull BlockPos position)
{
    /**
     * Initializes a new instance of the {@link GlobalPosition} class.
     * @param level The dimension where the {@code position} refers to.
     * @param position The block position inside the dimension stored in {@link #level}.
     * @throws ArgumentNullException {@code level} and/or {@code position} are {@code null}.
     */
    public GlobalPosition {
        ArgumentNullException.ThrowIfNull(level, "level");
        ArgumentNullException.ThrowIfNull(position, "position");
    }
}
