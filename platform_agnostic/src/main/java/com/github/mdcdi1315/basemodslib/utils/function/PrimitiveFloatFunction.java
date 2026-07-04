package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides a function and returns a value of type {@link TR} that accepts a non-null {@link Float} value as input.
 * @param <TR> The type of the result value to return.
 * @since 1.0.29
 */
@FunctionalInterface
public interface PrimitiveFloatFunction<TR>
    extends PrimitiveNumericFunction<Float, TR>
{
    TR function(float input);

    @Override
    default TR apply(Float input) { return function(input.floatValue()); }

    @Override
    default TR convert(Float input) { return function(input.floatValue()); }

    @Override
    default TR function(Float input) { return function(input.floatValue()); }
}
