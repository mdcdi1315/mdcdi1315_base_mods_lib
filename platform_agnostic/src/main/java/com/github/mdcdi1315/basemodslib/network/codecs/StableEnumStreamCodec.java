package com.github.mdcdi1315.basemodslib.network.codecs;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.network.NetworkHelpers;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;

import java.nio.charset.StandardCharsets;

/**
 * A class that de/encodes values of the specified enumeration class based on the value's constant name. <br />
 * Note that while this one is not affected by constant re-ordering, it can still be an issue if any of the constant values of the enum class have their name changed.
 * @param <T> The type of the enumeration class to de/encode.
 * @since 1.0.18
 */
public final class StableEnumStreamCodec<T extends Enum<T>>
    extends EnumStreamCodec<T>
{
    /**
     * Creates a new instance of the {@link StableEnumStreamCodec} class.
     *
     * @param enum_class The class of the enumeration type to be used for it's de/encoding metadata.
     * @throws ArgumentNullException {@code enum_class} is {@code null}.
     */
    public StableEnumStreamCodec(Class<T> enum_class) throws ArgumentNullException { super(enum_class); }

    @Override
    public T decode(ByteBuf buffer) {
        int len = NetworkHelpers.Read7BitEncodedIntUnsafe(buffer);
        byte[] bytes = new byte[len];
        buffer.slice(buffer.readerIndex(), len).readBytes(bytes);
        return T.valueOf(enum_class, new String(bytes, StandardCharsets.UTF_8));
    }

    @Override
    public void encode(ByteBuf o, T t)
    {
        String n = t.name();
        if (n.length() > 32767) {
            throw new EncoderException(StringUtils.Format("String for encoding the enumeration constant of type {0} is too long: {1} characters found.", enum_class.toString(), n.length()));
        } else {
            byte[] d = n.getBytes(StandardCharsets.UTF_8);
            NetworkHelpers.Write7BitEncodedIntUnsafe(o, d.length);
            o.writeBytes(d);
        }
    }
}
