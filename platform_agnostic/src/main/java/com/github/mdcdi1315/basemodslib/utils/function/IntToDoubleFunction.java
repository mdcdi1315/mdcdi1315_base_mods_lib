package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides a conversion function that does convert any integer to its equivalent {@code double} value.
 * @since 1.0.35
 */
public record IntToDoubleFunction()
    implements PrimitiveIntegerFunction<Double>
{
    @Override
    public Double apply(int value) { return (double)value; }

    @Override
    public Double function(int value) { return (double)value; }

    @Override
    public Double apply(Integer value) { return value.doubleValue(); }

    @Override
    public Double convert(Integer value) { return value.doubleValue(); }

    @Override
    public Double function(Integer value) { return value.doubleValue(); }
}
