package com.github.mdcdi1315.basemodslib.utils.weight;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.StringSupplier;
import com.github.mdcdi1315.basemodslib.codecs.AbstractListCodec;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.*;
import java.util.stream.Stream;

/**
 * Provides the base class for codecs that need to de/encode into {@link WeightedEntryList} derivants.
 * @param <T> The type of the weighted entries to be stored.
 * @param <TL> The type of the weighted entry list to be returned.
 */
public abstract class WeightedEntryListCodec<T extends IWeightedEntry, TL extends WeightedEntryList<T>>
    extends AbstractListCodec<T, TL>
{
    /**
     * Initializes a new instance of the {@link WeightedEntryListCodec} class.
     *
     * @param elementcodec The {@link Codec} that will be used to encode and decode elements from the list.
     * @throws ArgumentNullException {@code elementcodec} is {@code null}.
     */
    public WeightedEntryListCodec(Codec<T> elementcodec)
            throws ArgumentNullException
    {
        super(elementcodec);
    }

    @Override
    public <TI> DataResult<Pair<TL, TI>> decode(DynamicOps<TI> ops, TI input)
    {
        DataResult<Stream<TI>> d = ops.getStream(input);

        Optional<Stream<TI>> s;

        if ((s = d.result()).isPresent()) {
            LinkedList<T> list = new LinkedList<>();
            DataResult<Pair<T , TI>> dr; // We need the reference to this to get both the result and the error.
            Optional<Pair<T , TI>> result;
            Iterator<TI> itr = s.get().iterator();
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
                return DataResult.error(StringSupplier.FromFormatted("Exception of type %s occurred: %s", e.getClass().getName() , e.getMessage()));
            } finally {
                list.clear();
            }
        } else {
            return DataResult.error(new StringSupplier(d.error().get().message()));
        }
    }
}
