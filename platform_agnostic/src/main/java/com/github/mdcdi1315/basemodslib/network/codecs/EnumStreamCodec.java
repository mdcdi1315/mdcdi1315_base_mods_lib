package com.github.mdcdi1315.basemodslib.network.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.StreamCodec;

/**
 * Provides the base class for a stream codec that can de/encode enumeration types.
 * @param <T> The type of the enumeration class to be de/encoded.
 * @since 1.0.18
 */
public abstract class EnumStreamCodec<T extends Enum<T>>
    implements StreamCodec<ByteBuf, T>
{
    /**
     * Provides the {@link Class} of type {@link T} of the enumeration.
     */
    protected final Class<T> enum_class;

    /**
     * Creates a new instance of the {@link EnumStreamCodec} class.
     * @param enum_class The class of the enumeration type to be used for it's de/encoding metadata.
     * @throws ArgumentNullException {@code enum_class} is {@code null}.
     */
    public EnumStreamCodec(Class<T> enum_class)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.enum_class = enum_class, "enum_class");
    }
}
