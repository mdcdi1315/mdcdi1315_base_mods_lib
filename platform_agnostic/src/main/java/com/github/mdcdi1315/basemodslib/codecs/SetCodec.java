package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.google.common.collect.ImmutableSet;

import com.mojang.serialization.Codec;

import java.util.Set;
import java.util.Collection;

/**
 * Provides a default implementation of the {@link AbstractSetCodec} class. <br />
 * The set is de/encoded into {@link ImmutableSet} instances.
 * @param <T> The type of the elements to decode as a set.
 * @since 1.0.19
 */
public class SetCodec<T>
    extends AbstractSetCodec<T, Set<T>>
{
    /**
     * Initializes a new instance of the {@link SetCodec} class.
     *
     * @param elementcodec The {@link Codec} that will be used to encode and decode elements from the set.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public SetCodec(Codec<T> elementcodec) throws ArgumentNullException { super(elementcodec); }

    @Override
    protected Set<T> Transform(Collection<T> c) { return ImmutableSet.copyOf(c); }
}
