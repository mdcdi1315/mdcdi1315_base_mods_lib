package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.util.function.LongSupplier;

/**
 * Defines a function that does not accept any arguments and does return a primitive {@code long} value.
 * @since 1.0.37
 */
@FunctionalInterface
public interface ProvidesLongFunction
    extends ProvidesNumberFunction<Long>, LongSupplier
{
    /**
     * The actual definition of the function.
     * @return The {@code long} value provided by the implementation.
     */
    long provide();

    @NotNull
    @Override
    default Long get() { return provide(); }

    @NotNull
    @Override
    default Long function() { return provide(); }

    @Override
    default long getAsLong() { return provide(); }
}
