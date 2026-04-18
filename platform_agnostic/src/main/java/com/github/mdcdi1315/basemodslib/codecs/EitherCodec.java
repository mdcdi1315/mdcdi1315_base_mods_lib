package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides a {@link Codec} that can decode either using the first or the second codec instances provided. <br />
 * It is functionally equivalent to the {@link com.mojang.serialization.codecs.EitherCodec} codec, but with <br />
 * one difference: this implementation does instead provide the decoded object as an {@link Object} reference, <br />
 * and it is the responsibility of the user to determine how the object was decoded. <br />
 * Additionally, {@code null} values are prohibited to be encoded because they could cause ambiguation issues.
 * @param <TF> The type that the first codec de/encodes as.
 * @param <TS> The type that the second codec de/encodes as.
 */
public final class EitherCodec<TF , TS>
    implements Codec<Object>
{
    private final Codec<TF> first_codec;
    private final Codec<TS> second_codec;

    /**
     * Creates a new instance of the {@link EitherCodec} class from the specified codec instances.
     * @param first The first codec instance to use.
     * @param second The second codec instance to use.
     * @throws ArgumentNullException {@code first} and/or {@code second} are {@code null}.
     */
    public EitherCodec(Codec<TF> first, Codec<TS> second)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(first, "first");
        ArgumentNullException.ThrowIfNull(second, "second");
        first_codec = first;
        second_codec = second;
    }

    @Override
    public <T> DataResult<Pair<Object, T>> decode(DynamicOps<T> ops, T input)
    {
        var dr1 = first_codec.decode(ops , input);

        var error = dr1.error();

        if (error.isPresent()) {
            String msg1 = error.get().message();

            var dr2 = second_codec.decode(ops , input);

            var e2 = dr2.error();

            if (e2.isPresent()) {
                return CodecUtils.CreateJavaFormattedErrorDataResult(
                        "Both codecs have failed to give result: \nFirst codec: %s\nSecond codec: %s",
                        msg1,
                        e2.get().message()
                );
            } else {
                return DataResult.success(new Pair<>(dr2.result().get().getFirst() , input));
            }
        } else {
            return DataResult.success(new Pair<>(dr1.result().get().getFirst(), input));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataResult<T> encode(Object input, DynamicOps<T> ops, T prefix)
    {
        if (input == null) {
            return CodecUtils.CreateErrorDataResult("Cannot encode the null value!");
        } else {
            try {
                return first_codec.encode((TF) input , ops , prefix);
            } catch (ClassCastException cce) {
                try {
                    return second_codec.encode((TS) input, ops , prefix);
                } catch (ClassCastException cce2) {
                    return CodecUtils.CreateErrorDataResult("This object cannot be casted to either TF or TS. Check whether you have passed the correct object instance.");
                }
            }
        }
    }
}
