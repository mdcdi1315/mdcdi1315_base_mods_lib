package com.github.mdcdi1315.basemodslib.config.lowlevelapi.listsupport;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.mojang.serialization.Codec;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides a registry that retains codecs for de/serializing {@link com.github.mdcdi1315.basemodslib.config.ConfigList} objects.
 * @since 1.0.15
 */
public final class ListElementRegistry
{
    private ListElementRegistry() {}

    private static final ConcurrentHashMap<Class<?> , Codec<?>> serializers;

    static {
        serializers = new ConcurrentHashMap<>(10);
    }

    private static Codec<?> ResolveCodecOfPrimitive(Class<?> cls)
    {
        if (cls == String.class) {
            return Codec.STRING;
        } else if (cls == Boolean.class || cls == boolean.class) {
            return Codec.BOOL;
        } else if (cls == Byte.class || cls == byte.class) {
            return Codec.BYTE;
        } else if (cls == Short.class || cls == short.class) {
            return Codec.SHORT;
        } else if (cls == Integer.class || cls == int.class) {
            return Codec.INT;
        } else if (cls == Long.class || cls == long.class) {
            return Codec.LONG;
        } else if (cls == Float.class || cls == float.class) {
            return Codec.FLOAT;
        } else if (cls == Double.class || cls == double.class) {
            return Codec.DOUBLE;
        } else {
            return null;
        }
    }

    public static <T> void RegisterCodec(Class<T> ser_class, Codec<T> ser_codec)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(ser_class);
        ArgumentNullException.ThrowIfNull(ser_codec);
        if (serializers.putIfAbsent(ser_class, ser_codec) != null) {
            throw new InvalidOperationException(StringUtils.Format("The class of name {0} has been already registered." , ser_class.getName()));
        }
    }

    @MaybeNull
    public static <T> Codec<T> FindCodec(Class<T> cls)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(cls, "cls");
        Codec<?> c;
        return (Codec<T>) (((c = serializers.get(cls)) == null) ? ResolveCodecOfPrimitive(cls) : c);
    }
}
