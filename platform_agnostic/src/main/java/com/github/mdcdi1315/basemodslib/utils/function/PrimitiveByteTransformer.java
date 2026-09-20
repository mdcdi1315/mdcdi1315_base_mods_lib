package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides a function signature, that, given a primitive byte value,
 * it returns another primitive byte value.
 * @since 1.0.38
 */
@FunctionalInterface
public interface PrimitiveByteTransformer
    extends TransformsPrimitiveFunction<Byte>
{
    byte transform(byte value);

    @Override
    default Byte apply(Byte input) { return transform(input); }

    @Override
    default Byte convert(Byte input) { return transform(input); }

    @Override
    default Byte function(Byte input) { return transform(input); }
}
