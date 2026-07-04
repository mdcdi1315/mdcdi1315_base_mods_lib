package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides a conversion function that does convert any {@code long} to its equivalent {@code float} value.
 * @since 1.0.35
 */
public record LongToFloatFunction()
    implements PrimitiveLongFunction<Float>
{
    @Override
    public Float apply(long value) { return (float)value; }

    @Override
    public Float function(long value) { return (float)value; }

    @Override
    public Float apply(Long value) { return value.floatValue(); }

    @Override
    public Float convert(Long value) { return value.floatValue(); }

    @Override
    public Float function(Long value) { return value.floatValue(); }
}
