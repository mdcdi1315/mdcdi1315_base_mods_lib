package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;

public class WrappedICollectionFromJavaCollection<T, TC extends java.util.Collection<T>>
    extends BaseEnumerable<T>
    implements ICollection<T>
{
    private final TC collection;

    public WrappedICollectionFromJavaCollection(@NotNull TC collection) { this.collection = collection; }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    public int getCount() { return collection.size(); }

    @NotNull
    protected final TC GetCollection() { return collection; }

    @Override
    public void Add(T item)
    {
        try {
            collection.add(item);
        } catch (UnsupportedOperationException e) {
            throw new NotSupportedException("Read-only collection");
        }
    }

    @Override
    public void Clear()
    {
        try {
            collection.clear();
        } catch (UnsupportedOperationException e) {
            throw new NotSupportedException("Read-only collection");
        }
    }

    @Override
    public boolean Contains(T item) { return collection.contains(item); }

    @Override
    public void CopyTo(T[] array, int arrayIndex)
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (arrayIndex < 0) {
            throw new ArgumentOutOfRangeException("arrayIndex", "Array index cannot be a negative value.");
        } else if (arrayIndex + collection.size() > array.length) {
            throw new ArgumentException("The array does not have enough space to place all the elements of the current SingleLinkedList object.", "array");
        } else {
            for (T item : collection) { array[arrayIndex++] = item; }
        }
    }

    @Override
    public boolean Remove(T item)
    {
        try {
            return collection.remove(item);
        } catch (UnsupportedOperationException e) {
            throw new NotSupportedException("Read-only collection");
        }
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return new FromIteratorEnumerator<>(collection.iterator()); }
}
