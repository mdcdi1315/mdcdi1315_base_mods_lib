package com.github.mdcdi1315.basemodslib.utils.function;

import java.util.function.IntFunction;

/**
 * Provides a function and returns a value of type {@link TR} that accepts a non-null {@link Integer} value as input.
 * @param <TR> The type of the result value to return.
 * @since 1.0.21
 */
@FunctionalInterface
public interface PrimitiveIntegerFunction<TR>
    extends PrimitiveNumericFunction<Integer, TR>, IntFunction<TR>
{
    TR function(int value);

    @Override
    default TR apply(int value) { return function(value); }

    @Override
    default TR apply(Integer value) { return function(value.intValue()); }

    @Override
    default TR convert(Integer value) { return function(value.intValue()); }

    @Override
    default TR function(Integer value) { return function(value.intValue()); }
}
