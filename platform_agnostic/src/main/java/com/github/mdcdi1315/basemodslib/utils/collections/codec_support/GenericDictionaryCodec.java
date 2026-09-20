package com.github.mdcdi1315.basemodslib.utils.collections.codec_support;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.Dictionary;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.KeyValuePair;

import com.mojang.serialization.Codec;

/**
 * Provides a default codec implementation for the {@link Dictionary} class. <br />
 * This class can be inherited.
 * @param <TKey> The type of the keys in the dictionary object.
 * @param <TValue> The type of the values in the dictionary object.
 */
public class GenericDictionaryCodec<TKey, TValue>
    extends DictionaryCodec<TKey, TValue, Dictionary<TKey, TValue>>
{
    /**
     * Initializes a new instance of the {@link GenericDictionaryCodec} class.
     *
     * @param key_codec   The {@link Codec} that can de/encode keys of type {@link TKey}.
     * @param value_codec The {@link Codec} that can de/encode values of type {@link TValue}.
     * @throws ArgumentNullException {@code key_codec} and/or {@code value_codec} {@code null}.
     */
    public GenericDictionaryCodec(Codec<TKey> key_codec, Codec<TValue> value_codec)
            throws ArgumentNullException
    {
        super(key_codec, value_codec);
    }

    @Override
    protected Dictionary<TKey, TValue> CreateCollection() { return new Dictionary<>(20); }

    @Override
    protected void FinalizeCollection(Dictionary<TKey, TValue> collection) { collection.TrimExcess(); }

    @Override
    protected void AddElement(Dictionary<TKey, TValue> collection_inst, KeyValuePair<TKey, TValue> element) { collection_inst.Add(element); }
}
