package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.serialization.DataResult;

public final class ZeroOrPositiveIntegerCodec
    extends PrimitiveCodecWithValidation<Integer>
{

    @Override
    protected Integer Mapper(Number number) {
        return number.intValue();
    }

    @Override
    protected DataResult<Integer> Validate(Integer integer) {
        return (integer < 0) ?
            DataResult.error(new StringSupplier(String.format("Integer not positive or zero: %d" , integer)), integer) :
            DataResult.success(integer);
    }
}
