package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

/**
 * Provides a function and returns a value of type {@link TR} that accepts a non-null {@link Byte} value as input.
 * @param <TR> The type of the result value to return.
 * @since 1.0.21
 */
@FunctionalInterface
public interface PrimitiveByteFunction<TR>
    extends Func2<Byte, TR>
{
    TR function(byte value);

    @Override
    default TR apply(Byte value) { return function(value.byteValue()); }

    @Override
    default TR function(Byte input) { return function(input.byteValue()); }
}
