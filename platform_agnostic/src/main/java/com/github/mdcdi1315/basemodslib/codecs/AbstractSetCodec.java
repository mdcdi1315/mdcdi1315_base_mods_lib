package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.google.common.collect.ImmutableList;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;

import java.util.*;
import java.util.stream.Stream;

/**
 * Like {@link AbstractListCodec}, this provides a way for creating set codecs.
 * @param <TElement> The type of elements that the set will contain.
 * @param <TSetType> The type of the set to be returned.
 * @since 1.0.19
 */
public abstract class AbstractSetCodec<TElement, TSetType extends Set<TElement>>
    implements Codec<TSetType>
{
    /**
     * Gets the codec that can de/encode single elements of a set.
     */
    protected final Codec<TElement> element;

    /**
     * Initializes a new instance of the {@link AbstractSetCodec} class.
     * @param elementcodec The {@link Codec} that will be used to encode and decode elements from the set.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public AbstractSetCodec(Codec<TElement> elementcodec) throws ArgumentNullException { ArgumentNullException.ThrowIfNull(element = elementcodec, "elementcodec"); }

    /**
     * Gets a set containing the decoded elements and returns the converted set.
     * @param c A collection that contains the elements to transform to {@link TSetType}.
     * @return The transformed set object.
     * @apiNote If using the default {@link AbstractListCodec#decode(DynamicOps, Object)} implementation, this invocation can also throw any exception class derived from the {@link Exception} class.
     * The exception will be converted to an error and that will be returned back instead.
     */
    protected abstract TSetType Transform(Collection<TElement> c);

    /**
     * Decodes a set from the specified dynamic ops and serialized input.
     * @param ops The dynamic ops object to use.
     * @param input The serialized input to decode the set from.
     * @return A result object indicating success or failure. On success, it returns the decoded set object.
     * @param <T> The type of the input to decode.
     */
    @Override
    public <T> DataResult<Pair<TSetType, T>> decode(DynamicOps<T> ops, T input)
    {
        DataResult<Stream<T>> d = ops.getStream(input);

        Optional<Stream<T>> s;

        if ((s = d.result()).isPresent()) {
            ImmutableList.Builder<TElement> builder = ImmutableList.builder();
            DataResult<Pair<TElement , T>> dr; // We need the reference to this to get both the result and the error.
            Optional<Pair<TElement , T>> result;
            Iterator<T> itr = s.get().iterator();
            while (itr.hasNext()) {
                dr = element.decode(ops , itr.next());
                if ((result = dr.result()).isPresent()) {
                    builder.add(result.get().getFirst());
                } else {
                    BaseModsLib.LOGGER.warn("Element decode failed: {}. This element will be ignored.", dr.error().get().message());
                }
            }
            try {
                // We are not interested whether we will return an empty set.
                return DataResult.success(Pair.of(Transform(builder.build()), input));
            } catch (Exception e) {
                // Exceptions should be wrapped as errors because validation errors may have been found.
                return DataResult.error(StringSupplier.FromFormatted("Exception of type %s occurred: %s", e.getClass().getName() , e.getMessage()));
            }
        } else {
            return DataResult.error(new StringSupplier(d.error().get().message()));
        }
    }

    /**
     * Encodes a set from the specified object, dynamic ops and the currently written data so far.
     * @param input The set to encode.
     * @param ops The dynamic ops object to use.
     * @param prefix The currently written data where this set will be encoded to.
     * @return A result object indicating success or failure. On success, it returns the encoded object.
     * @param <T> The type of the result to produce.
     */
    @Override
    public <T> DataResult<T> encode(TSetType input, DynamicOps<T> ops, T prefix)
    {
        final ListBuilder<T> builder = ops.listBuilder();

        for (final TElement a : input) {
            builder.add(element.encodeStart(ops, a));
        }

        return builder.build(prefix);
    }


    /**
     * Decodes the set and instead of returning a pair having both errors and the set, it returns only the set. <br />
     * Internally, a mapping happens to get the object. The error is elsewise retained due to the data result semantics.
     * @param ops The data de/encoder to use.
     * @param input the input data to decode.
     * @return The decoded data.
     * @param <T> The type of data supported by the data de/encoder.
     */
    public final <T> DataResult<TSetType> DecodeSimple(DynamicOps<T> ops , T input) {
        return decode(ops , input).map(Pair::getFirst);
    }

    /**
     * Determines whether this set codec and the specified object are equal.
     * @param o The reference object with which to compare.
     * @return A value determining equality of both objects.
     */
    @Override
    public boolean equals(Object o) { return o instanceof AbstractSetCodec<? , ?> lc && Objects.equals(element, lc.element); }

    /**
     * Computes the hash code for this set codec.
     * @return The computed hash code, which is a value deriving from the element codec.
     */
    @Override
    public int hashCode() { return Objects.hash(element); }

    /**
     * Gets a string describing this set codec.
     * @return A string describing this set codec.
     */
    @Override
    public String toString() { return String.format("SetCodec[%s]" , element); }
}
