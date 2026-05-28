package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.ISupportsCloning;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;

public final class WrappedIListFromJavaList<T>
    extends WrappedICollectionFromJavaCollection<T, java.util.List<T>>
    implements IList<T>, ITraversableCollection<T>, ISupportsCloning<T>
{
    public WrappedIListFromJavaList(@NotNull java.util.List<T> list) { super(list); }

    @Override
    public T getItem(int index)
    {
        try {
            return GetCollection().get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new ArgumentOutOfRangeException("index", "Index is out of range of valid bounds for the current list.");
        }
    }

    @Override
    public void setItem(int index, T item)
    {
        try {
            GetCollection().set(index, item);
        } catch (IndexOutOfBoundsException e) {
            throw new ArgumentOutOfRangeException("index", "Index is out of range of bounds of the current list.");
        } catch (UnsupportedOperationException e) {
            throw new NotSupportedException("The list is read-only and cannot be modified.");
        }
    }

    @Override
    public void Insert(int index, T item)
    {
        try {
            GetCollection().add(index, item);
        } catch (IndexOutOfBoundsException e) {
            throw new ArgumentOutOfRangeException("index", "Index is out of range of bounds of the current list.");
        } catch (UnsupportedOperationException e) {
            throw new NotSupportedException("The list is read-only and cannot be modified.");
        }
    }

    @Override
    public WrappedIListFromJavaList<T> Slice(int index, int count)
            throws ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else {
            var c = GetCollection();
            int total = index + count;
            if (total > c.size() || total < 0) {
                throw new ArgumentException("The specified combination of index and count parameters exceed the list's bounds.");
            } else {
                return new WrappedIListFromJavaList<>(c.subList(index, index + count));
            }
        }
    }

    @Override
    public WrappedIListFromJavaList<T> Clone()
    {
        var c = GetCollection();
        return new WrappedIListFromJavaList<>(c.subList(0, c.size() - 1));
    }

    @Override
    public int GetCount() { return getCount(); }

    @Override
    public void RemoveAt(int index) { GetCollection().remove(index); }

    @Override
    public int IndexOf(T item) { return GetCollection().indexOf(item); }

    @Override
    public T GetItem(int index) throws ArgumentOutOfRangeException { return getItem(index); }

    @Override
    public WrappedIListFromJavaList<T> Slice(int count) throws ArgumentException { return Slice(0, count); }
}
