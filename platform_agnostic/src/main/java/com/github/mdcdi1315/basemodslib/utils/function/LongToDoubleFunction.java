package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides a conversion function that does convert any {@code long} to its equivalent {@code double} value.
 * @since 1.0.35
 */
public record LongToDoubleFunction()
    implements PrimitiveLongFunction<Double>
{
    @Override
    public Double apply(long value) { return (double)value; }

    @Override
    public Double function(long value) { return (double)value; }

    @Override
    public Double apply(Long value) { return value.doubleValue(); }

    @Override
    public Double convert(Long value) { return value.doubleValue(); }

    @Override
    public Double function(Long value) { return value.doubleValue(); }
}
