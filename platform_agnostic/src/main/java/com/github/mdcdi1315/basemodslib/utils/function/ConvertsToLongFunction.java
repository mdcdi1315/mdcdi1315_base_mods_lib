package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a functional interface that accepts an argument of type {@link T}
 * and returns a primitive long value.
 * @param <T> The type of the input argument.
 * @since 1.0.38
 */
@FunctionalInterface
public interface ConvertsToLongFunction<T>
    extends ReturnsPrimitiveFunction<T, Long>,
        java.util.function.ToLongFunction<T>
{
    /**
     * The actual implementation that accepts an input of type {@link T} and returns a {@code long} value.
     * @param input The input value.
     * @return The {@code long} value computed by {@code input}.
     */
    long to_long(T input);

    @NotNull
    @Override
    default Long apply(T input) { return to_long(input); }

    @NotNull
    @Override
    default Long convert(T input) { return to_long(input); }

    @NotNull
    @Override
    default Long function(T input) { return to_long(input); }

    @Override
    default long applyAsLong(T value) { return to_long(value); }
}
