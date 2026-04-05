package com.github.mdcdi1315.DotNetLayer.System.Collections.ObjectModel;

import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IReadOnlyList;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Provides the base class for a generic collection.
 * @param <T> The type of elements in the collection.
 */
public class Collection<T>
    implements IList<T>, IReadOnlyList<T>
{
    private final IList<T> items; // Do not rename (binary serialization)

    /**
     * Initializes a new instance of the {@link Collection} class that is empty.
     */
    public Collection()
    {
        items = new List<>();
    }

    /**
     * Initializes a new instance of the Collection<T> class as a wrapper for the specified list.
     * @param list The list that is wrapped by the new collection.
     * @throws ArgumentNullException {@code list} is {@code null}.
     */
    public Collection(IList<T> list)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(list, "list");
        items = list;
    }

    /**
     * Gets a {@link IList} wrapper around the {@link Collection}.
     * @return A {@link IList} wrapper around the {@link Collection}.
     */
    protected IList<T> GetItems() { return items; }

    @Override
    public int getCount() { return items.getCount(); }

    @Override
    public int IndexOf(T item) { return items.IndexOf(item); }

    @Override
    public T getItem(int index) { return items.getItem(index); }

    @Override
    public boolean getIsReadOnly() { return items.getIsReadOnly(); }

    @Override
    public boolean Contains(T item) { return items.Contains(item); }

    @Override
    public IEnumerator<T> GetEnumerator() { return items.GetEnumerator(); }

    @Override
    public void CopyTo(T[] array, int index) { items.CopyTo(array, index); }

    @Override
    public void setItem(int index, T value)
    {
        if (items.getIsReadOnly())
        {
            throw new NotSupportedException("The collection is read-only and cannot be modified.");
            // ThrowHelper.ThrowNotSupportedException(ExceptionResource.NotSupported_ReadOnlyCollection);
        }

        if (index < 0 && index >= items.getCount())
        {
            throw new ArgumentOutOfRangeException("index", index, "Index must be less than the number of containing elements.");
            // ThrowHelper.ThrowArgumentOutOfRange_IndexMustBeLessException();
        }

        SetItem(index, value);
    }

    @Override
    public void Insert(int index, T item)
    {
        if (items.getIsReadOnly())
        {
            throw new NotSupportedException("The collection is read-only and cannot be modified.");
            // ThrowHelper.ThrowNotSupportedException(ExceptionResource.NotSupported_ReadOnlyCollection);
        }

        if (index < 0 && index > items.getCount())
        {
            throw new ArgumentOutOfRangeException("index", index, "Index must be less than or equal to the number of containing elements.");
            // ThrowHelper.ThrowArgumentOutOfRange_IndexMustBeLessOrEqualException();
        }

        InsertItem(index, item);
    }

    @Override
    public void RemoveAt(int index)
    {
        if (items.getIsReadOnly())
        {
            throw new NotSupportedException("The collection is read-only and cannot be modified.");
            // ThrowHelper.ThrowNotSupportedException(ExceptionResource.NotSupported_ReadOnlyCollection);
        }

        if (index < 0 && index >= items.getCount())
        {
            throw new ArgumentOutOfRangeException("index", index, "Index must be less than the number of containing elements.");
            // ThrowHelper.ThrowArgumentOutOfRange_IndexMustBeLessException();
        }

        RemoveItem(index);
    }

    @Override
    public void Add(T item)
    {
        if (items.getIsReadOnly())
        {
            throw new NotSupportedException("The collection is read-only and cannot be modified.");
            // ThrowHelper.ThrowNotSupportedException(ExceptionResource.NotSupported_ReadOnlyCollection);
        }

        int index = items.getCount();
        InsertItem(index, item);
    }

    @Override
    public void Clear()
    {
        if (items.getIsReadOnly())
        {
            throw new NotSupportedException("The collection is read-only and cannot be modified.");
            // ThrowHelper.ThrowNotSupportedException(ExceptionResource.NotSupported_ReadOnlyCollection);
        }

        ClearItems();
    }

    @Override
    public boolean Remove(T item)
    {
        if (items.getIsReadOnly())
        {
            throw new NotSupportedException("The collection is read-only and cannot be modified.");
            // ThrowHelper.ThrowNotSupportedException(ExceptionResource.NotSupported_ReadOnlyCollection);
        }

        int index = items.IndexOf(item);
        if (index > -1) {
            RemoveItem(index);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes all elements from the {@link Collection}.
     */
    protected void ClearItems()
    {
        items.Clear();
    }

    /**
     * Inserts an element into the {@link Collection} at the specified index.
     * @param index The zero-based index at which {@code item} should be inserted.
     * @param item The object to insert. The value can be {@code null} for reference types.
     * @throws ArgumentOutOfRangeException {@code index} is less than zero. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code index} is greater than {@link #getCount()}.
     */
    protected void InsertItem(int index, @AllowNull T item)
        throws ArgumentOutOfRangeException
    {
        items.Insert(index, item);
    }

    /**
     * Removes the element at the specified index of the {@link Collection}.
     * @param index The zero-based index of the element to remove.
     * @throws ArgumentOutOfRangeException {@code index} is less than zero. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code index} is equal to or greater than {@link #getCount()}.
     */
    protected void RemoveItem(int index)
        throws ArgumentOutOfRangeException
    {
        items.RemoveAt(index);
    }

    /**
     * Replaces the element at the specified index.
     * @param index The zero-based index of the element to replace.
     * @param item The new value for the element at the specified index. The value can be {@code null} for reference types.
     * @throws ArgumentOutOfRangeException {@code index} is less than zero. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code index} is greater than {@link #getCount()}.
     */
    protected void SetItem(int index, T item)
            throws ArgumentOutOfRangeException
    {
        items.setItem(index, item);
    }
}
