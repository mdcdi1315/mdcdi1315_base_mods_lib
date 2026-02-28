package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.mojang.serialization.Codec;

import java.util.Set;
import java.util.Collection;

/**
 * Provides a default implementation of the {@link AbstractStrictSetCodec} class. <br />
 * The list is de/encoded into {@link ImmutableList} instances.
 * @param <T> The type of the elements to decode as a list.
 * @since 1.0.19
 */
public class StrictSetCodec<T>
     extends AbstractStrictSetCodec<T, Set<T>>
{
    /**
     * Initializes a new instance of the {@link StrictSetCodec} class.
     *
     * @param elementcodec The {@link Codec} that will be used to encode and decode elements from the set.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public StrictSetCodec(Codec<T> elementcodec) throws ArgumentNullException { super(elementcodec); }

    @Override
    protected Set<T> Transform(Collection<T> c) { return ImmutableSet.copyOf(c); }
}
