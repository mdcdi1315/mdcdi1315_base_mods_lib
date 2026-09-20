package com.github.mdcdi1315.basemodslib.utils.collections.codec_support;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.mojang.serialization.Codec;

/**
 * Provides a default codec implementation for the {@link List} class. <br />
 * This class can be inherited.
 * @param <T> The type of the elements that the {@link List} holds.
 */
public class ListCodec<T>
    extends EnumerableCodec<T, List<T>>
{
    /**
     * Initializes a new instance of the {@link ListCodec} class.
     *
     * @param element_codec The {@link Codec} that can de/encode elements of type {@link T}.
     * @throws ArgumentNullException {@code element_codec} is {@code null}.
     */
    public ListCodec(Codec<T> element_codec) throws ArgumentNullException { super(element_codec); }

    @Override
    protected List<T> CreateCollection() { return new List<>(20); }

    @Override
    protected void FinalizeCollection(List<T> collection) { collection.TrimExcess(); }

    @Override
    protected void AddElement(List<T> collection_inst, T element) { collection_inst.Add(element); }
}
