package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

/**
 * Provides a function and returns a value of type {@link TR} that accepts a non-null {@link Double} value as input.
 * @param <TR> The type of the result value to return.
 * @since 1.0.29
 */
@FunctionalInterface
public interface PrimitiveDoubleFunction<TR>
    extends Func2<Double, TR>
{
    TR function(double input);

    @Override
    default TR apply(Double input) { return function(input.doubleValue()); }

    @Override
    default TR function(Double input) { return function(input.doubleValue()); }
}
