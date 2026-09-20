package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a function signature, that, given a primitive float value,
 * it returns another primitive float value.
 * @since 1.0.38
 */
@FunctionalInterface
public interface PrimitiveFloatTransformer
    extends TransformsPrimitiveFunction<Float>
{
    float transform(float input);

    @NotNull
    @Override
    default Float apply(Float input) { return transform(input); }

    @NotNull
    @Override
    default Float convert(Float input) { return transform(input); }

    @NotNull
    @Override
    default Float function(Float input) { return transform(input); }
}
