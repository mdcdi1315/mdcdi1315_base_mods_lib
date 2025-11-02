package com.github.mdcdi1315.basemodslib.codecs;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.Function;

/**
 * A class implementation used as the base for primitive-related codecs. <br />
 * Note that there is already an interface of a primitive codec declared in DFU
 * however I made up my own one to ensure forward compatibility. <br />
 * This is also a class and not an interface to preserve encapsulation.
 * @param <TP> The type of the primitive to de/encode
 */
public abstract class PrimitiveCodec<TP>
        implements Codec<TP>
{
    private record DecodeMapper<TP , T>(T empty)
        implements Function<TP , Pair<TP, T>>
    {
        @Override
        public Pair<TP , T> apply(TP tp) {
            return Pair.of(tp , empty);
        }
    }

    /**
     * Reads a primitive from the specified dynamic ops and input data.
     * @param ops The dynamic ops object to decode the specified value.
     * @param input The input data to decode the primitive from.
     * @return A data result holding the decoded primitive value on success.
     * @param <T> The type of the input to decode the primitive from.
     */
    protected abstract <T> DataResult<TP> Read(final DynamicOps<T> ops, final T input);

    /**
     * Encodes the specified primitive and returns the data representing that primitive in serialized result.
     * @param ops The dynamic ops object to encode the specified value.
     * @param value The primitive to encode.
     * @return A data result holding the serialized result on success.
     * @param <T> The type of the serialized result to encode the primitive into.
     */
    protected abstract <T> T Write(final DynamicOps<T> ops, final TP value);

    /**
     * Decodes a primitive from the specified dynamic ops and input data.
     * @param ops The dynamic ops object to decode the specified value.
     * @param input The input data to decode the primitive from.
     * @return A data result holding the decoded primitive value on success.
     * @param <T> The type of the input to decode the primitive from.
     */
    @Override
    public final <T> DataResult<Pair<TP, T>> decode(DynamicOps<T> ops, T input) {
        return Read(ops, input).map(new DecodeMapper<>(ops.empty()));
    }

    /**
     * Encodes the specified primitive value by merging it to a primitive value.
     * @param input The input primitive value to encode.
     * @param ops The dynamic ops object to encode the specified value.
     * @param prefix The object under which this primitive value will be stored to.
     * @return A data result holding the serialized result on success.
     * @param <T> The type of the serialized result to encode the primitive into.
     */
    @Override
    public final <T> DataResult<T> encode(TP input, DynamicOps<T> ops, T prefix) {
        return ops.mergeToPrimitive(prefix, Write(ops, input));
    }
}
