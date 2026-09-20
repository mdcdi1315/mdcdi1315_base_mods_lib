package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a functional interface that accepts an argument of type {@link T}
 * and returns a primitive byte value.
 * @param <T> The type of the input argument.
 * @since 1.0.38
 */
@FunctionalInterface
public interface ConvertsToByteFunction<T>
    extends ReturnsPrimitiveFunction<T, Byte>
{
    /**
     * The actual implementation that accepts an input of type {@link T} and returns a {@code byte} value.
     * @param input The input value.
     * @return The {@code byte} value computed by {@code input}.
     */
    byte to_byte(T input);

    @NotNull
    @Override
    default Byte apply(T input) { return to_byte(input); }

    @NotNull
    @Override
    default Byte convert(T input) { return to_byte(input); }

    @NotNull
    @Override
    default Byte function(T input) { return to_byte(input); }
}
