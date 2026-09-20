package com.github.mdcdi1315.basemodslib.utils.collections.codec_support;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.KeyValuePair;

import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;
import com.github.mdcdi1315.basemodslib.codecs.DataResultBuilder;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.*;

/**
 * Provides a {@link Codec} that can de/encode {@link KeyValuePair} instances.
 * @param <TKey> The type of the key to be de/encoded as the key of the pair.
 * @param <TValue> The type of the value to be de/encoded as the value of the pair.
 */
public final class KeyValuePairCodec<TKey, TValue>
    implements Codec<KeyValuePair<TKey, TValue>>
{
    private final Codec<TKey> key_codec;
    private final Codec<TValue> value_codec;
    private final String key_field_name, value_field_name;

    /**
     * Initializes a new instance of the {@link KeyValuePairCodec} class. <br />
     * The field names for the key and for the value are {@code key} and {@code value}, respectively.
     * @param key_codec The instance that can de/encode the key part of the pair.
     * @param value_codec The instance that can de/encode the value part of the pair.
     * @throws ArgumentNullException {@code key_codec} and/or {@code value_codec} are {@code null}.
     */
    public KeyValuePairCodec(Codec<TKey> key_codec, Codec<TValue> value_codec)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key_codec, "key_codec");
        ArgumentNullException.ThrowIfNull(value_codec, "value_codec");
        this.key_codec = key_codec;
        this.key_field_name = "key";
        this.value_codec = value_codec;
        this.value_field_name = "value";
    }

    /**
     * Initializes a new instance of the {@link KeyValuePairCodec} class.
     * @param key_codec The instance that can de/encode the key part of the pair.
     * @param value_codec The instance that can de/encode the value part of the pair.
     * @param key_field_name The name of the key field in the created map.
     * @param value_field_name The name of the value field in the created map.
     * @throws ArgumentNullException {@code key_codec} and/or {@code value_codec} and/or {@code key_field_name} and/or {@code value_field_name} are {@code null}.
     * @throws ArgumentException {@code key_field_name} and/or {@code value_field_name} are empty strings (&quot;&quot;).
     */
    public KeyValuePairCodec(Codec<TKey> key_codec, Codec<TValue> value_codec, String key_field_name, String value_field_name)
            throws ArgumentNullException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(key_codec, "key_codec");
        ArgumentNullException.ThrowIfNull(value_codec, "value_codec");
        ArgumentNullException.ThrowIfNullOrEmpty(key_field_name, "key_field_name");
        ArgumentNullException.ThrowIfNullOrEmpty(value_field_name, "value_field_name");
        this.key_codec = key_codec;
        this.value_codec = value_codec;
        this.key_field_name = key_field_name;
        this.value_field_name = value_field_name;
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public <T> DataResult<Pair<KeyValuePair<TKey, TValue>, T>> decode(DynamicOps<T> ops, T input)
    {
        DataResult<MapLike<T>> like_result = ops.getMap(input);
        if (like_result.isError()) {
            return CodecUtils.CreateDotNetFormattedErrorDataResult(
                    "Input is not a map: {0}",
                    like_result.error().get().message()
            );
        } else {
            MapLike<T> like = like_result.result().get();
            T key_encoded = like.get(key_field_name);
            if (key_encoded == null) {
                return CodecUtils.CreateDotNetFormattedErrorDataResult(
                        "Cannot find key field named as '{0}' in the specified map.\nMap: {1}",
                        key_field_name,
                        like
                );
            } else {
                T value_encoded = like.get(value_field_name);
                if (value_encoded == null) {
                    return CodecUtils.CreateDotNetFormattedErrorDataResult(
                            "Cannot find value field named as '{0}' in the specified map.\nMap: {1}",
                            value_field_name,
                            like
                    );
                } else {
                    return DataResultBuilder.Of(
                            key_codec.parse(ops, key_encoded),
                            value_codec.parse(ops, value_encoded),
                            KeyValuePair::new
                    ).WithOpsInput(input).Build();
                }
            }
        }
    }

    @Override
    public <T> DataResult<T> encode(KeyValuePair<TKey, TValue> input, DynamicOps<T> ops, T prefix)
    {
        KeyValuePair.ValidateNonNullStructure(input);

        T empty_instance = ops.empty();
        RecordBuilder<T> record = ops.mapBuilder();

        record.add(key_field_name, key_codec.encode(input.getKey(), ops, empty_instance));
        record.add(value_field_name, value_codec.encode(input.getValue(), ops, empty_instance));

        return record.build(prefix);
    }
}
