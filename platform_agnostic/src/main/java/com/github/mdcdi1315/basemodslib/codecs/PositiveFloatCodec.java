package com.github.mdcdi1315.basemodslib.codecs;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Like the {@link PositiveIntegerCodec} class, this does check whether the input provided is a positive floating-point integer value.
 */
public final class PositiveFloatCodec
    extends PrimitiveCodecWithValidation<Float>
{
    @Override
    protected <T> T Write(DynamicOps<T> ops, Float value) { return ops.createFloat(value); }

    @Override
    protected Float Mapper(Number number) { return number.floatValue(); }

    @Override
    protected DataResult<Float> Validate(Float number) {
        return (number > 0f) ?
                DataResult.success(number) :
                CodecUtils.CreateJavaFormattedErrorDataResult("Single-precision floating-point value not positive: %d" , number);
    }
}
