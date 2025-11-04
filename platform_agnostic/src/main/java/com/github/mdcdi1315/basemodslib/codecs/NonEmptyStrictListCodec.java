package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.mojang.serialization.Codec;

import java.util.List;

/**
 * A special variant of the {@link StrictListCodec} by additionally validating that the list is not empty after decode.
 * @param <TElement> The type of the elements to decode.
 */
public class NonEmptyStrictListCodec<TElement>
    extends StrictListCodec<TElement>
{
    /**
     * Initializes a new instance of the {@link NonEmptyStrictListCodec} class.
     * @param elementcodec The {@link Codec} that will be used to encode and decode elements from the list.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public NonEmptyStrictListCodec(Codec<TElement> elementcodec)
            throws ArgumentNullException
    {
        super(elementcodec);
    }

    @Override
    protected List<TElement> Transform(List<TElement> list) {
        if (list.isEmpty()) {
            throw new InvalidOperationException("List must not be empty.");
        }
        return super.Transform(list);
    }
}
