package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides a wrapper {@link Codec} that falls back to another {@link Codec} implementation if the first one has failed. <br />
 * Inspired by NeoForge's withAlternative codec in NeoForgeExtraCodecs.
 * @param <T> The type that this codec de/encodes as.
 * @since 1.0.19
 */
public final class AlternativeCodec<T>
    implements Codec<T>
{
    private final Codec<T> normal, alternative;

    /**
     * Constructs a new instance of the {@link AlternativeCodec} class.
     * @param normal The normal {@link Codec} to use.
     * @param alternative The alternative {@link Codec} to use, if {@code normal} has failed to give a result.
     * @throws ArgumentNullException {@code normal} and/or {@code alternative} are {@code null}.
     */
    public AlternativeCodec(Codec<T> normal, Codec<T> alternative)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.normal = normal, "normal");
        ArgumentNullException.ThrowIfNull(this.alternative = alternative, "alternative");
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input)
    {
        DataResult<Pair<T, T1>> decoded = normal.decode(ops, input);
        return decoded.isError() ? alternative.decode(ops, input) : decoded;
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix)
    {
        DataResult<T1> encoded = normal.encode(input, ops, prefix);
        return encoded.isError() ? alternative.encode(input, ops, prefix) : encoded;
    }
}
