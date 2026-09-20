package com.github.mdcdi1315.basemodslib.utils.random.weighted;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.random.RandomUtils;
import com.github.mdcdi1315.basemodslib.utils.random.IRandomSource;
import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;

import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;
import java.util.Collections;

/**
 * Provides utility methods around weighted random lists.
 */
public final class WeightedRandomUtils
{
    private WeightedRandomUtils() {}

    private static final RecordCodecBuilder<IWeightedEntry, Integer> RECOMMENDED_RECORD_FIELD_CONFIG = RecordCodecBuilder.of(IWeightedEntry::GetWeight, "weight", CodecUtils.ZERO_OR_POSITIVE_INTEGER);

    /**
     * Gets a record builder instance that provides a recommended way for declaring the weight field in codec implementations. <br />
     * The field will be named as {@code weight}.
     * @return The record builder instance.
     * @param <T> The type of the weighted entry to be returned.
     */
    @Pure
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends IWeightedEntry> RecordCodecBuilder<T, Integer> GetRecommendedRecordFieldConfig() { return (RecordCodecBuilder<T, Integer>) RECOMMENDED_RECORD_FIELD_CONFIG; }

    /**
     * Gets the sum of all the weights of all the declared elements in the enumerable.
     * @param enumerable The collection that provides the elements to collect their weight values.
     * @return The sum of all the weight values of each element.
     * @param <T> The exact type of the elements contained in {@code enumerable}.
     * @throws InvalidOperationException Total weight is greater than {@link Integer#MAX_VALUE}.
     */
    public static <T extends IWeightedEntry> int GetTotalWeight(@AllowNull IEnumerable<T> enumerable)
    {
        long weight = 0L;

        try (var enumerator = CollectionManipulations.GetEnumeratorSafe(enumerable))
        {
            while (enumerator.MoveNext())
            {
                weight += enumerator.getCurrent().GetWeight();
            }
        }

        if (weight > Integer.MAX_VALUE) {
            throw new InvalidOperationException("Too many number of entries in the specified collection! Their weights made the total weight to overflow!");
        } else {
            return (int)weight;
        }
    }

    /**
     * Gets the sum of all the weights of all the declared elements in the iterable.
     * @param iterable The collection that provides the elements to collect their weight values.
     * @return The sum of all the weight values of each element.
     * @param <T> The exact type of the elements contained in {@code iterable}.
     * @throws InvalidOperationException Total weight is greater than {@link Integer#MAX_VALUE}.
     */
    public static <T extends IWeightedEntry> int GetTotalWeight(@AllowNull Iterable<T> iterable)
        throws InvalidOperationException
    {
        if (iterable == null) { iterable = Collections.emptyList(); }

        long weight = 0L;

        for (T entry : iterable)
        {
            weight += entry.GetWeight();
        }

        if (weight > Integer.MAX_VALUE) {
            throw new InvalidOperationException("Too many number of entries in the specified collection! Their weights made the total weight to overflow!");
        } else {
            return (int)weight;
        }
    }

    /**
     * Picks a random element from the given {@link IEnumerable} collection.
     * @param random The {@link IRandomSource}
     * @param enumerable The {@link IEnumerable} collection that contains the weighted entries.
     * @param total_weight The total weight of the entries contained in {@code enumerable}.
     * @return A random, weighted element.
     * @param <T> The exact type of the elements contained in {@code enumerable}.
     * @throws ArgumentNullException {@code random} and/or {@code enumerable} are {@code null}.
     * @throws ArgumentOutOfRangeException {@code total_weight} is less than zero.
     */
    @NotNull
    public static <T extends IWeightedEntry> Optional<T> PickRandomElement(IRandomSource random, IEnumerable<T> enumerable, int total_weight)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(random, "random");
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");

        if (total_weight < 0) {
            throw new ArgumentOutOfRangeException("total_weight", "Total weight cannot be a negative value!");
        } else {
            total_weight = RandomUtils.NextIntInRange(random, 0, total_weight);

            try (var enumerator = enumerable.GetEnumerator())
            {
                T current;
                while (enumerator.MoveNext())
                {
                    current = enumerator.getCurrent();
                    total_weight -= current.GetWeight();
                    if (total_weight < 1) { return Optional.of(current); }
                }
            }

            return Optional.empty();
        }
    }
}
