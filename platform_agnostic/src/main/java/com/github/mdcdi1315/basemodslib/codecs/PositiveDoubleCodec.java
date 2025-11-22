package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.serialization.DataResult;

/**
 * Like the {@link PositiveIntegerCodec} class, this does check whether the input provided is a positive floating-point integer value.
 */
public final class PositiveDoubleCodec
    extends PrimitiveCodecWithValidation<Double>
{
    @Override
    protected Double Mapper(Number number) {
        return number.doubleValue();
    }

    @Override
    protected DataResult<Double> Validate(Double number) {
        return (number > 0d) ?
                DataResult.success(number) :
                DataResult.error(StringSupplier.FromFormatted("Integer not positive: %d" , number));
    }
}
