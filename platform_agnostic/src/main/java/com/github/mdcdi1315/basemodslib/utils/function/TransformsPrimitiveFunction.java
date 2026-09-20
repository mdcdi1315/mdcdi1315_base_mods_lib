package com.github.mdcdi1315.basemodslib.utils.function;

import java.util.function.UnaryOperator;

/**
 * Provides the base functional interface that given an input numeric
 * value, returns another numeric value of the same type.
 * @param <T> The numeric type to be provided and returned from the function.
 * @since 1.0.38
 */
@FunctionalInterface
public interface TransformsPrimitiveFunction<T extends Number>
    extends ReturnsPrimitiveFunction<T, T>, PrimitiveNumericFunction<T, T>, UnaryOperator<T>
{
    /**
     * Transforms the given numeric value into a value of the same type.
     * @param input The input value to transform.
     * @return The transformed value.
     */
    @Override
    T function(T input);

    @Override
    default T apply(T input) { return function(input); }

    @Override
    default T convert(T input) { return function(input); }
}
