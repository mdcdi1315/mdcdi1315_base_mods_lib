package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

import java.util.Arrays;

// The SortedList class implements a sorted list of keys and values. Entries in
// a sorted list are sorted by their keys and are accessible both by key and by
// index. The keys of a sorted list can be ordered either according to a
// specific IComparer implementation given when the sorted list is
// instantiated, or according to the IComparable implementation provided
// by the keys themselves. In either case, a sorted list does not allow entries
// with duplicate keys.
//
// A sorted list internally maintains two arrays that store the keys and
// values of the entries. The capacity of a sorted list is the allocated
// length of these internal arrays. As elements are added to a sorted list, the
// capacity of the sorted list is automatically increased as required by
// reallocating the internal arrays.  The capacity is never automatically
// decreased, but users can call either TrimToSize or
// Capacity explicitly.
//
// The GetKeyList and GetValueList methods of a sorted list
// provides access to the keys and values of the sorted list in the form of
// List implementations. The List Objects returned by these
// methods are aliases for the underlying sorted list, so modifications
// made to those lists are directly reflected in the sorted list, and vice
// versa.
//
// The SortedList class provides a convenient way to create a sorted
// copy of another dictionary, such as a Hashtable. For example:
//
// Hashtable h = new Hashtable();
// h.Add(...);
// h.Add(...);
// ...
// SortedList s = new SortedList(h);
//
// The last line above creates a sorted list that contains a copy of the keys
// and values stored in the hashtable. In this particular example, the keys
// will be ordered according to the IComparable interface, which they
// all must implement. To impose a different ordering, SortedList also
// has a constructor that allows a specific IComparer implementation to
// be specified.
//
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class SortedList 
        implements IDictionary, ICloneable 
{
    private Object[] keys; // Do not rename (binary serialization)
    private Object[] values; // Do not rename (binary serialization)
    private int _size; // Do not rename (binary serialization)
    private int version; // Do not rename (binary serialization)
    private IComparer comparer; // Do not rename (binary serialization)
    private KeyList keyList; // Do not rename (binary serialization)
    private ValueList valueList; // Do not rename (binary serialization)

    // Constructs a new sorted list. The sorted list is initially empty and has
    // a capacity of zero. Upon adding the first element to the sorted list the
    // capacity is increased to 16, and then increased in multiples of two as
    // required. The elements of the sorted list are ordered according to the
    // IComparable interface, which must be implemented by the keys of
    // all entries added to the sorted list.
    public SortedList()
    {
        keys = new Object[0];
        values = new Object[0];
        _size = 0;
        comparer = Comparer.Default;
    }

    // Constructs a new sorted list. The sorted list is initially empty and has
    // a capacity of zero. Upon adding the first element to the sorted list the
    // capacity is increased to 16, and then increased in multiples of two as
    // required. The elements of the sorted list are ordered according to the
    // IComparable interface, which must be implemented by the keys of
    // all entries added to the sorted list.
    //
    public SortedList(int initialCapacity)
        throws ArgumentOutOfRangeException
    {
        if (initialCapacity < 0) {
            throw new ArgumentOutOfRangeException("initialCapacity", "Initial capacity cannot be a negative value.");
        }

        comparer = Comparer.Default;
        keys = new Object[initialCapacity];
        values = new Object[initialCapacity];
    }

    // Constructs a new sorted list with a given IComparer
    // implementation. The sorted list is initially empty and has a capacity of
    // zero. Upon adding the first element to the sorted list the capacity is
    // increased to 16, and then increased in multiples of two as required. The
    // elements of the sorted list are ordered according to the given
    // IComparer implementation. If comparer is null, the
    // elements are compared to each other using the IComparable
    // interface, which in that case must be implemented by the keys of all
    // entries added to the sorted list.
    //
    public SortedList(@AllowNull IComparer comparer)
    {
        this();
        if (comparer != null) this.comparer = comparer;
    }

    // Constructs a new sorted list with a given IComparer
    // implementation and a given initial capacity. The sorted list is
    // initially empty, but will have room for the given number of elements
    // before any reallocations are required. The elements of the sorted list
    // are ordered according to the given IComparer implementation. If
    // comparer is null, the elements are compared to each other using
    // the IComparable interface, which in that case must be implemented
    // by the keys of all entries added to the sorted list.
    //
    public SortedList(@AllowNull IComparer comparer, int capacity)
        throws ArgumentOutOfRangeException
    {
        this(comparer);
        SetCapacity(capacity);
    }

    // Constructs a new sorted list containing a copy of the entries in the
    // given dictionary. The elements of the sorted list are ordered according
    // to the IComparable interface, which must be implemented by the
    // keys of all entries in the given dictionary as well as keys
    // subsequently added to the sorted list.
    //
    public SortedList(IDictionary d)
            throws ArgumentNullException
    {
        this(d, null);
    }

    @StackTraceHidden
    private static int GetCountOrFail(IDictionary d)
    {
        if (d == null) {
            throw new ArgumentNullException("d");
        } else {
            return d.getCount();
        }
    }

    // Constructs a new sorted list containing a copy of the entries in the
    // given dictionary. The elements of the sorted list are ordered according
    // to the given IComparer implementation. If comparer is
    // null, the elements are compared to each other using the
    // IComparable interface, which in that case must be implemented
    // by the keys of all entries in the given dictionary as well as keys
    // subsequently added to the sorted list.
    //
    public SortedList(IDictionary d, @AllowNull IComparer comparer)
        throws ArgumentNullException
    {
        this(comparer, GetCountOrFail(d));
        d.GetKeys().CopyTo(keys, 0);
        d.GetValues().CopyTo(values, 0);

        // Array.Sort(Array keys, Array values, IComparer comparer) does not exist in System.Runtime contract v4.0.10.0.
        // This works around that by sorting only on the keys and then assigning values accordingly.
        // Array.Sort(keys, comparer);
        Arrays.sort(keys, comparer::Compare);
        for (int i = 0; i < keys.length; i++) 
        {
            values[i] = d.GetItem(keys[i]);
        }
        _size = d.getCount();
    }

    // Adds an entry with the given key and value to this sorted list. An
    // ArgumentException is thrown if the key is already present in the sorted list.
    //
    public void Add(Object key, Object value)
        throws ArgumentException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        int i = Array.BinarySearch(keys, 0, _size, key, comparer);
        if (i >= 0)
            throw new ArgumentException(StringUtils.Format("The key is already contained in this dictionary.\nOld: {0}\nNew: {1}", GetKey(i), key));
        Insert(~i, key, value);
    }

    // Returns the capacity of this sorted list. The capacity of a sorted list
    // represents the allocated length of the internal arrays used to store the
    // keys and values of the list, and thus also indicates the maximum number
    // of entries the list can contain before a reallocation of the internal
    // arrays is required.
    //
    public int GetCapacity() { return keys.length; }

    public void SetCapacity(int value)
            throws ArgumentOutOfRangeException
    {
        if (value < getCount()) {
            throw new ArgumentOutOfRangeException("value", "New capacity cannot be less than the collection's current number of elements.");
        } else if (value != keys.length) {
            if (value > 0) {
                Object[] newKeys = new Object[value];
                Object[] newValues = new Object[value];
                if (_size > 0) {
                    Array.Copy(keys, newKeys, _size);
                    Array.Copy(values, newValues, _size);
                }
                keys = newKeys;
                values = newValues;
            } else {
                // size can only be zero here.
                // Debug.Assert(_size == 0, "Size is not zero");
                keys = new Object[0];
                values = new Object[0];
            }
        }
    }

    // Returns the number of entries in this sorted list.
    //
    @Override
    public int getCount() { return _size; }

    // Returns a collection representing the keys of this sorted list. This
    // method returns the same Object as GetKeyList, but typed as an
    // ICollection instead of an IList.
    //

    @Override
    public ICollection GetKeys() { return GetKeyList(); }

    // Returns a collection representing the values of this sorted list. This
    // method returns the same Object as GetValueList, but typed as an
    // ICollection instead of an IList.
    //
    @Override
    public ICollection GetValues() { return GetValueList(); }

    // Is this SortedList read-only?
    @Override
    public boolean GetIsReadOnly() { return false; }

    @Override
    public boolean GetIsFixedSize() { return false; }

    // Is this SortedList synchronized (thread-safe)?
    @Override
    public boolean getIsSynchronized() { return false; }

    // Synchronization root for this Object.
    @Override
    public Object getSyncRoot() { return this; }

    // Removes all entries from this sorted list.
    @Override
    public void Clear()
    {
        // clear does not change the capacity
        version++;
        Array.Clear(keys, 0, _size); // Don't need to doc this but we clear the elements so that the gc can reclaim the references.
        Array.Clear(values, 0, _size); // Don't need to doc this but we clear the elements so that the gc can reclaim the references.
        _size = 0;
    }

    // Makes a virtually identical copy of this SortedList.  This is a shallow
    // copy.  IE, the Objects in the SortedList are not cloned - we copy the
    // references to those Objects.
    public Object Clone()
    {
        SortedList sl = new SortedList(_size);
        Array.Copy(keys, sl.keys, _size);
        Array.Copy(values, sl.values, _size);
        sl._size = _size;
        sl.version = version;
        sl.comparer = comparer;
        // Don't copy keyList nor valueList.
        return sl;
    }


    // Checks if this sorted list contains an entry with the given key.
    //
    @Override
    public boolean Contains(Object key) { return IndexOfKey(key) >= 0; }

    // Checks if this sorted list contains an entry with the given key.
    //
    public boolean ContainsKey(Object key)
    {
        // Yes, this is a SPEC'ed duplicate of Contains().
        return IndexOfKey(key) >= 0;
    }

    // Checks if this sorted list contains an entry with the given value. The
    // values of the entries of the sorted list are compared to the given value
    // using the Object.Equals method. This method performs a linear
    // search and is substantially slower than the Contains
    // method.
    //
    public boolean ContainsValue(Object value)
    {
        return IndexOfValue(value) >= 0;
    }

    // Copies the values in this SortedList to an array.
    @Override
    public void CopyTo(Object array, int arrayIndex)
    {
        ArgumentNullException.ThrowIfNull(array, "array");

        if (arrayIndex < 0) {
            throw new ArgumentOutOfRangeException("arrayIndex", "Array index cannot be less than 0.");
        } else if (java.lang.reflect.Array.getLength(array) - arrayIndex < getCount()) {
            throw new ArgumentException("The given array does not have enough space so that all the list's elements can be copied to it.");
        } else {
            for (int i = 0; i < getCount(); i++) {
                java.lang.reflect.Array.set(array, i + arrayIndex, new DictionaryEntry(keys[i], values[i]));
            }
        }
    }

    // Ensures that the capacity of this sorted list is at least the given
    // minimum value. If the current capacity of the list is less than
    // min, the capacity is increased to twice the current capacity or
    // to min, whichever is larger.
    private void EnsureCapacity (int min)
    {
        int newCapacity = keys.length == 0 ? 16 : keys.length * 2;
        if (newCapacity > Array.MaxLength || newCapacity < 0) newCapacity = Array.MaxLength;
        if (newCapacity < min) newCapacity = min;
        SetCapacity(newCapacity);
    }

    // Returns the value of the entry at the given index.
    //
    @NotNull
    public Object GetByIndex(int index)
    {
        if (index < 0 || index >= getCount())
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value, or exceed the collection's size.");
        return values[index];
    }

    // Returns an IDictionaryEnumerator for this sorted list.  If modifications
    // made to the sorted list while an enumeration is in progress,
    // the MoveNext and Remove methods
    // of the enumerator will throw an exception.
    //
    @NotNull
    @Override
    public IDictionaryEnumerator GetEnumerator()
    {
        return new SortedListEnumerator(this, 0, _size, SortedListEnumerator.DictEntry);
    }

    // Returns the key of the entry at the given index.
    //
    @NotNull
    public Object GetKey(int index)
    {
        if (index < 0 || index >= getCount())
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value, or exceed the collection's size.");
        return keys[index];
    }

    // Returns an IList representing the keys of this sorted list. The
    // returned list is an alias for the keys of this sorted list, so
    // modifications made to the returned list are directly reflected in the
    // underlying sorted list, and vice versa. The elements of the returned
    // list are ordered in the same way as the elements of the sorted list. The
    // returned list does not support adding, inserting, or modifying elements
    // (the Add, AddRange, Insert, InsertRange,
    // Reverse, Set, SetRange, and Sort methods
    // throw exceptions), but it does allow removal of elements (through the
    // Remove and RemoveRange methods or through an enumerator).
    // Null is an invalid key value.
    //
    @NotNull
    public IList GetKeyList()
    {
        if (keyList == null)
        {
            keyList = new KeyList(this);
        }
        return keyList;
    }

    // Returns an IList representing the values of this sorted list. The
    // returned list is an alias for the values of this sorted list, so
    // modifications made to the returned list are directly reflected in the
    // underlying sorted list, and vice versa. The elements of the returned
    // list are ordered in the same way as the elements of the sorted list. The
    // returned list does not support adding or inserting elements (the
    // Add, AddRange, Insert and InsertRange
    // methods throw exceptions), but it does allow modification and removal of
    // elements (through the Remove, RemoveRange, Set and
    // SetRange methods or through an enumerator).
    //
    @NotNull
    public IList GetValueList()
    {
        if (valueList == null)
        {
            valueList = new ValueList(this);
        }
        return valueList;
    }

    // Returns the value associated with the given key. If an entry with the
    // given key is not found, the returned value is null.
    //

    @Override
    public Object GetItem(Object key)
            throws ArgumentNullException
    {
        int i = IndexOfKey(key);
        if (i >= 0) return values[i];
        return null;
    }

    @Override
    public void SetItem(Object key, Object value)
            throws ArgumentNullException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        int i = Array.BinarySearch(keys, 0, _size, key, comparer);
        if (i >= 0) {
            values[i] = value;
            version++;
            return;
        }
        Insert(~i, key, value);
    }

    // Returns the index of the entry with a given key in this sorted list. The
    // key is located through a binary search, and thus the average execution
    // time of this method is proportional to Log2(size), where
    // size is the size of this sorted list. The returned value is -1 if
    // the given key does not occur in this sorted list. Null is an invalid
    // key value.
    //
    public int IndexOfKey(Object key)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        int ret = Array.BinarySearch(keys, 0, _size, key, comparer);
        return ret >= 0 ? ret : -1;
    }

    // Returns the index of the first occurrence of an entry with a given value
    // in this sorted list. The entry is located through a linear search, and
    // thus the average execution time of this method is proportional to the
    // size of this sorted list. The elements of the list are compared to the
    // given value using the Object.Equals method.
    //
    public int IndexOfValue(@AllowNull Object value) { return Array.IndexOf(values, value, 0, _size); }

    // Inserts an entry with a given key and value at a given index.
    private void Insert(int index, @AllowNull Object key, @AllowNull Object value)
    {
        if (_size == keys.length) EnsureCapacity(_size + 1);
        if (index < _size) {
            Array.Copy(keys, index, keys, index + 1, _size - index);
            Array.Copy(values, index, values, index + 1, _size - index);
        }
        keys[index] = key;
        values[index] = value;
        _size++;
        version++;
    }

    // Removes the entry at the given index. The size of the sorted list is
    // decreased by one.
    //
    public void RemoveAt(int index)
        throws ArgumentOutOfRangeException
    {
        if (index < 0 || index >= getCount())
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value, or exceed the collection's size.");
        _size--;
        if (index < _size) {
            Array.Copy(keys, index + 1, keys, index, _size - index);
            Array.Copy(values, index + 1, values, index, _size - index);
        }
        keys[_size] = null;
        values[_size] = null;
        version++;
    }

    // Removes an entry from this sorted list. If an entry with the specified
    // key exists in the sorted list, it is removed. An ArgumentException is
    // thrown if the key is null.
    //
    @Override
    public void Remove(Object key)
    {
        int i = IndexOfKey(key);
        if (i >= 0)
            RemoveAt(i);
    }

    // Sets the value at an index to a given value.  The previous value of
    // the given entry is overwritten.
    //
    public void SetByIndex(int index, Object value)
        throws ArgumentOutOfRangeException
    {
        if (index < 0 || index >= getCount())
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value, or exceed the collection's size.");
        values[index] = value;
        version++;
    }

    // Returns a thread-safe SortedList.
    //
    @NotNull
    public static SortedList Synchronized(SortedList list)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(list, "list");

        return new SyncSortedList(list);
    }

    // Sets the capacity of this sorted list to the size of the sorted list.
    // This method can be used to minimize a sorted list's memory overhead once
    // it is known that no new elements will be added to the sorted list. To
    // completely clear a sorted list and release all memory referenced by the
    // sorted list, execute the following statements:
    //
    // sortedList.Clear();
    // sortedList.TrimToSize();
    //
    public void TrimToSize() { SetCapacity(_size); }

    @TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
    private static final class SyncSortedList
            extends SortedList
    {
        private final SortedList _list; // Do not rename (binary serialization)
        private final Object _root; // Do not rename (binary serialization)

        public SyncSortedList(SortedList list)
        {
            super();
            _list = list;
            _root = list.getSyncRoot();
        }

        @Override
        public Object getSyncRoot() { return _root; }

        @Override
        public boolean getIsSynchronized() { return true; }

        @Override
        public void Clear() { synchronized (_root) { _list.Clear(); } }

        @Override
        public boolean GetIsReadOnly() { return _list.GetIsReadOnly(); }

        @Override
        public boolean GetIsFixedSize() { return _list.GetIsFixedSize(); }

        @Override
        public Object Clone() { synchronized (_root) { return _list.Clone(); } }

        @Override
        public void TrimToSize() { synchronized (_root) { _list.TrimToSize(); } }

        @Override
        public int getCount() { synchronized (_root) { return _list.getCount(); } }

        @Override
        public void Remove(Object key) { synchronized (_root) { _list.Remove(key); } }

        @Override
        public IList GetKeyList() { synchronized (_root) { return _list.GetKeyList(); } }

        @Override
        public int GetCapacity() { synchronized (_root) { return _list.GetCapacity(); } }

        @Override
        public void RemoveAt(int index) { synchronized (_root) { _list.RemoveAt(index); } }

        @Override
        public IList GetValueList() { synchronized (_root) { return _list.GetValueList(); } }

        @Override
        public Object GetKey( int index) { synchronized (_root) { return _list.GetKey(index); } }

        @Override
        public boolean Contains(Object key) { synchronized (_root) { return _list.Contains(key); } }

        @Override
        public boolean ContainsKey(Object key) { synchronized (_root) { return _list.ContainsKey(key); } }

        @Override
        public void CopyTo(Object array, int index) { synchronized (_root) { _list.CopyTo(array, index); } }

        @Override
        public Object GetByIndex(int index) { synchronized (_root) { return _list.GetByIndex(index); } }

        @Override
        public IDictionaryEnumerator GetEnumerator() { synchronized (_root) { return _list.GetEnumerator(); } }

        @Override
        public void Add(Object key, @AllowNull Object value) { synchronized (_root) { _list.Add(key, value); } }

        @Override
        public void SetByIndex(int index, Object value) { synchronized (_root) { _list.SetByIndex(index, value); } }

        @Override
        public int IndexOfValue(@AllowNull Object value) { synchronized (_root) { return _list.IndexOfValue(value); } }

        @Override
        public boolean ContainsValue(@AllowNull Object key) { synchronized (_root) { return _list.ContainsValue(key); } }

        @Override
        public Object GetItem(Object key) throws ArgumentNullException { synchronized (_root) { return _list.GetItem(key); } }

        @Override
        public void SetItem(Object key, Object value) throws ArgumentNullException, NotSupportedException { synchronized (_root) { _list.SetItem(key, value); } }

        @Override
        public int IndexOfKey(Object key)
        {
            ArgumentNullException.ThrowIfNull(key, "key");

            synchronized (_root) { return _list.IndexOfKey(key); }
        }
    }

    private static final class SortedListEnumerator
            implements IDictionaryEnumerator, ICloneable
    {
        private final SortedList _sortedList;
        private Object _key = null;
        private Object _value;
        private int _index;
        private final int _startIndex;        // Store for Reset.
        private final int _endIndex;
        private final int _version;
        private boolean _current;       // Is the current element valid?
        private final int _getObjectRetType;  // What should GetObject return?

        public static final int Keys = 1;
        public static final int Values = 2;
        public static final int DictEntry = 3;

        public SortedListEnumerator(SortedList sortedList, int index, int count, int getObjRetType)
        {
            _sortedList = sortedList;
            _index = index;
            _startIndex = index;
            _endIndex = index + count;
            _version = sortedList.version;
            _getObjectRetType = getObjRetType;
            _current = false;
        }

        @Override
        public SortedListEnumerator Clone()
        {
            return new SortedListEnumerator(
                    _sortedList,
                    _startIndex,
                    _endIndex - _startIndex,
                    _getObjectRetType
            );
        }

        @Override
        public Object GetKey()
                throws InvalidOperationException
        {
            if (_version != _sortedList.version)
                throw new InvalidOperationException("The collection was modified after the enumerator was created.");
            else if (_current)
                return _key;
            else
                throw new InvalidOperationException("The enumeration has not yet begun.");
        }

        @Override
        public Object GetValue()
                throws InvalidOperationException
        {
            if (_version != _sortedList.version)
                throw new InvalidOperationException("The collection was modified after the enumerator was created.");
            else if (_current)
                return _value;
            else
                throw new InvalidOperationException("The enumeration has not yet begun.");
        }

        @Override
        public DictionaryEntry GetEntry()
                throws InvalidOperationException
        {
            if (_version != _sortedList.version)
                throw new InvalidOperationException("The collection was modified after the enumerator was created.");
            else if (_current)
                return new DictionaryEntry(_key, _value);
            else
                throw new InvalidOperationException("The enumeration has not yet begun.");
        }

        @Override
        public Object getCurrent()
        {
            if (_current) {
                if (_getObjectRetType == Keys)
                    return _key;
                else if (_getObjectRetType == Values)
                    return _value;
                else
                    return new DictionaryEntry(_key, _value);
            } else {
                throw new InvalidOperationException("The enumeration has not yet begun.");
            }
        }

        @Override
        public boolean MoveNext()
        {
            if (_version != _sortedList.version) {
                throw new InvalidOperationException("The collection was modified after the enumerator was created.");
            } else if (_index < _endIndex) {
                _key = _sortedList.keys[_index];
                _value = _sortedList.values[_index];
                _index++;
                _current = true;
                return true;
            } else {
                _key = null;
                _value = null;
                _current = false;
                return false;
            }
        }

        @Override
        public void Reset()
        {
            if (_version != _sortedList.version)
                throw new InvalidOperationException("The collection was modified after the enumerator was created.");
            _index = _startIndex;
            _current = false;
            _key = null;
            _value = null;
        }
    }
    
    @TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
    private static final class KeyList 
            implements IList
    {
        private final SortedList sortedList; // Do not rename (binary serialization)

        public KeyList(SortedList sortedList)
        {
            this.sortedList = sortedList;
        }

        @Override
        public boolean GetIsReadOnly() { return true; }

        @Override
        public boolean GetIsFixedSize() { return true; }

        @Override
        public int getCount() { return sortedList._size; }

        @Override
        public Object getSyncRoot() { return sortedList.getSyncRoot(); }

        @Override
        public boolean getIsSynchronized() { return sortedList.getIsSynchronized(); }

        @Override
        public Object GetItem(int index) throws ArgumentOutOfRangeException { return sortedList.GetKey(index); }

        @Override
        public void SetItem(int index, Object value)
                throws ArgumentOutOfRangeException, NotSupportedException
        {
            throw new NotSupportedException("Not possible to modify a key to a collection that wraps a SortedList instance.");
        }

        public int Add(@AllowNull Object key)
        {
            throw new NotSupportedException("Not possible to add a key to a collection that wraps a SortedList instance.");
            //            return 0; // suppress compiler warning
        }

        public void Clear()
        {
            throw new NotSupportedException("Not possible to clear a collection that wraps a SortedList instance.");
        }

        public boolean Contains (@AllowNull Object key)
        {
            return sortedList.Contains(key);
        }

        public void CopyTo(Object array,int arrayIndex)
        {
            // defer error checking to Array.Copy
            Array.Copy(sortedList.keys, 0, (Object[])array, arrayIndex, sortedList.getCount());
        }

        public void Insert(int index, Object value)
        {
            throw new NotSupportedException("Not possible to add a key to a collection that wraps a SortedList instance.");
        }

        @Override
        public IEnumerator GetEnumerator()
        {
            return new SortedListEnumerator(sortedList, 0, sortedList.getCount(), SortedListEnumerator.Keys);
        }

        public int IndexOf (@AllowNull Object key)
        {
            ArgumentNullException.ThrowIfNull(key);

            int i = Array.BinarySearch(sortedList.keys, 0,
                    sortedList.getCount(), key, sortedList.comparer);
            if (i >= 0) return i;
            return -1;
        }

        public void Remove(@AllowNull Object key)
        {
            throw new NotSupportedException("Not possible to remove a key from a collection that wraps a SortedList instance.");
        }

        public void RemoveAt(int index)
        {
            throw new NotSupportedException("Not possible to remove a key from a collection that wraps a SortedList instance.");
        }
    }

    @TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
    private static final class ValueList
            implements IList
    {
        private final SortedList sortedList; // Do not rename (binary serialization)

        public ValueList(SortedList sortedList)
        {
            this.sortedList = sortedList;
        }

        @Override
        public boolean GetIsReadOnly() { return true; }

        @Override
        public boolean GetIsFixedSize() { return true; }

        @Override
        public int getCount() { return sortedList._size; }

        @Override
        public Object getSyncRoot() { return sortedList.getSyncRoot(); }

        @Override
        public boolean getIsSynchronized() { return sortedList.getIsSynchronized(); }

        @Override
        public boolean Contains(@AllowNull Object key) { return sortedList.ContainsValue(key); }

        @Override
        public Object GetItem(int index) throws ArgumentOutOfRangeException { return sortedList.GetByIndex(index); }

        @Override
        public void SetItem(int index, Object value)
                throws ArgumentOutOfRangeException, NotSupportedException
        {
            throw new NotSupportedException("Not possible to modify a key to a collection that wraps a SortedList instance.");
        }

        @Override
        public int Add(@AllowNull Object key)
        {
            throw new NotSupportedException("Not possible to add a key to a collection that wraps a SortedList instance.");
            //            return 0; // suppress compiler warning
        }

        @Override
        public void Clear()
        {
            throw new NotSupportedException("Not possible to clear a collection that wraps a SortedList instance.");
        }

        @Override
        public void CopyTo(Object array, int arrayIndex)
        {
            // defer error checking to Array.Copy
            Array.Copy(sortedList.values, 0, (Object[])array, arrayIndex, sortedList.getCount());
        }

        public void Insert(int index, Object value)
        {
            throw new NotSupportedException("Not possible to add a key to a collection that wraps a SortedList instance.");
        }

        @Override
        public IEnumerator GetEnumerator()
        {
            return new SortedListEnumerator(sortedList, 0, sortedList.getCount(), SortedListEnumerator.Values);
        }

        public int IndexOf(@AllowNull Object key)
        {
            ArgumentNullException.ThrowIfNull(key, "key");

            int i = Array.BinarySearch(sortedList.values, 0,
                    sortedList.getCount(), key, sortedList.comparer);
            if (i >= 0) return i;
            return -1;
        }

        public void Remove(@AllowNull Object key)
        {
            throw new NotSupportedException("Not possible to remove a key from a collection that wraps a SortedList instance.");
        }

        public void RemoveAt(int index)
        {
            throw new NotSupportedException("Not possible to remove a key from a collection that wraps a SortedList instance.");
        }
    }
}
