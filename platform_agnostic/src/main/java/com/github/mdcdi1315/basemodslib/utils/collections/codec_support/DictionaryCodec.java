package com.github.mdcdi1315.basemodslib.utils.collections.codec_support;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.KeyValuePair;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides a base class for building a dictionary-like codec based on {@link IEnumerable}. <br />
 * This class must be inherited.
 * @param <TKey> The type of the keys in the dictionary-like object.
 * @param <TValue> The type of the values in the dictionary-like object.
 * @param <TC> The exact type of the {@link IEnumerable} instance.
 */
public abstract class DictionaryCodec<
    TKey,
    TValue,
    TC extends IEnumerable<KeyValuePair<TKey, TValue>>
>
    implements Codec<TC>
{
    private final Codec<TKey> key_codec;
    private final Codec<TValue> value_codec;

    /**
     * Initializes a new instance of the {@link DictionaryCodec} class.
     * @param key_codec The {@link Codec} that can de/encode keys of type {@link TKey}.
     * @param value_codec The {@link Codec} that can de/encode values of type {@link TValue}.
     * @throws ArgumentNullException {@code key_codec} and/or {@code value_codec} {@code null}.
     */
    public DictionaryCodec(Codec<TKey> key_codec, Codec<TValue> value_codec)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key_codec, "key_codec");
        ArgumentNullException.ThrowIfNull(value_codec, "value_codec");
        this.key_codec = key_codec;
        this.value_codec = value_codec;
    }

    /**
     * Creates a new, writeable, dictionary instance.
     * @return The new, writeable dictionary instance.
     */
    @NotNull
    protected abstract TC CreateCollection();

    /**
     * Adds a new element to the dictionary instance.
     * @param collection_inst The dictionary instance to add the element to.
     * @param element The element to be added to {@code collection_inst}.
     */
    protected abstract void AddElement(@DisallowNull TC collection_inst, @DisallowNull KeyValuePair<TKey, TValue> element);

    /**
     * Optional. Finalize the collection after it was filled in, if so required.
     * @param collection The collection instance to finalize.
     */
    protected void FinalizeCollection(@DisallowNull TC collection) { }

    @NotNull
    @Override
    public final <T> DataResult<TC> parse(@DisallowNull Dynamic<T> input)
    {
        TC collection_inst = CreateCollection();
        String error = CollectionCodecUtils.DecodeDictionary(input.getOps(), input.getValue(), key_codec, value_codec, new CollectionActionInstance<>(collection_inst, this::AddElement));
        if (error == null) {
            FinalizeCollection(collection_inst);
            return DataResult.success(collection_inst);
        } else {
            return CodecUtils.CreateErrorDataResult(error);
        }
    }

    @NotNull
    @Override
    public final <TO> DataResult<TC> parse(DynamicOps<TO> ops, TO input)
    {
        TC collection_inst = CreateCollection();
        String error = CollectionCodecUtils.DecodeDictionary(ops, input, key_codec, value_codec, new CollectionActionInstance<>(collection_inst, this::AddElement));
        if (error == null) {
            FinalizeCollection(collection_inst);
            return DataResult.success(collection_inst);
        } else {
            return CodecUtils.CreateErrorDataResult(error);
        }
    }

    @NotNull
    @Override
    public final <TO> DataResult<Pair<TC, TO>> decode(DynamicOps<TO> ops, TO input)
    {
        TC collection_inst = CreateCollection();
        String error = CollectionCodecUtils.DecodeDictionary(ops, input, key_codec, value_codec, new CollectionActionInstance<>(collection_inst, this::AddElement));
        if (error == null) {
            FinalizeCollection(collection_inst);
            return DataResult.success(new Pair<>(collection_inst, input));
        } else {
            return CodecUtils.CreateErrorDataResult(error);
        }
    }

    @NotNull
    @Override
    public final <T> DataResult<T> encodeStart(DynamicOps<T> ops, TC input) { return CollectionCodecUtils.EncodeDictionary(ops, ops.empty(), key_codec, value_codec, input); }

    @NotNull
    @Override
    public final <TO> DataResult<TO> encode(TC input, DynamicOps<TO> ops, TO prefix) { return CollectionCodecUtils.EncodeDictionary(ops, prefix, key_codec, value_codec, input); }


}
