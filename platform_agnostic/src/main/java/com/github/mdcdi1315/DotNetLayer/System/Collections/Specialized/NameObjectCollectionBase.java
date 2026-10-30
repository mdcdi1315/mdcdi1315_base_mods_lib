package com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MemberNotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.UnconditionalSuppressMessage;

public abstract class NameObjectCollectionBase
        implements ICollection
{
    private int _version;
    private boolean _readOnly;
    private ArrayList _entriesArray;
    private volatile Hashtable _entriesTable;
    private final IEqualityComparer _keyComparer;
    private volatile NameObjectEntry _nullKeyEntry;
    @AllowNull
    private KeysCollection _keys;

    private static final OrdinalIgnoreCaseStrComparer s_defaultComparer = new OrdinalIgnoreCaseStrComparer();

    private static final class OrdinalIgnoreCaseStrComparer
            implements IComparer, IEqualityComparer
    {
        @Override
        public int Compare(Object x, Object y)
        {
            if (x instanceof String sa && y instanceof String sb) {
                return String.CASE_INSENSITIVE_ORDER.compare(sa, sb);
            } else {
                return Comparer.Default.Compare(x, y);
            }
        }

        @Override
        public int GetHashCode(Object obj) throws ArgumentNullException { return obj.hashCode(); }

        @Override
        public boolean Equals(Object x, Object y) throws ArgumentException { return Compare(x, y) == 0; }
    }

    protected NameObjectCollectionBase()
    {
        this(s_defaultComparer);
    }

    protected NameObjectCollectionBase(@AllowNull IEqualityComparer equalityComparer)
    {
        _keyComparer = equalityComparer == null ? s_defaultComparer : equalityComparer;
        Reset();
    }

    protected NameObjectCollectionBase(int capacity, @AllowNull IEqualityComparer equalityComparer)
    {
        this(equalityComparer);
        Reset(capacity);
    }

    protected NameObjectCollectionBase(int capacity)
    {
        _keyComparer = s_defaultComparer;
        Reset(capacity);
    }

    IEqualityComparer GetComparer() { return _keyComparer; }

    @MemberNotNull(Members = { "_entriesArray", "_entriesTable" })
    private void Reset()
    {
        _entriesArray = new ArrayList();
        _entriesTable = new Hashtable(_keyComparer);
        _nullKeyEntry = null;
        _version++;
    }

    @MemberNotNull(Members = { "_entriesArray", "_entriesTable" })
    private void Reset(int capacity)
    {
        _entriesArray = new ArrayList(capacity);
        _entriesTable = new Hashtable(capacity, _keyComparer);
        _nullKeyEntry = null;
        _version++;
    }

    @MaybeNull
    private NameObjectEntry FindEntry(@AllowNull String key)
    {
        if (key != null)
            return (NameObjectEntry)_entriesTable.GetItem(key);
        else
            return _nullKeyEntry;
    }

    protected boolean GetIsReadOnly()
    {
        return _readOnly;
    }

    protected void SetIsReadOnly(boolean value) { _readOnly = value; }

    protected boolean BaseHasKeys() { return (_entriesTable.getCount() > 0);  /* any entries with keys?*/ }

    //
    // Methods to add / remove entries
    //

    protected void BaseAdd(@AllowNull String name, @AllowNull Object value)
    {
        if (_readOnly)
            throw new NotSupportedException("The collection is read-only.");

        NameObjectEntry entry = new NameObjectEntry(name, value);

        // insert entry into hashtable
        if (name != null)
        {
            if (_entriesTable.GetItem(name) == null)
                _entriesTable.Add(name, entry);
        }
        else
        {
            // null key -- special case -- hashtable doesn't like null keys
            if (_nullKeyEntry == null) { _nullKeyEntry = entry; }
        }

        // add entry to the list
        _entriesArray.Add(entry);

        _version++;
    }

    protected void BaseRemove(@AllowNull String name)
    {
        if (_readOnly)
            throw new NotSupportedException("The collection is read-only.");

        if (name != null)
        {
            // remove from hashtable
            _entriesTable.Remove(name);

            // remove from array
            for (int i = _entriesArray.getCount() - 1; i >= 0; i--)
            {
                if (_keyComparer.Equals(name, BaseGetKey(i)))
                    _entriesArray.RemoveAt(i);
            }
        }
        else
        { // null key -- special case
            // null out special 'null key' entry
            _nullKeyEntry = null;

            // remove from array
            for (int i = _entriesArray.getCount() - 1; i >= 0; i--)
            {
                if (BaseGetKey(i) == null)
                    _entriesArray.RemoveAt(i);
            }
        }

        _version++;
    }

    protected void BaseRemoveAt(int index)
    {
        if (_readOnly)
            throw new NotSupportedException("The collection is read-only.");

        String key = BaseGetKey(index);

        if (key != null)
        {
            // remove from hashtable
            _entriesTable.Remove(key);
        }
        else
        { // null key -- special case
            // null out special 'null key' entry
            _nullKeyEntry = null;
        }

        // remove from array
        _entriesArray.RemoveAt(index);

        _version++;
    }

    protected void BaseClear()
    {
        if (_readOnly)
            throw new NotSupportedException("The collection is read-only.");

        Reset();
    }

    //
    // Access by name
    //

    @MaybeNull
    protected Object BaseGet(@AllowNull String name)
    {
        NameObjectEntry e = FindEntry(name);
        if (e == null) {
            return null;
        } else {
            return e.Value;
        }
    }

    protected void BaseSet(@AllowNull String name, @AllowNull Object value)
    {
        if (_readOnly)
            throw new NotSupportedException("The collection is read-only.");

        NameObjectEntry entry = FindEntry(name);
        if (entry != null)
        {
            entry.Value = value;
            _version++;
        }
        else
        {
            BaseAdd(name, value);
        }
    }

    //
    // Access by index
    //

    @MaybeNull
    protected Object BaseGet(int index)
    {
        NameObjectEntry entry = (NameObjectEntry)_entriesArray.GetItem(index);   // no null entry added to the array
        if (entry == null) {
            return null;
        } else {
            return entry.Value;
        }
    }

    @MaybeNull
    protected String BaseGetKey(int index)
    {
        NameObjectEntry entry = (NameObjectEntry)_entriesArray.GetItem(index);
        if (entry == null) {
            return null;
        } else {
            return entry.Key;
        }
    }

    protected void BaseSet(int index, @AllowNull Object value)
    {
        if (_readOnly)
            throw new NotSupportedException("The collection is read-only.");

        NameObjectEntry entry = (NameObjectEntry)_entriesArray.GetItem(index);
        if (entry != null) {
            entry.Value = value;
        }
        _version++;
    }

    //
    // ICollection implementation
    //
    @Override
    public Object getSyncRoot() { return this; }

    @Override
    public boolean getIsSynchronized() { return false; }

    @Override
    public int getCount() { return _entriesArray.getCount(); }

    @Override
    public IEnumerator GetEnumerator() { return new NameObjectKeysEnumerator(this); }

    @Override
    public void CopyTo(Object array, int index)
    {
        ArgumentNullException.ThrowIfNull(array, "array");

        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (java.lang.reflect.Array.getLength(array) - index < _entriesArray.getCount()) {
            throw new ArgumentException("The specified array does not have enough space to place all of the collection's entries.");
        }

        for (IEnumerator e = this.GetEnumerator(); e.MoveNext();)
            java.lang.reflect.Array.set(array, index++, e.getCurrent());
    }

    //
    //  Helper methods to get arrays of keys and values
    //

    protected String[] BaseGetAllKeys()
    {
        int n = _entriesArray.getCount();
        String[] allKeys = new String[n];

        for (int i = 0; i < n; i++)
            allKeys[i] = BaseGetKey(i);

        return allKeys;
    }

    protected Object[] BaseGetAllValues()
    {
        int n = _entriesArray.getCount();
        Object[] allValues = new Object[n];

        for (int i = 0; i < n; i++)
            allValues[i] = BaseGet(i);

        return allValues;
    }

    @UnconditionalSuppressMessage(
            Category = "AotAnalysis",
            CheckId = "IL3050:RequiresDynamicCode",
            Justification = "The API only works for reference type arguments and code for reference typed arrays is shareable."
    )
    protected Object[] BaseGetAllValues(Class<?> type)
    {
        ArgumentNullException.ThrowIfNull(type, "type");

        int n = _entriesArray.getCount();
        Object[] allValues = (Object[]) java.lang.reflect.Array.newInstance(type, n);

        for (int i = 0; i < n; i++)
        {
            allValues[i] = BaseGet(i);
        }

        return allValues;
    }

    //
    // Keys property
    //

    public KeysCollection GetKeys()
    {
        if (_keys == null)
        {
            _keys = new KeysCollection(this);
        }
        return _keys;
    }

    //
    // Simple entry class to allow substitution of values and indexed access to keys
    //

    private static final class NameObjectEntry
    {
        public NameObjectEntry(@AllowNull String name, @AllowNull Object value)
        {
            Key = name;
            Value = value;
        }

        @AllowNull
        public String Key;
        @AllowNull
        public Object Value;
    }

    //
    // Enumerator over keys of NameObjectCollection
    //

    private static final class NameObjectKeysEnumerator
            implements IEnumerator
    {
        private int _pos;
        private final int _version;
        private final NameObjectCollectionBase _coll;

        public NameObjectKeysEnumerator(NameObjectCollectionBase coll)
        {
            _coll = coll;
            _version = _coll._version;
            _pos = -1;
        }

        @Override
        public boolean MoveNext()
        {
            if (_version != _coll._version)
                throw new InvalidOperationException("The enumerator was created after the collection was modified.");

            if (_pos < _coll.getCount() - 1)
            {
                _pos++;
                return true;
            }
            else
            {
                _pos = _coll.getCount();
                return false;
            }
        }

        @Override
        public void Reset()
        {
            if (_version != _coll._version)
                throw new InvalidOperationException("The enumerator was created after the collection was modified.");
            _pos = -1;
        }

        @Override
        public String getCurrent()
        {
            if (_pos >= 0 && _pos < _coll.getCount()) {
                return _coll.BaseGetKey(_pos);
            } else {
                throw new InvalidOperationException("The enumeration has not yet begun.");
            }
        }
    }

    //
    // Keys collection
    //

    public static class KeysCollection
            implements ICollection
    {
        private final NameObjectCollectionBase _coll;

        private KeysCollection(NameObjectCollectionBase coll) { _coll = coll; }

        // Indexed access

        @MaybeNull
        public String GetItem(int index) { return Get(index); }

        @MaybeNull
        public String Get(int index) { return _coll.BaseGetKey(index); }

        // ICollection implementation

        @Override
        public int getCount() { return _coll.getCount(); }

        @Override
        public boolean getIsSynchronized() { return false; }

        @Override
        public Object getSyncRoot() { return _coll.getSyncRoot(); }

        @Override
        public IEnumerator GetEnumerator() { return new NameObjectKeysEnumerator(_coll); }

        @Override
        public void CopyTo(Object array, int index)
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
            } else if (java.lang.reflect.Array.getLength(array) - index < _coll.getCount()) {
                throw new ArgumentException("The specified array does not have enough space to place all of the collection's entries.");
            }

            for (IEnumerator e = this.GetEnumerator(); e.MoveNext();)
                java.lang.reflect.Array.set(array, index++, e.getCurrent());
        }
    }
}
