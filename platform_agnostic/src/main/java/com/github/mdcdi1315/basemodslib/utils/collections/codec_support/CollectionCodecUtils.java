package com.github.mdcdi1315.basemodslib.utils.collections.codec_support;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.KeyValuePair;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.*;

import java.util.stream.Stream;

/**
 * Internal class providing common methods for working with collections and codecs.
 */
final class CollectionCodecUtils
{
    private CollectionCodecUtils() {}

    @MaybeNull
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static <TE, TO> String DecodeEnumerable(DynamicOps<TO> ops, TO input, Decoder<TE> decoder, Action1<TE> element)
    {
        DataResult<Stream<TO>> enumerable_elements = ops.getStream(input);
        if (enumerable_elements.isError()) {
            return StringUtils.Format(
                    "The collection cannot be decoded because the encoded type is not a list. \nError details: {0}",
                    enumerable_elements.error().get().message()
            );
        } else {
            var iterator = enumerable_elements.result().get().iterator();

            DataResult<Pair<TE, TO>> decoded_element;
            while (iterator.hasNext())
            {
                decoded_element = decoder.decode(ops, iterator.next());
                if (decoded_element.isError()) {
                    return StringUtils.Format(
                            "An element in the collection failed to be decoded: {0}",
                            decoded_element.error().get().message()
                    );
                } else {
                    element.action(decoded_element.result().get().getFirst());
                }
            }

            return null;
        }
    }

    @NotNull
    public static <TE, TO> DataResult<TO> EncodeEnumerable(DynamicOps<TO> ops, TO prefix, Encoder<TE> encoder, IEnumerable<TE> enumerable)
    {
        ListBuilder<TO> builder = ops.listBuilder();

        if (!CollectionManipulations.IsEmpty(enumerable))
        {
            TO empty_instance = ops.empty();
            try (var enumerator = enumerable.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    builder.add(encoder.encode(enumerator.getCurrent(), ops, empty_instance));
                }
            }
        }

        return builder.build(prefix);
    }

    @MaybeNull
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static <TKey, TValue, TO> String DecodeDictionary(DynamicOps<TO> ops, TO input, Decoder<TKey> key_decoder, Decoder<TValue> value_decoder, Action1<KeyValuePair<TKey, TValue>> element)
    {
        DataResult<MapLike<TO>> like_result = ops.getMap(input);
        if (like_result.isError()) {
            return StringUtils.Format(
                    "The dictionary cannot be decoded because the encoded type is not a dictionary. \nError details: {0}",
                    like_result.error().get().message()
            );
        } else {
            var iterator = like_result.result().get().entries().iterator();

            Pair<TO, TO> pair;
            DataResult<Pair<TKey, TO>> key;
            DataResult<Pair<TValue, TO>> value;
            while (iterator.hasNext())
            {
                pair = iterator.next();
                key = key_decoder.decode(ops, pair.getFirst());
                if (key.isError()) {
                    return StringUtils.Format(
                            "The key of an element in the dictionary failed to be decoded: {0}",
                            key.error().get().message()
                    );
                } else {
                    value = value_decoder.decode(ops, pair.getSecond());
                    if (value.isError()) {
                        return StringUtils.Format(
                                "The value of an element in the dictionary failed to be decoded: {0}",
                                value.error().get().message()
                        );
                    } else {
                        element.action(new KeyValuePair<>(key.result().get().getFirst(), value.result().get().getFirst()));
                    }
                }
            }

            return null;
        }
    }

    @NotNull
    public static <TKey, TValue, TO> DataResult<TO> EncodeDictionary(DynamicOps<TO> ops, TO prefix, Encoder<TKey> key_encoder, Encoder<TValue> value_encoder, IEnumerable<KeyValuePair<TKey, TValue>> dictionary)
    {
        RecordBuilder<TO> record = ops.mapBuilder();

        if (!CollectionManipulations.IsEmpty(dictionary))
        {
            TO empty_instance = ops.empty();
            try (var enumerator = dictionary.GetEnumerator())
            {
                KeyValuePair<TKey, TValue> pair;
                while (enumerator.MoveNext())
                {
                    pair = enumerator.getCurrent();
                    record.add(
                            key_encoder.encode(pair.getKey(), ops, empty_instance),
                            value_encoder.encode(pair.getValue(), ops, empty_instance)
                    );
                }
            }
        }

        return record.build(prefix);
    }
}
