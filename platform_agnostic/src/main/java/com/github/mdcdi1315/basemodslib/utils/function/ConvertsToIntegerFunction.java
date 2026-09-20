package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a functional interface that accepts an argument of type {@link T}
 * and returns a primitive integer value.
 * @param <T> The type of the input argument.
 * @since 1.0.38
 */
@FunctionalInterface
public interface ConvertsToIntegerFunction<T>
    extends ReturnsPrimitiveFunction<T, Integer>,
        java.util.function.ToIntFunction<T>
{
    /**
     * The actual implementation that accepts an input of type {@link T} and returns an {@code int} value.
     * @param input The input value.
     * @return The {@code int} value computed by {@code input}.
     */
    int to_int(T input);

    @NotNull
    @Override
    default Integer apply(T input) { return to_int(input); }

    @Override
    default int applyAsInt(T value) { return to_int(value); }

    @NotNull
    @Override
    default Integer convert(T input) { return to_int(input); }

    @NotNull
    @Override
    default Integer function(T input) { return to_int(input); }
}
