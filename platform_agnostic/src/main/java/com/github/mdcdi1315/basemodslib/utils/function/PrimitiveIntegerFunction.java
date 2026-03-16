package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import java.util.function.IntFunction;

/**
 * Provides a function and returns a value of type {@link TR} that accepts a non-null {@link Integer} value as input.
 * @param <TR> The type of the result value to return.
 * @since 1.0.21
 */
@FunctionalInterface
public interface PrimitiveIntegerFunction<TR>
    extends Func2<Integer, TR>, IntFunction<TR>
{
    TR function(int value);

    @Override
    default TR apply(int value) { return function(value); }

    @Override
    default TR function(Integer v) { return function(v.intValue()); }
}
