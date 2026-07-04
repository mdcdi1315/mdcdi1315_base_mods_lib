package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides a function and returns a value of type {@link TR} that accepts a non-null {@link Short} value as input.
 * @param <TR> The type of the result value to return.
 * @since 1.0.21
 */
@FunctionalInterface
public interface PrimitiveShortFunction<TR>
    extends PrimitiveNumericFunction<Short, TR>
{
    TR function(short value);

    @Override
    default TR apply(Short value) { return function(value.shortValue()); }

    @Override
    default TR convert(Short value) { return function(value.shortValue()); }

    @Override
    default TR function(Short value) { return function(value.shortValue()); }
}
