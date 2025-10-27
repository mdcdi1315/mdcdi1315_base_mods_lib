package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;

import com.mojang.serialization.Codec;

import java.util.Map;

/**
 * Defines a pair of values.
 * @param first The first value to store.
 * @param second The second value to store.
 * @param <T1> The type of the first value to store.
 * @param <T2> The type of the second value to store.
 * @apiNote This class should be used instead of the
 * {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.KeyValuePair}
 * class if you need pair variables that you will later assign to them null. <br />
 * KeyValuePair instances should not become null, because they are .NET structure definitions.
 */
@SuppressWarnings("unused")
public record Pair<T1, T2>(@MaybeNull T1 first, @MaybeNull T2 second)
    implements Map.Entry<T1, T2>
{
    /**
     * Creates a key-value pair {@link Codec} of the specified key and value codec. <br />
     * On deserialization, this will produce the requested values. <br />
     * It is represented as an object containing two fields named {@code key} and {@code value} respectively.
     * @param type1codec The codec that can de/serialize the {@linkplain T1} type.
     * @param type2codec The codec that can de/serialize the {@linkplain T2} type.
     * @return A {@link Codec} returning a {@link Pair} of the passed codecs.
     * @param <T1> The first type to be de/serialized. Will be saved at {@link Pair#first} field.
     * @param <T2> The second type to be de/serialized. Will be saved at {@link Pair#second} field.
     * @exception ArgumentNullException {@code type1codec} and/or {@code type2codec} were null.
     * @apiNote In JSON the codec could be represented as: <br />
     * <code>
     *     {
     *         "key": &lt;Value-Of-First-Type&gt;,
     *         "value": &lt;Value-Of-Second-Type&gt;
     *     }
     * </code>
     */
    public static <T1, T2> Codec<Pair<T1 , T2>> CreateKeyValuePairCodec(Codec<T1> type1codec, Codec<T2> type2codec)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(type1codec , "type1codec");
        ArgumentNullException.ThrowIfNull(type2codec , "type2codec");
        return CodecUtils.CreateCodecDirect(
                type1codec.fieldOf("key").forGetter(Pair::first),
                type2codec.fieldOf("value").forGetter(Pair::second),
                Pair::new
        );
    }

    @Override
    public T1 getKey() {
        return first;
    }

    @Override
    public T2 getValue() {
        return second;
    }

    @Override
    public T2 setValue(T2 value) {
        throw new NotSupportedException("Cannot modify the values of a Pair instance.");
    }
}
