package com.github.mdcdi1315.basemodslib.utils.collections.codec_support;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Provides a base class for building a codec based on {@link IEnumerable}. <br />
 * This class must be inherited.
 * @param <T> The type of the elements that the {@link IEnumerable} holds.
 * @param <TC> The exact type of the {@link IEnumerable} instance.
 */
public abstract class EnumerableCodec<T, TC extends IEnumerable<T>>
    implements Codec<TC>
{
    private final Codec<T> element_codec;

    /**
     * Initializes a new instance of the {@link EnumerableCodec} class.
     * @param element_codec The {@link Codec} that can de/encode elements of type {@link T}.
     * @throws ArgumentNullException {@code element_codec} is {@code null}.
     */
    protected EnumerableCodec(Codec<T> element_codec)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(element_codec, "element_codec");
        this.element_codec = element_codec;
    }

    /**
     * Creates a new, writeable, collection instance.
     * @return The new, writeable collection instance.
     */
    @NotNull
    protected abstract TC CreateCollection();

    /**
     * Adds a new element to the collection instance.
     * @param collection_inst The collection instance to add the element to.
     * @param element The element to be added to {@code collection_inst}.
     */
    protected abstract void AddElement(@DisallowNull TC collection_inst, @AllowNull T element);

    /**
     * Optional. Finalize the collection after it was filled in, if so required.
     * @param collection The collection instance to finalize.
     */
    protected void FinalizeCollection(@DisallowNull TC collection) { }

    @NotNull
    @Override
    public <TO> DataResult<TC> parse(@DisallowNull Dynamic<TO> input)
    {
        TC collection_inst = CreateCollection();
        String error = CollectionCodecUtils.DecodeEnumerable(input.getOps(), input.getValue(), element_codec, new CollectionActionInstance<>(collection_inst, this::AddElement));
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
        String error = CollectionCodecUtils.DecodeEnumerable(ops, input, element_codec, new CollectionActionInstance<>(collection_inst, this::AddElement));
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
        String error = CollectionCodecUtils.DecodeEnumerable(ops, input, element_codec, new CollectionActionInstance<>(collection_inst, this::AddElement));
        if (error == null) {
            FinalizeCollection(collection_inst);
            return DataResult.success(new Pair<>(collection_inst, input));
        } else {
            return CodecUtils.CreateErrorDataResult(error);
        }
    }

    @NotNull
    @Override
    public final <TO> DataResult<TO> encodeStart(DynamicOps<TO> ops, TC input) { return CollectionCodecUtils.EncodeEnumerable(ops, ops.empty(), element_codec, input); }

    @NotNull
    @Override
    public final <TO> DataResult<TO> encode(TC input, DynamicOps<TO> ops, TO prefix) { return CollectionCodecUtils.EncodeEnumerable(ops, prefix, element_codec, input); }
}
