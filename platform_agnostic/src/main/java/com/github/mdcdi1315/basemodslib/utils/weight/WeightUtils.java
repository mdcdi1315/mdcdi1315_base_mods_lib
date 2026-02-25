package com.github.mdcdi1315.basemodslib.utils.weight;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Optional;

/**
 * Shared utilities around {@link IWeightedEntry} and {@link Weight} instances.
 */
public final class WeightUtils
{
    private WeightUtils() {}

    /**
     * Gets the total weight from an iterable of items.
     * @param elements The items to iterate.
     * @return The total weight of the items contained in {@code elements}.
     * @param <T> The type of weighted entries to enumerate.
     * @throws ArgumentException Sum of weights must be <= 2147483647.
     */
    public static <T extends IWeightedEntry> int GetTotalWeight(Iterable<T> elements)
            throws ArgumentException
    {
        long i = 0L;

        for (T t : elements) { i += t.GetWeight().Value; }

        if (i > 2147483647L) {
            throw new ArgumentException("Sum of weights must be <= 2147483647");
        } else {
            return (int)i;
        }
    }


    public static <T extends IWeightedEntry> Optional<T> GetRandomItem(RandomSource random, List<T> elements, int totalWeight)
            throws ArgumentOutOfRangeException
    {
        if (totalWeight < 0) {
            throw new ArgumentOutOfRangeException("totalWeight", "Negative total weight in GetRandomItem");
        } else if (totalWeight == 0) {
            return Optional.empty();
        } else {
            return GetWeightedItem(elements, random.nextInt(totalWeight));
        }
    }

    public static <T extends IWeightedEntry> Optional<T> GetRandomItem(RandomSource random, List<T> elements) { return GetRandomItem(random , elements , GetTotalWeight(elements)); }

    public static <T extends IWeightedEntry> Optional<T> GetWeightedItem(List<T> elements, int index)
    {
        for (T t : elements) {
            index -= t.GetWeight().Value;
            if (index < 0) { return Optional.of(t); }
        }

        return Optional.empty();
    }

}