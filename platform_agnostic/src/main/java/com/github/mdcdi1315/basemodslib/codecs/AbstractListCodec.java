package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;

import java.util.*;
import java.util.stream.Stream;

/**
 * Provides the base implementation class for list codecs. <br />
 * Deriving classes are free to change the all the implementation details of this class. <br />
 * By default, the decoding method lazily decodes the list, ignoring any erroring elements by the codec.
 * @param <TElement> The type of the elements to decode.
 * @param <TListType> The type of the list to return.
 */
public abstract class AbstractListCodec<TElement, TListType extends List<TElement>>
    implements Codec<TListType>
{
    /**
     * Gets the codec that can de/encode single elements of a list.
     */
    protected final Codec<TElement> element;

    /**
     * Initializes a new instance of the {@link AbstractListCodec} class.
     * @param elementcodec The {@link Codec} that will be used to encode and decode elements from the list.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public AbstractListCodec(Codec<TElement> elementcodec)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(elementcodec , "elementcodec");
        element = elementcodec;
    }

    /**
     * Gets a list containing the decoded elements and returns the converted list
     * @param list The list to transform to type {@link TListType}.
     * @return The transformed list object.
     * @apiNote If using the default {@link AbstractListCodec#decode(DynamicOps, Object)} implementation, this invocation can also throw any exception class derived from the {@link Exception} class.
     * The exception will be converted to an error and that will be returned back instead.
     */
    protected abstract TListType Transform(List<TElement> list);

    /**
     * Decodes a list from the specified dynamic ops and serialized input.
     * @param ops The dynamic ops object to use.
     * @param input The serialized input to decode the list from.
     * @return A result object indicating success or failure. On success, it returns the decoded list object.
     * @param <T> The type of the input to decode.
     */
    @Override
    public <T> DataResult<Pair<TListType, T>> decode(DynamicOps<T> ops, T input)
    {
        DataResult<Stream<T>> d = ops.getStream(input);

        Optional<Stream<T>> s;

        if ((s = d.result()).isPresent()) {
            ArrayList<TElement> list = new ArrayList<>(10);
            DataResult<Pair<TElement , T>> dr; // We need the reference to this to get both the result and the error.
            Optional<Pair<TElement , T>> result;
            Iterator<T> itr = s.get().iterator();
            while (itr.hasNext()) {
                dr = element.decode(ops , itr.next());
                if ((result = dr.result()).isPresent()) {
                    list.add(result.get().getFirst());
                } else {
                    BaseModsLib.LOGGER.warn("Element decode failed: {}. This element will be ignored.", dr.error().get().message());
                }
            }
            try {
                // We are not interested whether we will return an empty list.
                return DataResult.success(Pair.of(Transform(list), input));
            } catch (Exception e) {
                // Exceptions should be wrapped as errors because validation errors may have been found.
                return CodecUtils.CreateJavaFormattedErrorDataResult("Exception of type %s occurred: %s", e.getClass().getName() , e.getMessage());
            }
        } else {
            return DataResult.error(d.error().get().messageSupplier());
        }
    }

    /**
     * Encodes a list from the specified object, dynamic ops and the currently written data so far.
     * @param input The list to encode.
     * @param ops The dynamic ops object to use.
     * @param prefix The currently written data where this list will be encoded to.
     * @return A result object indicating success or failure. On success, it returns the encoded object.
     * @param <T> The type of the result to produce.
     */
    @Override
    public <T> DataResult<T> encode(TListType input, DynamicOps<T> ops, T prefix)
    {
        final ListBuilder<T> builder = ops.listBuilder();

        for (final TElement a : input) {
            builder.add(element.encodeStart(ops, a));
        }

        return builder.build(prefix);
    }

    /**
     * Decodes the list and instead of returning a pair having both errors and the list, it returns only the list. <br />
     * Internally, a mapping happens to get the object. The error is elsewise retained due to the data result semantics.
     * @param ops The data de/encoder to use.
     * @param input the input data to decode.
     * @return The decoded data.
     * @param <T> The type of data supported by the data de/encoder.
     */
    public final <T> DataResult<TListType> DecodeSimple(DynamicOps<T> ops , T input) {
        return decode(ops , input).map(Pair::getFirst);
    }

    /**
     * Determines whether this list codec and the specified object are equal.
     * @param o The reference object with which to compare.
     * @return A value determining equality of both objects.
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof AbstractListCodec<? , ?> lc && Objects.equals(element, lc.element);
    }

    /**
     * Computes the hash code for this list codec.
     * @return The computed hash code, which is a value deriving from the element codec.
     */
    @Override
    public int hashCode() {
        return Objects.hash(element);
    }

    /**
     * Gets a string describing this list codec.
     * @return A string describing this list codec.
     */
    @Override
    public String toString() {
        return String.format("ListCodec[%s]" , element);
    }
}
