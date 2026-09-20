package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a functional interface that accepts an argument of type {@link T}
 * and returns a primitive short value.
 * @param <T> The type of the input argument.
 * @since 1.0.38
 */
@FunctionalInterface
public interface ConvertsToShortFunction<T>
    extends ReturnsPrimitiveFunction<T, Short>
{
    /**
     * The actual implementation that accepts an input of type {@link T} and returns a {@code short} value.
     * @param input The input value.
     * @return The {@code short} value computed by {@code input}.
     */
    short to_short(T input);

    @NotNull
    @Override
    default Short apply(T input) { return to_short(input); }

    @NotNull
    @Override
    default Short convert(T input) { return to_short(input); }

    @NotNull
    @Override
    default Short function(T input) { return to_short(input); }
}
