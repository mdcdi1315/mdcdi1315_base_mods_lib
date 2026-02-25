package com.github.mdcdi1315.basemodslib.world;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;

/**
 * An 'extended' variant of the {@link GlobalPosition} class,
 * providing a better precision on the block position by using a {@link Vec3} object instead.
 * @param level The dimension where the {@code position} refers to.
 * @param position The block position inside the dimension stored in {@link #level}.
 * @since 1.0.18
 */
public record GlobalPrecisePosition(@NotNull ResourceKey<Level> level, @NotNull Vec3 position)
{
    /**
     * Initializes a new instance of the {@link GlobalPrecisePosition} class.
     * @param level The dimension where the {@code position} refers to.
     * @param position The block position inside the dimension stored in {@link #level}.
     * @throws ArgumentNullException {@code level} and/or {@code position} are {@code null}.
     */
    public GlobalPrecisePosition {
        ArgumentNullException.ThrowIfNull(level, "level");
        ArgumentNullException.ThrowIfNull(position, "position");
    }
}
