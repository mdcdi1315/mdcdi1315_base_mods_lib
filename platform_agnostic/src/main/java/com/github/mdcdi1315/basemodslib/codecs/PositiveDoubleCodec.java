package com.github.mdcdi1315.basemodslib.codecs;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public final class PositiveDoubleCodec
    extends PrimitiveCodecWithValidation<Double>
{
    @Override
    protected <T> T Write(DynamicOps<T> ops, Double value) { return ops.createDouble(value); }

    @Override
    protected Double Mapper(Number number) { return number.doubleValue(); }

    @Override
    protected DataResult<Double> Validate(Double number) {
        return (number > 0d) ?
                DataResult.success(number) :
                CodecUtils.CreateJavaFormattedErrorDataResult("Double-precision floating-point value not positive: %d" , number);
    }
}
