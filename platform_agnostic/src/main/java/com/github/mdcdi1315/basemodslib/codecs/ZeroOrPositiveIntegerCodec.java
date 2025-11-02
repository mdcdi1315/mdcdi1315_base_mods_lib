package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.serialization.DataResult;

/**
 * Provides a primitive codec that validates whether the read integer value is zero or a positive value. <br />
 * It could be considered that this class is a special case of the {@link IntRangeCodec} class,
 * since it is possible to achieve the same thing by passing to its constructor 0 and {@link Integer#MAX_VALUE} value. <br />
 * However, this is a faster variant of that class and does not allocate the two integers required for retaining the desired number bounds.
 */
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
            DataResult.error(StringSupplier.FromFormatted("Integer not positive or zero: %d" , integer), integer) :
            DataResult.success(integer);
    }
}
