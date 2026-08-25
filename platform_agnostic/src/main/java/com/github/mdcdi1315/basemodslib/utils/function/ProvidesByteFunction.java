package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Defines a function that does not accept any arguments and does return a primitive {@code byte} value.
 * @since 1.0.37
 */
@FunctionalInterface
public interface ProvidesByteFunction
    extends ProvidesNumberFunction<Byte>
{
    /**
     * The actual definition of the function.
     * @return The {@code byte} value provided by the implementation.
     */
    byte provide();

    @NotNull
    @Override
    default Byte get() { return provide(); }

    @NotNull
    @Override
    default Byte function() { return provide(); }
}
