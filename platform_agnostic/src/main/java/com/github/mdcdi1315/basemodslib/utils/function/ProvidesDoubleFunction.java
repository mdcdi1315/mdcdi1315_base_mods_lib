package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.util.function.DoubleSupplier;

/**
 * Defines a function that does not accept any arguments and does return a primitive {@code double} value.
 * @since 1.0.37
 */
@FunctionalInterface
public interface ProvidesDoubleFunction
    extends ProvidesNumberFunction<Double>, DoubleSupplier
{
    /**
     * The actual definition of the function.
     * @return The {@code double} value provided by the implementation.
     */
    double provide();

    @NotNull
    @Override
    default Double get() { return provide(); }

    @NotNull
    @Override
    default Double function() { return provide(); }

    @Override
    default double getAsDouble() { return provide(); }
}
