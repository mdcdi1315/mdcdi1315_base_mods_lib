package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a functional interface that accepts an argument of type {@link T}
 * and returns a primitive double value.
 * @param <T> The type of the input argument.
 * @since 1.0.38
 */
@FunctionalInterface
public interface ConvertsToDoubleFunction<T>
    extends ReturnsPrimitiveFunction<T, Double>,
        java.util.function.ToDoubleFunction<T>
{
    /**
     * The actual implementation that accepts an input of type {@link T} and returns a {@code double} value.
     * @param input The input value.
     * @return The {@code double} value computed by {@code input}.
     */
    double to_double(T input);

    @NotNull
    @Override
    default Double apply(T input) { return to_double(input); }

    @NotNull
    @Override
    default Double convert(T input) { return to_double(input); }

    @NotNull
    @Override
    default Double function(T input) { return to_double(input); }

    @Override
    default double applyAsDouble(T value) { return to_double(value); }
}
