package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.serialization.DataResult;

/**
 * Like the {@link PositiveIntegerCodec} class, this does check whether the input provided is a positive floating-point integer value.
 */
public final class PositiveFloatCodec
    extends PrimitiveCodecWithValidation<Float>
{
    @Override
    protected Float Mapper(Number number) {
        return number.floatValue();
    }

    @Override
    protected DataResult<Float> Validate(Float number) {
        return (number > 0f) ?
                DataResult.success(number) :
                DataResult.error(StringSupplier.FromFormatted("Integer not positive: %d" , number));
    }
}
