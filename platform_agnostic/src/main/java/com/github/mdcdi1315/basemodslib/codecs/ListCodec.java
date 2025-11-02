package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.google.common.collect.ImmutableList;

import com.mojang.serialization.Codec;

import java.util.List;

/**
 * Provides a default implementation of the {@link AbstractListCodec} class. <br />
 * The list is de/encoded into {@link ImmutableList} instances.
 * @param <TElement>
 */
public class ListCodec<TElement>
    extends AbstractListCodec<TElement , List<TElement>>
{
    /**
     * Initializes a new instance of the {@link ListCodec} class.
     * @param elementcodec The {@link Codec} that will be used to encode and decode elements from the list.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public ListCodec(Codec<TElement> elementcodec)
            throws ArgumentNullException
    {
        super(elementcodec);
    }

    @Override
    protected List<TElement> Transform(List<TElement> list) {
        return ImmutableList.copyOf(list);
    }
}