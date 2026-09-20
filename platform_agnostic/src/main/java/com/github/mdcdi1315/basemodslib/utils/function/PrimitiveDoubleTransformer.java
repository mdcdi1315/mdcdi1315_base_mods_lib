package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a function signature, that, given a primitive double value,
 * it returns another primitive double value.
 * @since 1.0.38
 */
@FunctionalInterface
public interface PrimitiveDoubleTransformer
    extends TransformsPrimitiveFunction<Double>,
        java.util.function.DoubleFunction<Double>,
        java.util.function.DoubleUnaryOperator
{
    double transform(double input);

    @NotNull
    @Override
    default Double apply(double value) { return transform(value); }

    @NotNull
    @Override
    default Double apply(Double input) { return transform(input); }

    @NotNull
    @Override
    default Double convert(Double input) { return transform(input); }

    @NotNull
    @Override
    default Double function(Double input) { return transform(input); }

    @Override
    default double applyAsDouble(double operand) { return transform(operand); }
}
