package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides a conversion function that does convert any integer to its equivalent {@code float} value.
 * @since 1.0.35
 */
public record IntToFloatFunction()
    implements PrimitiveIntegerFunction<Float>
{
    @Override
    public Float apply(int value) { return (float)value; }

    @Override
    public Float function(int value) { return (float)value; }

    @Override
    public Float apply(Integer value) { return value.floatValue(); }

    @Override
    public Float convert(Integer value) { return value.floatValue(); }

    @Override
    public Float function(Integer value) { return value.floatValue(); }
}
