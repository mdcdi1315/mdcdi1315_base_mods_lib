package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides a conversion function that does convert any byte to its equivalent {@code int} value.
 * @since 1.0.35
 */
public record ByteToIntFunction()
    implements PrimitiveByteFunction<Integer>
{
    @Override
    public Integer function(byte value) { return (int)value; }

    @Override
    public Integer apply(Byte value) { return value.intValue(); }

    @Override
    public Integer convert(Byte value) { return value.intValue(); }

    @Override
    public Integer function(Byte value) { return value.intValue(); }
}
