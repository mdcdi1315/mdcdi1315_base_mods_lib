package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Set;

/**
 * Defines a codec that does decode either using the element codec,
 * or into a list codec if the data to decode denote to be such. <br />
 * If a single element is described, the result is returned as a set containing the decoded element. <br />
 * Encoding is always done directly into a set to ensure further type safety.
 * @param <TElement> The type of the element to decode.
 * @since 1.0.19
 */
public final class SingleElementOrSetCodec<TElement>
    extends SetCodec<TElement>
{
    /**
     * Initializes a new instance of the {@link SingleElementOrSetCodec} class.
     *
     * @param elementcodec The {@link Codec} that will be used to encode and decode elements from the set.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public SingleElementOrSetCodec(Codec<TElement> elementcodec) throws ArgumentNullException { super(elementcodec); }

    /**
     * Decodes a set from the specified dynamic ops and serialized input. <br />
     * If a single element was found and was decoded successfully, it will be wrapped into a list object and that will be returned instead.
     * @param ops The dynamic ops object to use.
     * @param input The serialized input to decode the list from.
     * @return A result object indicating success or failure. On success, it returns the decoded list object.
     * @param <T> The type of the input to decode.
     */
    @Override
    @SuppressWarnings("all")
    public <T> DataResult<Pair<Set<TElement>, T>> decode(DynamicOps<T> ops, T input)
    {
        DataResult<Pair<TElement , T>> single = element.decode(ops , input);

        var e = single.error();

        if (e.isPresent()) {
            // Try with the list codec instead.
            DataResult<Pair<Set<TElement> , T>> set = super.decode(ops , input);

            var e2 = set.error();

            return e2.isPresent() ?
                    CodecUtils.CreateJavaFormattedErrorDataResult("Deserialization failed. \nSingle element codec failed with: %s \nList codec failed with: %s \n" , e.get().message() , e2.get().message()):
                    set;
        } else {
            // Single element decode successful, return the element wrapped in an immutable list.
            // It is OK to call get() on the data result, since either an error or a valid result will only exist, not both.

            var pair = single.result().get();

            return DataResult.success(Pair.of(Set.of(pair.getFirst()) , pair.getSecond()));
        }
    }
}
