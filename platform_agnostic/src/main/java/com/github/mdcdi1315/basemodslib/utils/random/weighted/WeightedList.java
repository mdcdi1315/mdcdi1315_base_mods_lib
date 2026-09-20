package com.github.mdcdi1315.basemodslib.utils.random.weighted;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;

/**
 * Provides a wrapper that wraps a given {@link IList} object
 * and supports getting/setting the total weight value exposed
 * through the {@link IWeightedList} interface. <br />
 * The implementations of the class guarantee that the
 * total weight won't break if the underlying list does not
 * support all the interface methods.
 * @param <T> The type of the elements that the weighted list contains.
 */
public final class WeightedList<T extends IWeightedEntry>
    implements IList<T>, IWeightedList
{
    private int total_weight;
    private final IList<T> list;

    /**
     * Initializes a new instance of the {@link WeightedList} class.
     * @param list The {@link IList} object to wrap as a weighted list.
     * @throws ArgumentNullException {@code list} is {@code null}.
     */
    public WeightedList(IList<T> list)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(list, "list");
        this.list = list;
        total_weight = WeightedRandomUtils.GetTotalWeight(list);
    }

    /**
     * Wraps the given Java Collections Framework list as a weighted list.
     * @param list The list to convert as an instance of the {@link WeightedList} class.
     * @return The converted {@link WeightedList} that wraps the provided {@code list}.
     * @param <T> The type of the elements that the weighted list contains.
     * @throws ArgumentNullException {@code list} is {@code null}.
     */
    @NotNull
    public static <T extends IWeightedEntry> WeightedList<T> WrapJavaList(java.util.List<T> list)
        throws ArgumentNullException
    {
        return new WeightedList<>(CollectionManipulations.AsList(list));
    }

    @Override
    public void setItem(int index, T item)
    {
        T old = list.getItem(index);
        total_weight -= old.GetWeight();
        list.setItem(index, item);
        total_weight += item.GetWeight();
    }

    @Override
    public void Insert(int index, T item)
    {
        list.Insert(index, item);
        total_weight += item.GetWeight();
    }

    @Override
    public void RemoveAt(int index)
    {
        T old = list.getItem(index);
        list.RemoveAt(index);
        total_weight -= old.GetWeight();
    }

    @Override
    public void Add(T item)
    {
        list.Add(item);
        total_weight += item.GetWeight();
    }

    @Override
    public void Clear()
    {
        list.Clear();
        total_weight = 0;
    }

    @Override
    public boolean Remove(T item)
    {
        boolean value = list.Remove(item);
        if (value) { total_weight -= item.GetWeight(); }
        return value;
    }

    @Override
    public int getCount() { return list.getCount(); }

    @Pure
    @Override
    public int GetTotalWeight() { return total_weight; }

    @Override
    public int IndexOf(T item) { return list.IndexOf(item); }

    @Override
    public T getItem(int index) { return list.getItem(index); }

    @Override
    public boolean getIsReadOnly() { return list.getIsReadOnly(); }

    @Override
    public boolean Contains(T item) { return list.Contains(item); }

    @Override
    public IEnumerator<T> GetEnumerator() { return list.GetEnumerator(); }

    @Override
    public void CopyTo(T[] array, int arrayIndex) { list.CopyTo(array, arrayIndex); }
}
