package com.github.mdcdi1315.basemodslib.network.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.network.NetworkHelpers;

import io.netty.buffer.ByteBuf;

/**
 * A class that de/encodes values of the specified enumeration class based on the value's ordinal constant.
 * <h3>Remarks:</h3> <br />
 * This class de/encodes using the enumeration's ordinal constants to de/encode the desired enumeration constant. <br />
 * This is unsafe and dangerous in some cases. <br />
 * The {@link NetworkHelpers#WriteEnumUnsafe(ByteBuf, Enum)} method discusses the pitfalls of using this.
 * @since 1.0.18
 * @param <T> The type of the enumeration class to de/encode.
 */
public final class UnstableEnumStreamCodec<T extends Enum<T>>
    extends EnumStreamCodec<T>
{
    /**
     * Creates a new instance of the {@link UnstableEnumStreamCodec} class.
     *
     * @param enum_class The class of the enumeration type to be used for it's de/encoding metadata.
     * @throws ArgumentNullException {@code enum_class} is {@code null}.
     */
    public UnstableEnumStreamCodec(Class<T> enum_class) throws ArgumentNullException { super(enum_class); }

    @Override
    public T decode(ByteBuf byteBuf) { return NetworkHelpers.ReadEnumUnsafe(byteBuf, enum_class); }

    @Override
    public void encode(ByteBuf o, T t) { NetworkHelpers.WriteEnumUnsafe(o, t); }
}
