package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.*;
import java.util.stream.Stream;

/**
 * Provides the base implementation codec for strictly decoding lists <br />
 * (That is, if an element fails decoding, the entire decoding process fails).
 * @param <TElement> The type of the elements to decode.
 * @param <TListType> The type of the list to return.
 */
public abstract class AbstractStrictListCodec<TElement, TListType extends List<TElement>>
        extends AbstractListCodec<TElement , TListType>
{
    /**
     * Initializes a new instance of the {@link AbstractStrictListCodec} class.
     * @param elementcodec The {@link Codec} that will be used to encode and decode elements from the list.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public AbstractStrictListCodec(Codec<TElement> elementcodec)
        throws ArgumentNullException
    {
        super(elementcodec);
    }

    /**
     * Decodes a list from the specified dynamic ops and serialized input. <br />
     * Decoding is done strictly, meaning that decoding will fail when any list element failed to be decoded.
     * @param ops The dynamic ops object to use.
     * @param input The serialized input to decode the list from.
     * @return A result object indicating success or failure. On success, it returns the decoded list object.
     * @param <T> The type of the input to decode.
     */
    // Override the decode method to define decoding strictness.
    @Override
    public <T> DataResult<Pair<TListType, T>> decode(DynamicOps<T> ops, T input)
    {
        DataResult<Stream<T>> d = ops.getStream(input);

        Optional<Stream<T>> s;

        if ((s = d.result()).isPresent()) {
            Iterator<T> itr = s.get().iterator();
            ArrayList<TElement> list = new ArrayList<>(10);
            DataResult<Pair<TElement , T>> dr; // We need the reference to this to get both the result and the error.
            Optional<DataResult.Error<Pair<TElement , T>>> err = Optional.empty();
            // Loop will break if at least one element has failed the decode process
            while (itr.hasNext() && (err = (dr = element.decode(ops , itr.next())).error()).isEmpty()) {
                list.add(dr.result().get().getFirst());
            }
            if (err.isPresent()) {
                // OK, an element failed decode.
                // Return the failure back.
                return DataResult.error(new StringSupplier(err.get().message()));
            } else {
                try {
                    // We can just return the list directly.
                    return DataResult.success(Pair.of(Transform(list), input));
                } catch (Exception e) {
                    // Exceptions should be wrapped as errors because validation errors may have been found.
                    return DataResult.error(StringSupplier.FromFormatted("Exception of type %s occurred: %s", e.getClass().getName() , e.getMessage()));
                }
            }
        } else {
            return DataResult.error(new StringSupplier(d.error().get().message()));
        }
    }
}