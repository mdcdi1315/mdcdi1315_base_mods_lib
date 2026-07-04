package com.github.mdcdi1315.basemodslib.utils.function;

import java.util.function.LongFunction;

/**
 * Provides a function and returns a value of type {@link TR} that accepts a non-null {@link Long} value as input.
 * @param <TR> The type of the result value to return.
 * @since 1.0.21
 */
@FunctionalInterface
public interface PrimitiveLongFunction<TR>
    extends PrimitiveNumericFunction<Long, TR>, LongFunction<TR>
{
    TR function(long value);

    @Override
    default TR apply(long value) { return function(value); }

    @Override
    default TR apply(Long value) { return function(value.longValue()); }

    @Override
    default TR convert(Long value) { return function(value.longValue()); }

    @Override
    default TR function(Long value) { return function(value.longValue()); }
}
