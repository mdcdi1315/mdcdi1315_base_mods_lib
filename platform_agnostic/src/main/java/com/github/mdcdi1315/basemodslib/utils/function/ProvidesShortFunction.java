package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Defines a function that does not accept any arguments and does return a primitive {@code short} value.
 * @since 1.0.37
 */
@FunctionalInterface
public interface ProvidesShortFunction
    extends ProvidesNumberFunction<Short>
{
    /**
     * The actual definition of the function.
     * @return The {@code short} value provided by the implementation.
     */
    short provide();

    @NotNull
    @Override
    default Short get() { return provide(); }

    @NotNull
    @Override
    default Short function() { return provide(); }
}
