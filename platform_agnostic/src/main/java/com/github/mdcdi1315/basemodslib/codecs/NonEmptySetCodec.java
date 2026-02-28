package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.mojang.serialization.Codec;

import java.util.Set;
import java.util.Collection;

/**
 * A special variant of the {@link SetCodec} by additionally validating that the set is not empty after decode.
 * @param <T> The type of the elements to decode.
 * @since 1.0.19
 */
public class NonEmptySetCodec<T>
    extends SetCodec<T>
{
    /**
     * Initializes a new instance of the {@link NonEmptySetCodec} class.
     *
     * @param elementcodec The {@link Codec} that will be used to encode and decode elements from the set.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public NonEmptySetCodec(Codec<T> elementcodec) throws ArgumentNullException { super(elementcodec); }

    @Override
    protected Set<T> Transform(Collection<T> c)
    {
        if (c.isEmpty()) {
            throw new InvalidOperationException("Set must not be empty.");
        } else {
            return super.Transform(c);
        }
    }
}
