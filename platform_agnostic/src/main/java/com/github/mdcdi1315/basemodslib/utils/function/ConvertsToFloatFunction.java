package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides a functional interface that accepts an argument of type {@link T}
 * and returns a primitive float value.
 * @param <T> The type of the input argument.
 * @since 1.0.38
 */
@FunctionalInterface
public interface ConvertsToFloatFunction<T>
    extends ReturnsPrimitiveFunction<T, Float>
{
    /**
     * The actual implementation that accepts an input of type {@link T} and returns a {@code float} value.
     * @param input The input value.
     * @return The {@code float} value computed by {@code input}.
     */
    float to_float(T input);

    @Override
    default Float apply(T input) { return to_float(input); }

    @Override
    default Float convert(T input) { return to_float(input); }

    @Override
    default Float function(T input) { return to_float(input); }
}
