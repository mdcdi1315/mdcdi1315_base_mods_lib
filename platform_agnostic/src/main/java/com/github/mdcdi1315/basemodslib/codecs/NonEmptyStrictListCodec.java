package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.mojang.serialization.Codec;

import java.util.List;

public class NonEmptyStrictListCodec<TElement>
    extends StrictListCodec<TElement>
{

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
