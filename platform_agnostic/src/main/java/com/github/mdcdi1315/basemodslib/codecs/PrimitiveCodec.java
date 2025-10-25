package com.github.mdcdi1315.basemodslib.codecs;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

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
    protected abstract <T> DataResult<TP> Read(final DynamicOps<T> ops, final T input);

    protected abstract <T> T Write(final DynamicOps<T> ops, final TP value);

    @Override
    public final <T> DataResult<Pair<TP, T>> decode(DynamicOps<T> ops, T input) {
        return Read(ops, input).map(r -> Pair.of(r, ops.empty()));
    }

    @Override
    public final <T> DataResult<T> encode(TP input, DynamicOps<T> ops, T prefix) {
        return ops.mergeToPrimitive(prefix, Write(ops, input));
    }
}
