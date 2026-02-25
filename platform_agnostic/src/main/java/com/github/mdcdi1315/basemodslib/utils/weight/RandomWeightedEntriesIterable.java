package com.github.mdcdi1315.basemodslib.utils.weight;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;

import net.minecraft.util.RandomSource;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Iterator;
import java.util.Optional;

/**
 * Provides an {@link Iterable} implementation providing random entries appropriately weighted.
 * @param <T> The type of elements to be returned by the iterators.
 */
public final class RandomWeightedEntriesIterable<T extends IWeightedEntry>
    implements Iterable<T>, ISynchronized
{
    private static final class RandomWeightedEntriesIterator<T extends IWeightedEntry>
            implements Iterator<T>, ISynchronized
    {
        private T computed;
        private int rollsdone;
        private final RandomSource rs;
        private final ImmutableList<T> list;
        private final int totalweight , rolls;

        RandomWeightedEntriesIterator(ImmutableList<T> elements , RandomSource source , int totalweight , int nrolls) { list = elements; this.totalweight = totalweight; this.rolls = nrolls; rs = source; rollsdone = -1; }

        @Override
        public boolean hasNext()
        {
            Optional<T> ret = Optional.empty();
            while (ret.isEmpty() && ++rollsdone < rolls) { ret = WeightUtils.GetWeightedItem(list , rs.nextInt(totalweight)); }
            if (ret.isPresent()) {
                computed = ret.get();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public T next() { return computed; }
    }

    private final RandomSource rs;
    private final ImmutableList<T> list;
    private final int totalweight , rolls;

    /**
     * Creates a new instance of the {@link RandomWeightedEntriesIterable} class from the specified list of elements,
     * the {@link RandomSource} to compute the random values from, the total weight of {@code elements} and the number of rolls to perform.
     * @param elements The number of elements to extract random entries from.
     * @param source The random number generator to manufacture a random returned entry from.
     * @param totalweight The total weight of {@code elements}.
     * @param nrolls The number of rolls to perform.
     * @throws ArgumentNullException {@code elements} and/or {@code source} are {@code null}.
     * @throws ArgumentException {@code elements} list cannot be empty.
     * @throws ArgumentOutOfRangeException {@code totalweight} and/or {@code nrolls} are negative values.
     */
    public RandomWeightedEntriesIterable(List<T> elements, RandomSource source, int totalweight, int nrolls)
            throws ArgumentNullException, ArgumentException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(source, "source");
        ArgumentNullException.ThrowIfNull(elements, "elements");
        if (elements.isEmpty()) {
            throw new ArgumentException("List cannot be empty." , "elements");
        } else if (totalweight < 0) {
            throw new ArgumentOutOfRangeException("totalweight" , "Total weight cannot be less than zero.");
        } else if (nrolls < 0) {
            throw new ArgumentOutOfRangeException("nrolls" , "The number of rolls to perform cannot be less than zero.");
        } else {
            list = ImmutableList.copyOf(elements);
            this.totalweight = totalweight;
            this.rolls = nrolls;
            rs = source;
        }
    }

    @Override
    public Iterator<T> iterator() { return new RandomWeightedEntriesIterator<>(list, rs, totalweight, rolls); }

    /**
     * Creates an {@link Iterator} with a new number of rolls to instead perform.
     * @param newrolls The number of new rolls to perform.
     * @return An {@link Iterator} instance providing an iterator that will perform {@code newrolls} rolls.
     * @throws ArgumentOutOfRangeException {@code newrolls} is a negative value.
     */
    public Iterator<T> IteratorWithRolls(int newrolls)
            throws ArgumentOutOfRangeException
    {
        if (newrolls < 0) {
            throw new ArgumentOutOfRangeException("newrolls" , "The number of rolls to perform cannot be less than zero.");
        } else {
            return new RandomWeightedEntriesIterator<>(list, rs, totalweight, newrolls);
        }
    }

    /**
     * Gets the number of candidates currently known by this iterable instance.
     * @return The number of candidate entries that can be returned by iterators.
     */
    public int GetCount() { return list.size(); }

    /**
     * Gets the number of rolls to perform before the iterator stops iteration.
     * @return The number of rolls to be performed by iterators returned from the {@link #iterator()} method.
     */
    public int GetRolls() { return rolls; }
}
