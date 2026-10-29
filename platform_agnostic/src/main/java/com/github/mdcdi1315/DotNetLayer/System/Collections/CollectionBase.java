package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import java.lang.Exception;

/**
 * Provides the {@code abstract} base class for a strongly typed collection.
 */
// Useful base class for typed read/write collections where items derive from Object
public abstract class CollectionBase
        implements IList
{
    private final ArrayList _list;

    /**
     * Initializes a new instance of the {@link CollectionBase} class with the default initial capacity.
     */
    protected CollectionBase()
    {
        _list = new ArrayList();
    }

    /**
     * Initializes a new instance of the {@link CollectionBase} class with the specified capacity.
     * @param capacity The number of elements that the new list can initially store.
     */
    protected CollectionBase(int capacity)
    {
        _list = new ArrayList(capacity);
    }

    /**
     * Gets an {@link IList} containing the list of elements in the {@link CollectionBase} instance.
     * @return An {@link IList} representing the {@link CollectionBase} instance itself.
     */
    @NotNull
    protected IList GetList() { return this; }

    /**
     * Gets an {@link ArrayList} containing the list of elements in the {@link CollectionBase} instance.
     * @return An {@link ArrayList} representing the {@link CollectionBase} instance itself.
     *         Retrieving the value of this property is an O(1) operation.
     */
    @NotNull
    protected ArrayList GetInnerList() { return _list; }

    @Override
    public int getCount() { return _list.getCount(); }

    /**
     * Gets the number of elements that the {@link CollectionBase} can contain.
     * @return The number of elements that the {@link CollectionBase} can contain.
     */
    public int GetCapacity() { return GetInnerList().GetCapacity(); }

    @Override
    public Object getSyncRoot() { return GetInnerList().getSyncRoot(); }

    @Override
    public boolean GetIsReadOnly() { return GetInnerList().GetIsReadOnly(); }

    @Override
    public int IndexOf(Object value) { return GetInnerList().IndexOf(value); }

    @Override
    public boolean GetIsFixedSize() { return GetInnerList().GetIsFixedSize(); }

    @Override
    public IEnumerator GetEnumerator() { return GetInnerList().GetEnumerator(); }

    @Override
    public boolean Contains(Object value) { return GetInnerList().Contains(value); }

    @Override
    public boolean getIsSynchronized() { return GetInnerList().getIsSynchronized(); }

    @Override
    public void CopyTo(Object array, int index) throws ArgumentException { GetInnerList().CopyTo(array, index); }

    /**
     * Sets the number of elements that the {@link CollectionBase} can contain.
     * @param value The desired number of elements.
     * @throws ArgumentOutOfRangeException {@code value} is set to a value that is less than {@link #getCount()}.
     * @throws OutOfMemoryException There is not enough memory available on the system.
     */
    public void SetCapacity(int value) throws ArgumentOutOfRangeException, OutOfMemoryException { GetInnerList().SetCapacity(value); }

    @Override
    public void Clear()
    {
        OnClear();
        GetInnerList().Clear();
        OnClearComplete();
    }

    @Override
    public void RemoveAt(int index)
    {
        if (index < 0 || index >= getCount())
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value and must be less than the collection's size.");
        Object temp = GetInnerList().GetItem(index);
        OnValidate(temp);
        OnRemove(index, temp);
        GetInnerList().RemoveAt(index);
        try
        {
            OnRemoveComplete(index, temp);
        }
        catch (Exception e)
        {
            GetInnerList().Insert(index, temp);
            throw e;
        }
    }

    @Override
    public Object GetItem(int index)
            throws ArgumentOutOfRangeException 
    {
        if (index < 0 || index >= getCount())
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value and must be less than the collection's size.");
        return GetInnerList().GetItem(index);
    }

    @Override
    public void SetItem(int index, Object value) 
            throws ArgumentOutOfRangeException, NotSupportedException
    {
        if (index < 0 || index >= getCount())
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value and must be less than the collection's size.");
        OnValidate(value);
        Object temp = GetInnerList().GetItem(index);
        OnSet(index, temp, value);
        GetInnerList().SetItem(index, value);
        try
        {
            OnSetComplete(index, temp, value);
        }
        catch (Exception e)
        {
            GetInnerList().SetItem(index, temp);
            throw e;
        }
    }

    @Override
    public int Add(@AllowNull Object value)
    {
        OnValidate(value);
        OnInsert(GetInnerList().getCount(), value);
        int index = GetInnerList().Add(value);
        try
        {
            OnInsertComplete(index, value);
        }
        catch (Exception e)
        {
            GetInnerList().RemoveAt(index);
            throw e;
        }
        return index;
    }

    @Override
    public void Remove(@AllowNull Object value)
    {
        OnValidate(value);
        int index = GetInnerList().IndexOf(value);
        if (index < 0) throw new ArgumentException("Specified value was not found.");
        OnRemove(index, value);
        GetInnerList().RemoveAt(index);
        try
        {
            OnRemoveComplete(index, value);
        }
        catch (Exception e)
        {
            GetInnerList().Insert(index, value);
            throw e;
        }
    }

    @Override
    public void Insert(int index, @AllowNull Object value)
    {
        if (index < 0 || index > getCount())
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value and must be less than the collection's size.");
        OnValidate(value);
        OnInsert(index, value);
        GetInnerList().Insert(index, value);
        try
        {
            OnInsertComplete(index, value);
        }
        catch (Exception e)
        {
            GetInnerList().RemoveAt(index);
            throw e;
        }
    }

    /**
     * Performs additional custom processes before setting a value in the {@link CollectionBase} instance.
     * @param index The zero-based index at which {@code oldValue} can be found.
     * @param oldValue The value to replace with {@code newValue}.
     * @param newValue The new value of the element at {@code index}.
     */
    protected void OnSet(int index, @AllowNull Object oldValue, @AllowNull Object newValue) { }

    /**
     * Performs additional custom processes before inserting a new element into the {@link CollectionBase} instance.
     * @param index The zero-based index at which to insert {@code value}.
     * @param value The new value of the element at {@code index}.
     */
    protected void OnInsert(int index, @AllowNull Object value) { }

    /**
     * Performs additional custom processes when clearing the contents of the {@link CollectionBase} instance.
     */
    protected void OnClear() { }

    /**
     * Performs additional custom processes when removing an element from the {@link CollectionBase} instance.
     * @param index The zero-based index at which {@code value} can be found.
     * @param value The value of the element to remove from {@code index}.
     */
    protected void OnRemove(int index, @AllowNull Object value) { }

    /**
     * Performs additional custom processes when validating a value.
     * @param value The object to validate.
     * @throws ArgumentNullException {@code value} is {@code null}.
     */
    protected void OnValidate(Object value)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(value, "value");
    }

    /**
     * Performs additional custom processes after setting a value in the {@link CollectionBase} instance.
     * @param index The zero-based index at which {@code oldValue} can be found.
     * @param oldValue The value to replace with {@code newValue}.
     * @param newValue The new value of the element at {@code index}.
     */
    protected void OnSetComplete(int index, @AllowNull Object oldValue, @AllowNull Object newValue) { }

    /**
     * Performs additional custom processes after inserting a new element into the {@link CollectionBase} instance.
     * @param index The zero-based index at which to insert {@code value}.
     * @param value The new value of the element at {@code index}.
     */
    protected void OnInsertComplete(int index, @AllowNull Object value) { }

    /**
     * Performs additional custom processes after clearing the contents of the {@link CollectionBase} instance.
     */
    protected void OnClearComplete() { }

    /**
     * Performs additional custom processes after removing an element from the {@link CollectionBase} instance.
     * @param index The zero-based index at which to insert {@code value}.
     * @param value The new value of the element at {@code index}.
     */
    protected void OnRemoveComplete(int index, @AllowNull Object value) { }
}