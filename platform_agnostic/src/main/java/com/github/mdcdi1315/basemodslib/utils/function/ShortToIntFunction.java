package com.github.mdcdi1315.basemodslib.utils.function;

/**
 * Provides a conversion function that does convert any {@code short} to its equivalent {@code int} value.
 * @since 1.0.35
 */
public record ShortToIntFunction()
    implements PrimitiveShortFunction<Integer>
{
    @Override
    public Integer function(short value) { return (int)value; }

    @Override
    public Integer apply(Short value) { return value.intValue(); }

    @Override
    public Integer convert(Short value) { return value.intValue(); }

    @Override
    public Integer function(Short value) { return value.intValue(); }
}
