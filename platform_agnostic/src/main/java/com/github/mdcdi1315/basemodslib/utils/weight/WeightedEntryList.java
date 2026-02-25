package com.github.mdcdi1315.basemodslib.utils.weight;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.utils.EmptyIterable;

import net.minecraft.util.RandomSource;

import java.util.*;

/**
 * Provides a list that manages {@link IWeightedEntry} objects. <br />
 * Management is done very efficiently, as the total weight is computed at the same time elements are added or removed.
 * @param <T>
 */
public class WeightedEntryList<T extends IWeightedEntry>
    implements List<T>
{
    private int total_weight;
    private final List<T> list;

    /**
     * Initializes a new instance of the {@link WeightedEntryList} class, using a {@link LinkedList} to store and retrieve elements.
     */
    public WeightedEntryList()
    {
        list = new LinkedList<>();
        total_weight = 0;
    }

    protected WeightedEntryList(List<T> list)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.list = list, "list");
        total_weight = 0;
        for (T item : this.list) { total_weight += item.GetWeight().Value; }
    }

    @Override
    public int size() { return list.size(); }

    @Override
    public boolean isEmpty() { return list.isEmpty(); }

    @Override
    public boolean contains(Object o) { return list.contains(o); }

    @Override
    public @NotNull Iterator<T> iterator() { return list.iterator(); }

    @Override
    public @NotNull Object[] toArray() { return list.toArray(); }

    @Override
    public @NotNull <T1> T1[] toArray(@DisallowNull T1[] a) { return list.toArray(a); }

    @Override
    public boolean add(T t)
    {
        if (list.add(t)) {
            total_weight = t.GetWeight().Value;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void add(int index, T element) { list.add(index, element); total_weight += element.GetWeight().Value; }

    @Override
    public boolean remove(Object o)
    {
        int i = list.indexOf(o);
        if (i > -1) {
            list.remove(i);
            total_weight -= ((IWeightedEntry)o).GetWeight().Value;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public T remove(int index)
    {
        T old = list.remove(index);
        total_weight -= old.GetWeight().Value;
        return old;
    }

    @Override
    public boolean containsAll(@DisallowNull Collection<?> c) { return list.containsAll(c); }

    @Override
    public boolean addAll(@DisallowNull Collection<? extends T> c)
    {
        if (list.addAll(c)) {
            for (T i : c) { total_weight += i.GetWeight().Value; }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addAll(int index, @DisallowNull Collection<? extends T> c)
    {
        if (list.addAll(index, c)) {
            for (T i : c) { total_weight += i.GetWeight().Value; }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeAll(@DisallowNull Collection<?> c) { return false; }

    @Override
    public boolean retainAll(@DisallowNull Collection<?> c) { return false; }

    @Override
    public void clear() { list.clear(); }

    @Override
    public T get(int index) { return list.get(index); }

    @Override
    public T set(int index, T element)
    {
        T old = list.set(index, element);
        if (old != null) { total_weight -= old.GetWeight().Value; }
        total_weight += element.GetWeight().Value;
        return old;
    }

    @Override
    public int indexOf(Object o) { return list.indexOf(o); }

    @Override
    public int lastIndexOf(Object o) { return list.lastIndexOf(o); }

    @Override
    public @NotNull ListIterator<T> listIterator() { return list.listIterator(); }

    @Override
    public @NotNull ListIterator<T> listIterator(int index) { return list.listIterator(index); }

    @Override
    public @NotNull List<T> subList(int fromIndex, int toIndex) { return list.subList(fromIndex, toIndex); }

    // WeightedEntryList specific extensions

    /**
     * Gets the total weight of all the items that are contained in the list.
     * @return The total weight of all the enregistered items.
     */
    public final int GetTotalWeight() { return total_weight; }

    /**
     * Gets an iterable that provides the following abilities: <br />
     * -&gt; Provides random entries. Randomization is done thanks to the passed in {@link RandomSource} instance. <br />
     * -&gt; If {@link #isEmpty()} returns {@code true}, then an empty iterable is returned. <br />
     * -&gt; Maximum number of entries that can be returned from the iterable is {@code rolls} (This is valid every time that {@link Iterable#iterator()} is called on it).
     * @param source The random source to use.
     * @param rolls The number of rolls to perform.
     * @return An iterable that returns random elements up to {@code rolls}.
     * @throws ArgumentNullException {@code source} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code rolls} is a negative value.
     */
    public Iterable<T> GetRandomWeightedEntries(RandomSource source, int rolls)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        try {
            return new RandomWeightedEntriesIterable<>(list, source, total_weight, rolls);
        } catch (ArgumentException e) {
            if (e.getClass() == ArgumentException.class) { return new EmptyIterable<>(); } else { throw e; }
        }
    }
}
