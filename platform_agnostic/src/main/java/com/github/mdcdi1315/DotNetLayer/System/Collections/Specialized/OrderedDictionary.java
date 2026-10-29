package com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized;

import com.github.mdcdi1315.DotNetLayer.System.Collections.*;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

@TypeForwardedFrom(AssemblyFullName = "System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class OrderedDictionary
        implements IOrderedDictionary
{
    private ArrayList _objectsArray;
    private Hashtable _objectsTable;
    private int _initialCapacity;
    private IEqualityComparer _comparer;
    private boolean _readOnly;

    public OrderedDictionary() { this(0); }

    public OrderedDictionary(int capacity) { this(capacity, null); }

    public OrderedDictionary(@AllowNull IEqualityComparer comparer) { this(0, comparer); }

    public OrderedDictionary(int capacity, @AllowNull IEqualityComparer comparer)
    {
        _initialCapacity = capacity;
        _comparer = comparer;
    }

    private OrderedDictionary(OrderedDictionary dictionary)
    {
        // Debug.Assert(dictionary != null);

        _readOnly = true;
        _objectsArray = dictionary._objectsArray;
        _objectsTable = dictionary._objectsTable;
        _comparer = dictionary._comparer;
        _initialCapacity = dictionary._initialCapacity;
    }

    /// <devdoc>
    /// Gets the size of the table.
    /// </devdoc>
    public int getCount()
    {
        if (_objectsArray == null)
        {
            return 0;
        }
        return _objectsArray.getCount();
    }

    @Override
    public Object getSyncRoot() { return this; }

    @Override
    public boolean getIsSynchronized() { return false; }

    @Override
    public boolean GetIsReadOnly() { return _readOnly; }

    @Override
    public boolean GetIsFixedSize() { return _readOnly; }

    /// <devdoc>
    /// Gets the collection of keys in the table in order.
    /// </devdoc>
    @NotNull
    public ICollection GetKeys()
    {
        ArrayList objectsArray = EnsureObjectsArray();
        Hashtable objectsTable = EnsureObjectsTable();
        return new OrderedDictionaryKeyValueCollection(objectsArray, objectsTable, _comparer);
    }

    @NotNull
    private ArrayList EnsureObjectsArray()
    {
        // _objectsArray ??= new ArrayList(_initialCapacity);
        if (_objectsArray == null)
        {
            _objectsArray = new ArrayList(_initialCapacity);
        }
        return _objectsArray;
    }

    @NotNull
    private Hashtable EnsureObjectsTable()
    {
        // _objectsTable ??= new Hashtable(_initialCapacity, _comparer);
        if (_objectsTable == null)
        {
            _objectsTable = new Hashtable(_initialCapacity, _comparer);
        }
        return _objectsTable;
    }

    @Override
    @MaybeNull
    public Object GetItem(int index) throws ArgumentOutOfRangeException {
        return ((DictionaryEntry)EnsureObjectsArray().GetItem(index)).GetValue();
    }

    @Override
    public void SetItem(int index, @AllowNull Object value)
            throws ArgumentOutOfRangeException
    {
        if (_readOnly)
        {
            throw new NotSupportedException("The collection is read-only.");
        }
        if (_objectsArray == null || index < 0 || index >= _objectsArray.getCount())
        {
            throw new ArgumentOutOfRangeException("index");
        }
        ArrayList objectsArray = EnsureObjectsArray();
        Hashtable objectsTable = EnsureObjectsTable();
        Object key = ((DictionaryEntry)objectsArray.GetItem(index)).GetKey();
        objectsArray.SetItem(index, new DictionaryEntry(key, value));
        objectsTable.SetItem(key, value);
    }

    @Override
    @MaybeNull
    public Object GetItem(Object key)
            throws ArgumentNullException
    {
        if (_objectsTable == null) {
            return null;
        } else {
            return _objectsTable.GetItem(key);
        }
    }

    @Override
    public void SetItem(Object key, Object value)
            throws ArgumentNullException, NotSupportedException
    {
        if (_readOnly)
        {
            throw new NotSupportedException("The collection is read-only.");
        }
        Hashtable objectsTable = EnsureObjectsTable();
        if (objectsTable.Contains(key))
        {
            objectsTable.SetItem(key, value);
            EnsureObjectsArray().SetItem(IndexOfKey(key), new DictionaryEntry(key, value));
        }
        else
        {
            Add(key, value);
        }
    }

    /// <devdoc>
    /// Returns an arrayList of the values in the table
    /// </devdoc>
    @NotNull
    public ICollection GetValues()
    {
        ArrayList objectsArray = EnsureObjectsArray();
        return new OrderedDictionaryKeyValueCollection(objectsArray);
    }

    /// <devdoc>
    /// Adds a new entry to the table with the lowest-available index.
    /// </devdoc>
    public void Add(Object key, @AllowNull Object value)
    {
        if (_readOnly)
        {
            throw new NotSupportedException("The collection is read-only.");
        }
        Hashtable objectsTable = EnsureObjectsTable();
        ArrayList objectsArray = EnsureObjectsArray();
        objectsTable.Add(key, value);
        objectsArray.Add(new DictionaryEntry(key, value));
    }

    /// <devdoc>
    /// Clears all elements in the table.
    /// </devdoc>
    @Override
    public void Clear()
    {
        if (_readOnly)
        {
            throw new NotSupportedException("The collection is read-only.");
        }
        if (_objectsTable != null) { _objectsTable.Clear(); }
        if (_objectsArray != null) { _objectsArray.Clear(); }
    }

    /// <devdoc>
    /// Returns a readonly OrderedDictionary for the given OrderedDictionary.
    /// </devdoc>
    @NotNull
    public OrderedDictionary AsReadOnly() { return new OrderedDictionary(this); }

    /// <devdoc>
    /// Returns true if the key exists in the table, false otherwise.
    /// </devdoc>
    @Override
    public boolean Contains(Object key)
    {
        ArgumentNullException.ThrowIfNull(key);

        if (_objectsTable == null)
        {
            return false;
        }
        return _objectsTable.Contains(key);
    }

    /// <devdoc>
    /// Copies the table to an array.  This will not preserve order.
    /// </devdoc>
    public void CopyTo(Object array, int index)
    {
        Hashtable objectsTable = EnsureObjectsTable();
        objectsTable.CopyTo(array, index);
    }

    private int IndexOfKey(Object key)
    {
        if (_objectsArray == null)
        {
            return -1;
        }
        for (int i = 0; i < _objectsArray.getCount(); i++)
        {
            Object o = ((DictionaryEntry)_objectsArray.GetItem(i)).GetKey();
            if (_comparer != null)
            {
                if (_comparer.Equals(o, key))
                {
                    return i;
                }
            }
            else
            {
                if (o.equals(key))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    /// <devdoc>
    /// Inserts a new object at the given index with the given key.
    /// </devdoc>
    @Override
    public void Insert(int index, Object key, @AllowNull Object value)
    {
        if (_readOnly) {
            throw new NotSupportedException("The collection is read-only.");
        } else if (index > getCount()) {
            throw new ArgumentOutOfRangeException("index", "Index is greater than the value of GetCount().");
        } else if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else {
            Hashtable objectsTable = EnsureObjectsTable();
            ArrayList objectsArray = EnsureObjectsArray();
            objectsTable.Add(key, value);
            objectsArray.Insert(index, new DictionaryEntry(key, value));
        }
    }

    /// <devdoc>
    /// Removes the entry at the given index.
    /// </devdoc>
    @Override
    public void RemoveAt(int index)
    {
        if (_readOnly) {
            throw new NotSupportedException("The collection is read-only.");
        } else if (index >= getCount()) {
            throw new ArgumentOutOfRangeException("index", "Index is greater than or equal to the value of GetCount().");
        } else if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        }
        Hashtable objectsTable = EnsureObjectsTable();
        ArrayList objectsArray = EnsureObjectsArray();
        Object key = ((DictionaryEntry)objectsArray.GetItem(index)).GetKey();
        objectsArray.RemoveAt(index);
        objectsTable.Remove(key);
    }

    /// <devdoc>
    /// Removes the entry with the given key.
    /// </devdoc>
    @Override
    public void Remove(Object key)
    {
        if (_readOnly)
        {
            throw new NotSupportedException("The collection is read-only.");
        }
        ArgumentNullException.ThrowIfNull(key);

        int index = IndexOfKey(key);
        if (index < 0)
        {
            return;
        }

        Hashtable objectsTable = EnsureObjectsTable();
        ArrayList objectsArray = EnsureObjectsArray();
        objectsTable.Remove(key);
        objectsArray.RemoveAt(index);
    }
    
    @Override
    public IDictionaryEnumerator GetEnumerator() { return new OrderedDictionaryEnumerator(EnsureObjectsArray(), OrderedDictionaryEnumerator.DictionaryEntry); }

    /// <devdoc>
    /// OrderedDictionaryEnumerator works just like any other IDictionaryEnumerator, but it retrieves DictionaryEntries
    /// in the order by index.
    /// </devdoc>
    private static final class OrderedDictionaryEnumerator 
            implements IDictionaryEnumerator
    {
        private final int _objectReturnType;
        public static final int Keys = 1;
        public static final int Values = 2;
        public static final int DictionaryEntry = 3;
        private final IEnumerator _arrayEnumerator;

        private OrderedDictionaryEnumerator(ArrayList array, int objectReturnType)
        {
            _arrayEnumerator = array.GetEnumerator();
            _objectReturnType = objectReturnType;
        }

        /// <devdoc>
        /// Retrieves the current DictionaryEntry.  This is the same as Entry, but not strongly-typed.
        /// </devdoc>
        @MaybeNull
        public Object getCurrent()
        {
            // Debug.Assert(_arrayEnumerator.Current != null);
            if (_objectReturnType == Keys)
            {
                return ((DictionaryEntry)_arrayEnumerator.getCurrent()).GetKey();
            }
            if (_objectReturnType == Values)
            {
                return ((DictionaryEntry)_arrayEnumerator.getCurrent()).GetValue();
            }
            return GetEntry();
        }

        /// <devdoc>
        /// Retrieves the current DictionaryEntry
        /// </devdoc>
        ///
        @Override
        public DictionaryEntry GetEntry()
        {
            // Debug.Assert(_arrayEnumerator.Current != null);
            return new DictionaryEntry(((DictionaryEntry)_arrayEnumerator.getCurrent()).GetKey(), ((DictionaryEntry)_arrayEnumerator.getCurrent()).GetValue());
        }

        /// <devdoc>
        /// Retrieves the key of the current DictionaryEntry
        /// </devdoc>
        @Override
        public Object GetKey()
        {
            // Debug.Assert(_arrayEnumerator.Current != null);
            return ((DictionaryEntry)_arrayEnumerator.getCurrent()).GetKey();
        }

        /// <devdoc>
        /// Retrieves the value of the current DictionaryEntry
        /// </devdoc>
        @Override
        @MaybeNull
        public Object GetValue()
        {
            // Debug.Assert(_arrayEnumerator.Current != null);
            return ((DictionaryEntry)_arrayEnumerator.getCurrent()).GetValue();
        }

        /// <devdoc>
        /// Resets the enumerator pointer to the beginning.
        /// </devdoc>
        public void Reset() { _arrayEnumerator.Reset(); }

        /// <devdoc>
        /// Moves the enumerator pointer to the next member
        /// </devdoc>
        public boolean MoveNext() { return _arrayEnumerator.MoveNext(); }
    }

    /// <devdoc>
    /// OrderedDictionaryKeyValueCollection implements IList for the Values and Keys properties
    /// that is "live"- it will reflect changes to the OrderedDictionary on the collection made after the getter
    /// was called.
    /// </devdoc>
    private static final class OrderedDictionaryKeyValueCollection
            implements IList
    {
        private final ArrayList _objects;
        @AllowNull
        private final Hashtable _objectsTable;
        @AllowNull
        private final IEqualityComparer _comparer;

        public OrderedDictionaryKeyValueCollection(ArrayList array, Hashtable objectsTable, @AllowNull IEqualityComparer comparer)
        {
            _objects = array;
            _objectsTable = objectsTable;
            _comparer = comparer;
        }

        public OrderedDictionaryKeyValueCollection(ArrayList array)
        {
            _objects = array;
            _comparer = null;
            _objectsTable = null;
        }

        private boolean GetIsKeys() { return _objectsTable != null; }

        @Override
        public void CopyTo(Object array, int index)
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
            } else {
                var en = _objects.GetEnumerator();
                Object o;
                while (en.MoveNext())
                {
                    o = en.getCurrent();
                    java.lang.reflect.Array.set(array, index++, GetIsKeys() ? ((DictionaryEntry)o).GetKey() : ((DictionaryEntry)o).GetValue());
                }
            }
        }

        @Override
        public boolean GetIsReadOnly() { return true; }

        @Override
        public boolean GetIsFixedSize() { return true; }

        @Override
        public boolean getIsSynchronized() { return false; }

        @Override
        public int getCount() { return _objects.getCount(); }

        @Override
        public Object getSyncRoot() { return _objects.getSyncRoot(); }

        @Override
        public IEnumerator GetEnumerator()
        {
            return new OrderedDictionaryEnumerator(_objects, GetIsKeys() ? OrderedDictionaryEnumerator.Keys : OrderedDictionaryEnumerator.Values);
        }

        @Override
        public boolean Contains(@AllowNull Object value)
        {
            if (GetIsKeys())
            {
                // Debug.Assert(_objectsTable is not null);
                return value != null && _objectsTable.ContainsKey(value);
            }

            var en = _objects.GetEnumerator();
            Object o;
            while (en.MoveNext())
            {
                o = en.getCurrent();
                if (java.util.Objects.equals(((DictionaryEntry)o).GetValue(), value))
                {
                    return true;
                }
            }

            return false;
        }

        public int IndexOf(@AllowNull Object value)
        {
            for (int i = 0; i < _objects.getCount(); i++)
            {
                if (GetIsKeys())
                {
                    Object entryKey = ((DictionaryEntry)_objects.GetItem(i)).GetKey();
                    if (_comparer != null)
                    {
                        if (_comparer.Equals(entryKey, value))
                        {
                            return i;
                        }
                    }
                    else if (entryKey.equals(value))
                    {
                        return i;
                    }
                }
                else if (java.util.Objects.equals(((DictionaryEntry)_objects.GetItem(i)).GetValue(), value))
                {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public Object GetItem(int index)
                throws ArgumentOutOfRangeException
        {
            DictionaryEntry entry = (DictionaryEntry)_objects.GetItem(index);
            return GetIsKeys() ? entry.GetKey() : entry.GetValue();
        }

        @Override
        public void SetItem(int index, Object value)
                throws ArgumentOutOfRangeException, NotSupportedException
        {
            throw new NotSupportedException(GetNotSupportedErrorMessage());
        }

        @Override
        public void Insert(int index, @AllowNull Object value)
        {
            throw new NotSupportedException(GetNotSupportedErrorMessage());
        }

        @Override
        public void Remove(@AllowNull Object value)
        {
            throw new NotSupportedException(GetNotSupportedErrorMessage());
        }

        @Override
        public void RemoveAt(int index)
        {
            throw new NotSupportedException(GetNotSupportedErrorMessage());
        }

        @Override
        public int Add(@AllowNull Object value)
        {
            throw new NotSupportedException(GetNotSupportedErrorMessage());
        }

        @Override
        public void Clear()
        {
            throw new NotSupportedException(GetNotSupportedErrorMessage());
        }

        @NotNull
        private String GetNotSupportedErrorMessage() { return GetIsKeys() ? "Not supported in a wrapped key set" : "Not supported in a wrapped value set"; }
    }
}