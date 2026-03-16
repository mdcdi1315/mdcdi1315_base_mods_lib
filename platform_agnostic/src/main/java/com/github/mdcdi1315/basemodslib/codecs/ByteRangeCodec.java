package com.github.mdcdi1315.basemodslib.codecs;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides a primitive codec with validation that validates whether a {@link Byte} value is into the specified bounds.
 */
public final class ByteRangeCodec
    extends PrimitiveCodecWithValidation<Byte>
{
    private final byte min_value , max_value;

    /**
     * Creates a new instance of the codec, specifying the range of numbers allowed to decode into.
     * @param min_value The minimum inclusive bound of the range of numbers allowed to decode.
     * @param max_value The maximum inclusive bound of the range of numbers allowed to decode.
     * @implNote Although that the parameters are defined as integers, they are downcast to byte values once the constructor returns. <br />
     * It is provided that way to load constants for the bounds directly without needing to cast to byte.
     */
    public ByteRangeCodec(int min_value , int max_value)
    {
        this.min_value = (byte) min_value;
        this.max_value = (byte) max_value;
    }

    @Override
    protected Byte Mapper(Number number) { return number.byteValue(); }

    @Override
    protected <T> T Write(DynamicOps<T> ops, Byte value) { return ops.createByte(value); }

    @Override
    protected DataResult<Byte> Validate(Byte number) {
        return (number < min_value || number > max_value) ?
                CodecUtils.CreateJavaFormattedErrorDataResultWithPartial("Value %d outside of range [%d..%d]", number, number, min_value, max_value) :
                DataResult.success(number);
    }
}
