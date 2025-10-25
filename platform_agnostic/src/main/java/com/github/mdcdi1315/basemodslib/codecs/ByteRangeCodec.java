package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.serialization.DataResult;

public final class ByteRangeCodec
    extends PrimitiveCodecWithValidation<Byte>
{
    private final byte min_value , max_value;

    public ByteRangeCodec(int min_value , int max_value)
    {
        this.min_value = (byte) min_value;
        this.max_value = (byte) max_value;
    }

    @Override
    protected Byte Mapper(Number number) {
        return number.byteValue();
    }

    @Override
    protected DataResult<Byte> Validate(Byte number) {
        return (number >= min_value && number <= max_value) ?
                DataResult.success(number) :
                DataResult.error(
                        new StringSupplier(
                                String.format("Value %d outside of range [%d..%d]" , number , min_value , max_value)
                        ),
                        number
                );
    }
}
