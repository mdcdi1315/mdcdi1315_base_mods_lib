package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.serialization.DataResult;

/**
 * Provides the probability codec implementation for double-precision floating-point numbers,
 * see {@link FloatProbabilityCodec} class for more information about this.
 */
public final class DoubleProbabilityCodec
    extends PrimitiveCodecWithValidation<Double>
{
    @Override
    protected Double Mapper(Number number) {
        return number.doubleValue();
    }

    @Override
    protected DataResult<Double> Validate(Double number)
    {
        return (number > 1d || number < 0d) ?
                DataResult.error(StringSupplier.FromFormatted("Probability value out of range [0..1]: %f" , number), number) :
                DataResult.success(number);
    }
}
