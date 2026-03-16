package com.github.mdcdi1315.basemodslib.codecs;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides the implementation for decoding floating-point probability values,
 * that are values ranging from 0 to 1. <br />
 * It is preferable to use this implementation instead of the float range codec because that one allocates two codec instances,
 * while this class incorporates both functionalities in one class instance. <br />
 * Finally, you should obtain an instance of this class by the singleton provided in {@link CodecUtils} class, namely the {@link CodecUtils#FLOAT_PROBABILITY} field.
 */
public final class FloatProbabilityCodec
    extends PrimitiveCodecWithValidation<Float>
{
    @Override
    protected Float Mapper(Number number) { return number.floatValue(); }

    @Override
    protected <T> T Write(DynamicOps<T> ops, Float value) { return ops.createFloat(value); }

    @Override
    protected DataResult<Float> Validate(Float number)
    {
        return (number > 1f || number < 0f) ?
                CodecUtils.CreateJavaFormattedErrorDataResultWithPartial("Probability value out of range [0..1]: %f", number, number) :
                DataResult.success(number);
    }
}
