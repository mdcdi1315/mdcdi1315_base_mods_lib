package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.google.common.collect.ImmutableList;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.*;
import java.util.stream.Stream;

/**
 * Provides the base implementation codec for strictly decoding sets <br />
 * (That is, if an element set decoding, the entire decoding process sets).
 * @param <TElement> The type of the elements to decode.
 * @param <TSetType> The type of the set to return.
 * @since 1.0.19
 */
public abstract class AbstractStrictSetCodec<TElement, TSetType extends Set<TElement>>
    extends AbstractSetCodec<TElement, TSetType>
{
    /**
     * Initializes a new instance of the {@link AbstractStrictSetCodec} class.
     *
     * @param elementcodec The {@link Codec} that will be used to encode and decode elements from the set.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public AbstractStrictSetCodec(Codec<TElement> elementcodec) throws ArgumentNullException { super(elementcodec); }

    /**
     * Decodes a set from the specified dynamic ops and serialized input. <br />
     * Decoding is done strictly, meaning that decoding will fail when any set element failed to be decoded.
     * @param ops The dynamic ops object to use.
     * @param input The serialized input to decode the set from.
     * @return A result object indicating success or failure. On success, it returns the decoded set object.
     * @param <T> The type of the input to decode.
     */
    // Override the decode method to define decoding strictness.
    @Override
    public <T> DataResult<Pair<TSetType, T>> decode(DynamicOps<T> ops, T input)
    {
        DataResult<Stream<T>> d = ops.getStream(input);

        Optional<Stream<T>> s;

        if ((s = d.result()).isPresent()) {
            Iterator<T> itr = s.get().iterator();
            ImmutableList.Builder<TElement> builder = ImmutableList.builder();
            DataResult<Pair<TElement , T>> dr; // We need the reference to this to get both the result and the error.
            Optional<DataResult.Error<Pair<TElement , T>>> err = Optional.empty();
            // Loop will break if at least one element has failed the decode process
            while (itr.hasNext() && (err = (dr = element.decode(ops , itr.next())).error()).isEmpty()) {
                builder.add(dr.result().get().getFirst());
            }
            if (err.isPresent()) {
                // OK, an element failed decode.
                // Return the failure back.
                return DataResult.error(new StringSupplier(err.get().message()));
            } else {
                try {
                    // We can just return the set directly.
                    return DataResult.success(Pair.of(Transform(builder.build()), input));
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
