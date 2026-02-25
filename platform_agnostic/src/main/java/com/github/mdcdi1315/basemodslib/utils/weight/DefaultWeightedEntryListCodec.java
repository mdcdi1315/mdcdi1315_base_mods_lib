package com.github.mdcdi1315.basemodslib.utils.weight;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.google.common.collect.ImmutableList;

import com.mojang.serialization.Codec;

import java.util.List;

/**
 * Provides a default implementation of the {@link WeightedEntryListCodec} by de/encoding raw {@link WeightedEntryList} instances.
 * @param <T> The type of elements to be de/encoded.
 */
public final class DefaultWeightedEntryListCodec<T extends IWeightedEntry>
    extends WeightedEntryListCodec<T, WeightedEntryList<T>>
{
    /**
     * Initializes a new instance of the {@link DefaultWeightedEntryListCodec} class.
     *
     * @param elementcodec The {@link Codec} that will be used to encode and decode elements from the list.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public DefaultWeightedEntryListCodec(Codec<T> elementcodec) throws ArgumentNullException { super(elementcodec); }

    private static final class Decoded<T extends IWeightedEntry>
        extends WeightedEntryList<T>
    {
        public Decoded(ImmutableList<T> e) { super(e); }
    }

    @Override
    protected WeightedEntryList<T> Transform(List<T> list) { return new Decoded<>(ImmutableList.copyOf(list)); }
}
