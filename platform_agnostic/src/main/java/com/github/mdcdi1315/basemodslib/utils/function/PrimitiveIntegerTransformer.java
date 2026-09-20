package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a function signature, that, given a primitive integer value,
 * it returns another primitive integer value.
 * @since 1.0.38
 */
@FunctionalInterface
public interface PrimitiveIntegerTransformer
    extends TransformsPrimitiveFunction<Integer>,
        java.util.function.IntFunction<Integer>,
        java.util.function.IntUnaryOperator
{
    int transform(int value);

    @NotNull
    @Override
    default Integer apply(int value) { return transform(value); }

    @NotNull
    @Override
    default Integer apply(Integer input) { return transform(input); }

    @Override
    default int applyAsInt(int operand) { return transform(operand); }

    @NotNull
    @Override
    default Integer convert(Integer input) { return transform(input); }

    @NotNull
    @Override
    default Integer function(Integer input) { return transform(input); }
}
