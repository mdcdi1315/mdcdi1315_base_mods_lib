package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Provides the {@code abstract} base class for a strongly typed collection of key/value pairs.
 */
public abstract class DictionaryBase
    implements IDictionary
{
    @AllowNull
    private Hashtable _hashtable;

    /**
     * Gets the list of elements contained in the {@link DictionaryBase} instance.
     * @return A {@link Hashtable} representing the {@link DictionaryBase} instance itself.
     */
    @NotNull
    protected Hashtable GetInnerHashtable()
    {
        if (_hashtable == null)
        {
            _hashtable = new Hashtable();
        }
        return _hashtable;
    }

    /**
     * Gets the list of elements contained in the {@link DictionaryBase} instance.
     * @return An {@link IDictionary} representing the {@link DictionaryBase} instance itself.
     */
    @NotNull
    protected IDictionary GetDictionary() { return this; }

    @Override
    public ICollection GetKeys() { return GetInnerHashtable().GetKeys(); }

    @Override
    public Object getSyncRoot() { return GetInnerHashtable().getSyncRoot(); }

    @Override
    public ICollection GetValues() { return GetInnerHashtable().GetValues(); }

    @Override
    public boolean GetIsReadOnly() { return GetInnerHashtable().GetIsReadOnly(); }

    @Override
    public boolean GetIsFixedSize() { return GetInnerHashtable().GetIsFixedSize(); }

    @Override
    public boolean getIsSynchronized() { return GetInnerHashtable().getIsSynchronized(); }

    @Override
    public IDictionaryEnumerator GetEnumerator() { return GetInnerHashtable().GetEnumerator(); }

    @Override
    public boolean Contains(Object key) throws ArgumentNullException { return GetInnerHashtable().Contains(key); }

    @Override
    public void CopyTo(Object array, int index) throws ArgumentException { GetInnerHashtable().CopyTo(array, index); }

    @Override
    public int getCount()
    {
        // to avoid newing inner list if no items are ever added
        return _hashtable == null ? 0 : _hashtable.getCount();
    }

    @Override
    public Object GetItem(Object key)
    {
        var currentValue = GetInnerHashtable().GetItem(key);
        OnGet(key, currentValue);
        return currentValue;
    }

    @Override
    public void SetItem(Object key, Object value)
    {
        OnValidate(key, value);
        boolean keyExists = true;
        var temp = GetInnerHashtable().GetItem(key);
        if (temp == null)
        {
            keyExists = GetInnerHashtable().Contains(key);
        }

        OnSet(key, temp, value);
        GetInnerHashtable().SetItem(key, value);
        try
        {
            OnSetComplete(key, temp, value);
        }
        catch (Exception e)
        {
            if (keyExists)
            {
                GetInnerHashtable().SetItem(key, temp);
            }
            else
            {
                GetInnerHashtable().Remove(key);
            }
            throw e;
        }
    }

    @Override
    public void Add(Object key, Object value)
            throws ArgumentException, NotSupportedException
    {
        OnValidate(key, value);
        OnInsert(key, value);
        GetInnerHashtable().Add(key, value);
        try
        {
            OnInsertComplete(key, value);
        }
        catch (Exception e)
        {
            GetInnerHashtable().Remove(key);
            throw e;
        }
    }

    @Override
    public void Clear()
    {
        OnClear();
        GetInnerHashtable().Clear();
        OnClearComplete();
    }

    @Override
    public void Remove(Object key)
            throws ArgumentNullException, NotSupportedException
    {
        if (GetInnerHashtable().Contains(key))
        {
            Object temp = GetInnerHashtable().GetItem(key);
            OnValidate(key, temp);
            OnRemove(key, temp);

            GetInnerHashtable().Remove(key);
            try
            {
                OnRemoveComplete(key, temp);
            }
            catch (Exception e)
            {
                GetInnerHashtable().Add(key, temp);
                throw e;
            }
        }
    }

    /**
     * Gets the element with the specified key and value in the {@link DictionaryBase} instance.
     * @param key The key of the element to get.
     * @param currentValue The current value of the element associated with key.
     * @return An {@link Object} containing the element with the specified key and value.
     */
    @MaybeNull
    protected Object OnGet(Object key, @AllowNull Object currentValue) { return currentValue; }

    /**
     * Performs additional custom processes before setting a value in the {@link DictionaryBase} instance.
     * @param key The key of the element to locate.
     * @param oldValue The old value of the element associated with {@code key}.
     * @param newValue The new value of the element associated with {@code key}.
     */
    protected void OnSet(Object key, @AllowNull Object oldValue, @AllowNull Object newValue) { }

    /**
     * Performs additional custom processes before inserting a new element into the {@link DictionaryBase} instance.
     * @param key The key of the element to insert.
     * @param value The value of the element to insert.
     */
    protected void OnInsert(Object key, @AllowNull Object value) { }

    /**
     * Performs additional custom processes before clearing the contents of the {@link DictionaryBase} instance.
     */
    protected void OnClear() { }

    /**
     * Performs additional custom processes before removing an element from the {@link DictionaryBase} instance.
     * @param key The key of the element to remove.
     * @param value The value of the element to remove.
     */
    protected void OnRemove(Object key, @AllowNull Object value) { }

    /**
     * Performs additional custom processes when validating the element with the specified key and value.
     * @param key The key of the element to validate.
     * @param value The value of the element to validate.
     */
    protected void OnValidate(Object key, @AllowNull Object value)  { }

    /**
     * Performs additional custom processes after setting a value in the {@link DictionaryBase} instance.
     * @param key The key of the element to locate.
     * @param oldValue The old value of the element associated with key.
     * @param newValue The new value of the element associated with key.
     */
    protected void OnSetComplete(Object key, @AllowNull Object oldValue, @AllowNull Object newValue) { }

    /**
     * Performs additional custom processes after inserting a new element into the {@link DictionaryBase} instance.
     * @param key The key of the element to insert.
     * @param value The value of the element to insert.
     */
    protected void OnInsertComplete(Object key, @AllowNull Object value) { }

    /**
     * Performs additional custom processes after clearing the contents of the {@link DictionaryBase} instance.
     */
    protected void OnClearComplete() { }

    /**
     * Performs additional custom processes after removing an element from the {@link DictionaryBase} instance.
     * @param key The key of the element to remove.
     * @param value The value of the element to remove.
     */
    protected void OnRemoveComplete(Object key, @AllowNull Object value) { }
}
