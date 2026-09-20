package com.github.mdcdi1315.basemodslib.utils.random.weighted;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;

import java.util.stream.Stream;

/**
 * Provides a {@link Codec} that can de/encode {@link WeightedList} instances.
 * @param <T> The type of the elements that the weighted list can use to de/encode instances.
 */
public final class WeightedListCodec<T extends IWeightedEntry>
    implements Codec<WeightedList<T>>
{
    private final Codec<T> element_codec;

    /**
     * Creates a new instance of the {@link WeightedListCodec} class.
     * @param element_codec The {@link Codec} that is able to de/encode the individual elements of the list.
     * @throws ArgumentNullException {@code element_codec} is {@code null}.
     */
    public WeightedListCodec(Codec<T> element_codec)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(element_codec, "element_codec");
        this.element_codec = element_codec;
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public <TO> DataResult<Pair<WeightedList<T>, TO>> decode(DynamicOps<TO> ops, TO input)
    {
        DataResult<Stream<TO>> drs = ops.getStream(input);
        if (drs.isError()) {
            return CodecUtils.CreateDotNetFormattedErrorDataResult(
                    "Could not decode the list input because the input could not be decoded: \nError details: {0}",
                    drs.error().get().message()
            );
        } else {
            Stream<TO> stream = drs.result().get();

            var itr = stream.iterator();

            var list = new List<T>(20);

            DataResult<Pair<T, TO>> item_dr;
            while (itr.hasNext())
            {
                item_dr = element_codec.decode(ops, itr.next());
                if (item_dr.isError()) {
                    return CodecUtils.CreateDotNetFormattedErrorDataResult(
                            "An element failed to be decoded: {0}",
                            item_dr.error().get().message()
                    );
                } else {
                    list.Add(item_dr.result().get().getFirst());
                }
            }

            list.TrimExcess();

            return DataResult.success(new Pair<>(new WeightedList<>(list), input));
        }
    }

    @Override
    public <TO> DataResult<TO> encode(WeightedList<T> input, DynamicOps<TO> ops, TO prefix)
    {
        ListBuilder<TO> builder = ops.listBuilder();

        try (var enumerator = input.GetEnumerator())
        {
            while (enumerator.MoveNext())
            {
                builder.add(element_codec.encode(enumerator.getCurrent(), ops, ops.empty()));
            }
        }

        return builder.build(prefix);
    }
}
