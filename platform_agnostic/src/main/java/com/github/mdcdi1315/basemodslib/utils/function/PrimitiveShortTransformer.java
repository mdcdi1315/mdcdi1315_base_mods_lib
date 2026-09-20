package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a function signature, that, given a primitive short value,
 * it returns another primitive short value.
 * @since 1.0.38
 */
@FunctionalInterface
public interface PrimitiveShortTransformer
    extends TransformsPrimitiveFunction<Short>
{
    short transform(short value);

    @NotNull
    @Override
    default Short apply(Short input) { return transform(input); }

    @NotNull
    @Override
    default Short convert(Short input) { return transform(input); }

    @NotNull
    @Override
    default Short function(Short input) { return transform(input); }
}
