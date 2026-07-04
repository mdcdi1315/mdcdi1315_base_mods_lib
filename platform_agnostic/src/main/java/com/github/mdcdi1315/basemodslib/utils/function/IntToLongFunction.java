package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides a conversion function that does convert any integer to its equivalent {@code long} value.
 * @since 1.0.35
 */
public record IntToLongFunction()
    implements PrimitiveIntegerFunction<Long>
{
    @Override
    public Long apply(int value) { return (long)value; }

    @Override
    public Long function(int value) { return (long)value; }

    @Override
    public Long apply(Integer input) { return input.longValue(); }

    @Override
    public Long convert(Integer input) { return input.longValue(); }

    @Override
    public Long function(Integer input) { return input.longValue(); }
}
