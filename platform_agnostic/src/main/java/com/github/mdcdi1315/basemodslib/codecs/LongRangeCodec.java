package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.serialization.DataResult;

/**
 * Provides a primitive codec with validation that validates whether a {@link Long} value is into the specified bounds.
 */
public final class LongRangeCodec
    extends PrimitiveCodecWithValidation<Long>
{
    private final long min_value, max_value;

    /**
     * Creates a new instance of the codec, specifying the range of numbers allowed to decode into.
     * @param min_value The minimum inclusive bound of the range of numbers allowed to decode.
     * @param max_value The maximum inclusive bound of the range of numbers allowed to decode.
     */
    public LongRangeCodec(long min_value , long max_value)
    {
        this.min_value = min_value;
        this.max_value = max_value;
    }

    @Override
    protected Long Mapper(Number number) {
        return number.longValue();
    }

    @Override
    protected DataResult<Long> Validate(Long number) {
        return (number < min_value || number > max_value) ?
                DataResult.error(StringSupplier.FromFormatted("Value %d outside of range [%d..%d]" , number , min_value , max_value), number) :
                DataResult.success(number);
    }
}
