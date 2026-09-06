package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Defines a function that does not accept any arguments and does return a primitive {@code float} value.
 * @since 1.0.37
 */
@FunctionalInterface
public interface ProvidesFloatFunction
    extends ProvidesNumberFunction<Float>
{
    /**
     * The actual definition of the function.
     * @return The {@code float} value provided by the implementation.
     */
    float provide();

    @NotNull
    @Override
    default Float get() { return provide(); }

    @NotNull
    @Override
    default Float function() { return provide(); }
}
