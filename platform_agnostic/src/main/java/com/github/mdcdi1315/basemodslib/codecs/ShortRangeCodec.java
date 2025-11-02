package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.serialization.DataResult;

/**
 * Provides a primitive codec with validation that validates whether a {@link Short} value is into the specified bounds.
 */
public final class ShortRangeCodec
    extends PrimitiveCodecWithValidation<Short>
{
    private final short min_value, max_value;

    /**
     * Creates a new instance of the codec, specifying the range of numbers allowed to decode into.
     * @param min_value The minimum inclusive bound of the range of numbers allowed to decode.
     * @param max_value The maximum inclusive bound of the range of numbers allowed to decode.
     * @implNote Although that the parameters are defined as integers, they are downcast to short values once the constructor returns. <br />
     * It is provided that way to load constants for the bounds directly without needing to cast to short.
     */
    public ShortRangeCodec(int min_value , int max_value)
    {
        this.min_value = (short) min_value;
        this.max_value = (short) max_value;
    }

    @Override
    protected Short Mapper(Number number) {
        return number.shortValue();
    }

    @Override
    protected DataResult<Short> Validate(Short number) {
        return (number < min_value || number > max_value) ?
                DataResult.error(StringSupplier.FromFormatted("Value %d outside of range [%d..%d]" , number , min_value , max_value), number) :
                DataResult.success(number);
    }
}
