package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.serialization.DataResult;

public final class FloatProbabilityCodec
    extends PrimitiveCodecWithValidation<Float>
{
    @Override
    protected Float Mapper(Number number) {
        return number.floatValue();
    }

    @Override
    protected DataResult<Float> Validate(Float number)
    {
        return (number > 1f || number < 0f) ?
                DataResult.error(StringSupplier.FromFormatted("Probability value out of range [0..1]: %f" , number), number) :
                DataResult.success(number);
    }
}
