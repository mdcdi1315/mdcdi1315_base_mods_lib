package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.serialization.DataResult;

public final class PositiveIntegerCodec
    extends PrimitiveCodecWithValidation<Integer>
{
    @Override
    protected Integer Mapper(Number number) {
        return number.intValue();
    }

    @Override
    protected DataResult<Integer> Validate(Integer number) {
        return (number < 1) ?
            DataResult.error(StringSupplier.FromFormatted("Integer not positive: %d" , number)) :
            DataResult.success(number);
    }
}
