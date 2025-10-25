package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.serialization.DataResult;

public final class ShortRangeCodec
    extends PrimitiveCodecWithValidation<Short>
{
    private final short min_value, max_value;

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
        return (number >= min_value && number <= max_value) ?
                DataResult.success(number) :
                DataResult.error(new StringSupplier(String.format("Value %d outside of range [%d..%d]" , number , min_value , max_value)), number);
    }
}
