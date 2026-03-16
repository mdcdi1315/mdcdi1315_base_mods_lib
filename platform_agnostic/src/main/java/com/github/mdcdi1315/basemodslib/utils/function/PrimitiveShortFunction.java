package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

/**
 * Provides a function and returns a value of type {@link TR} that accepts a non-null {@link Short} value as input.
 * @param <TR> The type of the result value to return.
 * @since 1.0.21
 */
@FunctionalInterface
public interface PrimitiveShortFunction<TR>
    extends Func2<Short, TR>
{
    TR function(short value);

    @Override
    default TR apply(Short aShort) { return function(aShort.shortValue()); }

    @Override
    default TR function(Short input) { return function(input.shortValue()); }
}
