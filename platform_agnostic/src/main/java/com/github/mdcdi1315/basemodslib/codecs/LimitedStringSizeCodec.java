package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides a string {@link Codec} that does apply a limit on the number of characters that a string value can consist of when it is de/encoded.
 * @since 1.0.23
 */
public final class LimitedStringSizeCodec
    implements Codec<String>
{
    private final int max_size, min_size;

    /**
     * Initializes a new instance of the {@link LimitedStringSizeCodec} class,
     * specifying the range of string sizes under which de/encodes succeed.
     * @param min_size The minimum number of characters that a string must have to be de/encoded successfully.
     * @param max_size The maximum number of characters that a string must have to be de/encoded successfully. This must be greater than {@code min_size}.
     * @throws ArgumentOutOfRangeException {@code min_size} or {@code max_size} are negative values. <br />
     * -or- <br />
     * {@code min_size} is equal to or larger than {@code max_size}.
     */
    public LimitedStringSizeCodec(int min_size, int max_size)
            throws ArgumentOutOfRangeException
    {
        if (min_size < 0) {
            throw new ArgumentOutOfRangeException("min_size", "Minimum size of the string cannot be negative");
        } else if (max_size < 0) {
            throw new ArgumentOutOfRangeException("max_size", "Maximum size of the string cannot be negative");
        } else if (min_size >= max_size) {
            throw new ArgumentOutOfRangeException("min_size", "Minimum size of the string cannot be equal to or greater than max_size");
        } else {
            this.min_size = min_size;
            this.max_size = max_size;
        }
    }

    @Override
    public <T> DataResult<Pair<String, T>> decode(DynamicOps<T> ops, T input)
    {
        DataResult<String> dr = ops.getStringValue(input);
        if (dr.isSuccess()) {
            String value = dr.result().get();
            int len = value.length();
            if (len < min_size || len > max_size) {
                return CodecUtils.CreateDotNetFormattedErrorDataResult("The length of the string ({0} characters) is out of the range [{1}..{2}].", len, min_size, max_size);
            } else {
                return DataResult.success(new Pair<>(value, input));
            }
        } else {
            return DataResult.error(dr.error().get().messageSupplier());
        }
    }

    @Override
    public <T> DataResult<T> encode(String input, DynamicOps<T> ops, T prefix)
    {
        int len = input.length();
        if (len < min_size || len > max_size) {
            return CodecUtils.CreateDotNetFormattedErrorDataResult("The length of the string ({0} characters) is out of the range [{1}..{2}].", len, min_size, max_size);
        } else {
            return DataResult.success(ops.createString(input));
        }
    }
}
