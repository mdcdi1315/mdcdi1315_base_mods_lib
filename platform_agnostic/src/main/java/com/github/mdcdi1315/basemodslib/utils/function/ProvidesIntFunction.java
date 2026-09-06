package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.util.function.IntSupplier;

/**
 * Defines a function that does not accept any arguments and does return a primitive {@code int} value.
 * @since 1.0.37
 */
@FunctionalInterface
public interface ProvidesIntFunction
    extends ProvidesNumberFunction<Integer>, IntSupplier
{
    /**
     * The actual definition of the function.
     * @return The {@code int} value provided by the implementation.
     */
    int provide();

    @NotNull
    @Override
    default Integer get() { return provide(); }

    @Override
    default int getAsInt() { return provide(); }

    @NotNull
    @Override
    default Integer function() { return provide(); }
}
