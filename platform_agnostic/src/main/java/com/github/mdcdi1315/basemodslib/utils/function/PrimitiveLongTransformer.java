package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a function signature, that, given a primitive long value,
 * it returns another primitive long value.
 * @since 1.0.38
 */
@FunctionalInterface
public interface PrimitiveLongTransformer
    extends TransformsPrimitiveFunction<Long>,
        java.util.function.LongFunction<Long>,
        java.util.function.LongUnaryOperator
{
    long transform(long value);

    @NotNull
    @Override
    default Long apply(long value) { return transform(value); }

    @NotNull
    @Override
    default Long apply(Long input) { return transform(input); }

    @NotNull
    @Override
    default Long convert(Long input) { return transform(input); }

    @NotNull
    @Override
    default Long function(Long input) { return transform(input); }

    @Override
    default long applyAsLong(long operand) { return transform(operand); }
}
