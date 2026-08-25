package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.RequiresDynamicCode;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class ArrayList
    implements IList, ICloneable
{
    private int _size; // Do not rename (binary serialization)
    private int _version; // Do not rename (binary serialization)
    private Object[] _items; // Do not rename (binary serialization)

    private static final int _defaultCapacity = 4;

    public ArrayList()
    {
        _size = 0;
        _version = 0;
        _items = new Object[0];
    }

    public ArrayList(int capacity)
    {
        if (capacity < 0)
        {
            throw new ArgumentOutOfRangeException("capacity", "Capacity must not be a negative number");
        } else {
            _size = 0;
            _version = 0;
            _items = new Object[capacity];
        }
    }

    public ArrayList(ICollection c)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(c, "c");

        int count = c.getCount();
        if (count == 0)
        {
            _items = new Object[0];
        }
        else
        {
            _items = new Object[count];
            AddRange(c);
        }
    }

    // Returns a thread-safe wrapper around an IList.
    //
    public static IList Synchronized(IList list)
    {
        ArgumentNullException.ThrowIfNull(list);

        return new SyncIList(list);
    }

    // Returns a thread-safe wrapper around a ArrayList.
    //
    public static ArrayList Synchronized(ArrayList list)
    {
        ArgumentNullException.ThrowIfNull(list, "list");

        return new SyncArrayList(list);
    }

    // Returns an IList that contains count copies of value.
    //
    public static ArrayList Repeat(@AllowNull Object value, int count)
    {
        if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
        } else {
            ArrayList list = new ArrayList(Math.max(count, _defaultCapacity));
            for (int i = 0; i < count; i++)
                list.Add(value);
            return list;
        }
    }

    // Creates a ArrayList wrapper for a particular IList.  This does not
    // copy the contents of the IList, but only wraps the IList.  So any
    // changes to the underlying list will affect the ArrayList.  This would
    // be useful if you want to Reverse a subrange of an IList, or want to
    // use a generic BinarySearch or Sort method without implementing one yourself.
    // However, since these methods are generic, the performance may not be
    // nearly as good for some operations as they would be on the IList itself.
    //
    public static ArrayList Adapter(IList list)
    {
        ArgumentNullException.ThrowIfNull(list);

        return new IListWrapper(list);
    }

    // Returns a list wrapper that is fixed at the current size.  Operations
    // that add or remove items will fail, however, replacing items is allowed.
    //
    public static IList FixedSize(IList list)
    {
        ArgumentNullException.ThrowIfNull(list);

        return new FixedSizeList(list);
    }

    // Returns a list wrapper that is fixed at the current size.  Operations
    // that add or remove items will fail, however, replacing items is allowed.
    //
    public static ArrayList FixedSize(ArrayList list)
    {
        ArgumentNullException.ThrowIfNull(list);

        return new FixedSizeArrayList(list);
    }

    // Returns a read-only IList wrapper for the given IList.
    //
    public static IList ReadOnly(IList list)
    {
        ArgumentNullException.ThrowIfNull(list);

        return new ReadOnlyList(list);
    }

    // Returns a read-only ArrayList wrapper for the given ArrayList.
    //
    public static ArrayList ReadOnly(ArrayList list)
    {
        ArgumentNullException.ThrowIfNull(list);

        return new ReadOnlyArrayList(list);
    }

    // Ensures that the capacity of this list is at least the given minimum
    // value. If the current capacity of the list is less than min, the
    // capacity is increased to twice the current capacity or to min,
    // whichever is larger.
    private void EnsureCapacity(int min)
    {
        if (_items.length < min)
        {
            long newCapacity = _items.length == 0 ? _defaultCapacity : _items.length * 2L;
            // Allow the list to grow to maximum possible capacity (~2G elements) before encountering overflow.
            // Note that this check works even when _items.Length overflowed thanks to the (uint) cast
            if (newCapacity > Array.MaxLength) newCapacity = Array.MaxLength;
            if (newCapacity < min) newCapacity = min;
            SetCapacity((int)newCapacity);
        }
    }

    // Implements an enumerator for a ArrayList. The enumerator uses the
    // internal version number of the list to ensure that no modifications are
    // made to the list while an enumeration is in progress.
    private static final class ArrayListEnumerator
            implements IEnumerator, ICloneable
    {
        private final ArrayList _list;
        private int _index;
        private final int _endIndex;       // Where to stop.
        private final int _version;
        @AllowNull
        private Object _currentElement;
        private final int _startIndex;     // Save this for Reset.

        public ArrayListEnumerator(ArrayList list, int index, int count)
        {
            _list = list;
            _startIndex = index;
            _index = index - 1;
            _endIndex = _index + count;  // last valid index
            _version = list._version;
            _currentElement = null;
        }

        @NotNull
        public ArrayListEnumerator Clone() { return new ArrayListEnumerator(_list, _index, _endIndex); }

        public boolean MoveNext()
        {
            if (_version != _list._version)
                throw new InvalidOperationException("The collection was modified while the enumerator is still alive.");
            if (_index < _endIndex)
            {
                _currentElement = _list.GetItem(++_index);
                return true;
            }
            else
            {
                _index = _endIndex + 1;
            }

            return false;
        }

        public Object getCurrent()
        {
            if (_index < _startIndex)
            {
                throw new InvalidOperationException("Enumeration has not yet begun.");
            }
            else if (_index > _endIndex)
            {
                throw new InvalidOperationException("Enumeration has been ended.");
            }
            return _currentElement;
        }

        public void Reset()
        {
            if (_version != _list._version) throw new InvalidOperationException("The collection was modified while the enumerator is still alive.");
            _index = _startIndex - 1;
        }
    }

    private static final class ArrayListEnumeratorSimple
            implements IEnumerator, ICloneable
    {
        private final ArrayList _list;
        private int _index;
        private final int _version;
        @AllowNull
        private Object _currentElement;
        private final boolean _isArrayList;
        // this object is used to indicate enumeration has not started or has terminated
        private static final Object s_dummyObject = new Object();

        public ArrayListEnumeratorSimple(ArrayList list)
        {
            _list = list;
            _index = -1;
            _version = list._version;
            _currentElement = s_dummyObject;
            _isArrayList = list.getClass() == ArrayList.class;
        }

        public ArrayListEnumeratorSimple Clone() { return new ArrayListEnumeratorSimple(_list); }

        public boolean MoveNext()
        {
            if (_version != _list._version)
            {
                throw new InvalidOperationException("The collection was modified while the enumerator is still alive.");
            }

            if (_isArrayList)
            {  // avoid calling virtual methods if we are operating on ArrayList to improve performance
                if (_index < _list._size - 1)
                {
                    _currentElement = _list._items[++_index];
                    return true;
                }
                else
                {
                    _currentElement = s_dummyObject;
                    _index = _list._size;
                    return false;
                }
            }
            else
            {
                if (_index < _list.getCount() - 1)
                {
                    _currentElement = _list.GetItem(++_index);
                    return true;
                }
                else
                {
                    _index = _list.getCount();
                    _currentElement = s_dummyObject;
                    return false;
                }
            }
        }

        @MaybeNull
        public Object getCurrent()
        {
            Object temp = _currentElement;
            if (s_dummyObject == temp)
            {
                // check if enumeration has not started or has terminated
                if (_index == -1)
                {
                    throw new InvalidOperationException("Enumeration has not yet begun.");
                }
                else
                {
                    throw new InvalidOperationException("Enumeration has been ended.");
                }
            }

            return temp;
        }

        public void Reset()
        {
            if (_version != _list._version) {
                throw new InvalidOperationException("The collection was modified while the enumerator is still alive.");
            } else {
                _currentElement = s_dummyObject;
                _index = -1;
            }
        }
    }

    private static final class IListWrapper
        extends ArrayList
    {
        private final IList _list;

        public IListWrapper(IList list)
        {
            _list = list;
            super._version = 0; // list doesn't contain a version number
        }

        @Override
        public int getCount() { return _list.getCount(); }

        @Override
        public int GetCapacity() { return _list.getCount(); }

        @Override
        public Object getSyncRoot() { return _list.getSyncRoot(); }

        @Override
        public boolean GetIsReadOnly() { return _list.GetIsReadOnly(); }

        @Override
        public boolean GetIsFixedSize() { return _list.GetIsFixedSize(); }

        @Override
        public boolean getIsSynchronized() { return _list.getIsSynchronized(); }

        @Override
        public Object GetItem(int index) throws ArgumentOutOfRangeException { return _list.GetItem(index); }

        @Override
        public void SetItem(int index, Object value)
                throws ArgumentOutOfRangeException, NotSupportedException
        {
            _list.SetItem(index, value);
            super._version++;
        }

        @Override
        public void SetCapacity(int value)
                throws ArgumentOutOfRangeException
        {
            if (value < getCount())
                throw new ArgumentOutOfRangeException("value", "New capacity must not be less than the list's size.");
        }

        public int Add(@AllowNull Object obj)
        {
            int i = _list.Add(obj);
            super._version++;
            return i;
        }

        public void AddRange(ICollection c) { InsertRange(getCount(), c); }

        // Other overloads with automatically work
        public int BinarySearch(int index, int count, @AllowNull Object value, @AllowNull IComparer comparer)
        {
            if (index < 0)
                throw new ArgumentOutOfRangeException("index", index, "Index must not be a negative number");
            // ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
            if (count < 0)
                throw new ArgumentOutOfRangeException("count", count, "Count must not be a negative number");
            // ThrowHelper.ThrowLengthArgumentOutOfRange_ArgumentOutOfRange_NeedNonNegNum();
            if (getCount() - index < count)
                throw new ArgumentException("array", "Specified index and count parameters do exceed the array's bounds.");
            // ThrowHelper.ThrowArgumentException(ExceptionResource.Argument_InvalidOffLen);

            if (comparer == null) { comparer = Comparer.Default; }

            int lo = index;
            int hi = index + count - 1;
            int mid;
            while (lo <= hi)
            {
                mid = lo + ((hi - lo) >>> 1);
                int r = comparer.Compare(value, _list.GetItem(mid));
                if (r == 0)
                    return mid;
                if (r < 0)
                    hi = mid - 1;
                else
                    lo = mid + 1;
            }
            // return bitwise complement of the first element greater than value.
            // Since hi is less than lo now, ~lo is the correct item.
            return ~lo;
        }

        public void Clear()
        {
            // If _list is an array, it will support Clear method.
            // We shouldn't allow clear operation on a FixedSized ArrayList
            if (_list.GetIsFixedSize())
            {
                throw new NotSupportedException("The specified collection is of fixed size.");
            }

            _list.Clear();
            super._version++;
        }

        @Override
        public ArrayList Clone() { return new IListWrapper(_list); }

        @Override
        public boolean Contains(Object value) { return _list.Contains(value); }

        @Override
        public void CopyTo(Object array, int arrayIndex) throws ArgumentNullException, ArgumentOutOfRangeException { _list.CopyTo(array, arrayIndex); }

        @Override
        public IEnumerator GetEnumerator() { return _list.GetEnumerator(); }

        @Override
        public IEnumerator GetEnumerator(int index, int count)
                throws ArgumentException
        {
            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
            } else if (count < 0) {
                throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
            } else if (_list.getCount() - index < count) {
                throw new ArgumentException("The specified index and count parameter values do exceed the list's size.");
            } else {
                return new IListWrapperEnumWrapper(this, index, count);
            }
        }

        @Override
        public int IndexOf(Object value) { return _list.IndexOf(value); }

        @Override
        public int IndexOf(Object value, int startIndex) { return IndexOf(value, startIndex, _list.getCount() - startIndex); }

        @Override
        public int IndexOf(Object value, int startIndex, int count)
        {
            if (startIndex > getCount()) {
                throw new ArgumentOutOfRangeException("startIndex", "Index must be less than the list's size and larger or equal to zero.");
            } else if (count < 0 || startIndex > getCount() - count) {
                throw new ArgumentOutOfRangeException("count", "Count must be greater than or equal to zero and less than the list's size.");
            } else {
                int endIndex = startIndex + count;
                if (value == null)
                {
                    for (int i = startIndex; i < endIndex; i++)
                        if (_list.GetItem(i) == null)
                            return i;
                }
                else
                {
                    for (int i = startIndex; i < endIndex; i++)
                        if (_list.GetItem(i) instanceof Object o && o.equals(value))
                            return i;
                }
                return -1;
            }
        }

        @Override
        public void Insert(int index, Object value)
        {
            _list.Insert(index, value);
            super._version++;
        }

        @Override
        public void InsertRange(int index, ICollection c)
        {
            ArgumentNullException.ThrowIfNull(c, "c");

            if (index < 0 || index > getCount())
                throw new ArgumentOutOfRangeException("index", "Index must not be a negative number, or greater than the list's size.");

            if (c.getCount() > 0)
            {
                if (_list instanceof ArrayList al)
                {
                    // We need to special case ArrayList.
                    // When c is a range of _list, we need to handle this in a special way.
                    // See ArrayList.InsertRange for details.
                    al.InsertRange(index, c);
                }
                else
                {
                    IEnumerator en = c.GetEnumerator();
                    while (en.MoveNext())
                    {
                        _list.Insert(index++, en.getCurrent());
                    }
                }
                super._version++;
            }
        }

        @Override
        public int LastIndexOf(@AllowNull Object value)
        {
            int count = _list.getCount();
            return LastIndexOf(value, count - 1, count);
        }

        @Override
        public int LastIndexOf(@AllowNull Object value, int startIndex)
        {
            return LastIndexOf(value, startIndex, startIndex + 1);
        }

        @Override
        public int LastIndexOf(@AllowNull Object value, int startIndex, int count)
        {
            if (_list.getCount() == 0) return -1;

            if (startIndex < 0 || startIndex >= _list.getCount()) throw new ArgumentOutOfRangeException("startIndex", "Index must not be a negative number, and less than the list's size.");
            if (count < 0 || count > startIndex + 1) throw new ArgumentOutOfRangeException("count", "Count must not be a negative number and less than the start index.");

            int endIndex = startIndex - count + 1;
            if (value == null)
            {
                for (int i = startIndex; i >= endIndex; i--)
                    if (_list.GetItem(i) == null)
                        return i;
            }
            else
            {
                for (int i = startIndex; i >= endIndex; i--)
                    if (_list.GetItem(i) instanceof Object o && o.equals(value))
                        return i;
            }
            return -1;
        }

        @Override
        public void Remove(Object value)
                throws NotSupportedException
        {
            int index = IndexOf(value);
            if (index >= 0)
                RemoveAt(index);
        }

        @Override
        public void RemoveAt(int index)
        {
            _list.RemoveAt(index);
            super._version++;
        }

        @Override
        public void RemoveRange(int index, int count)
        {
            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
            } else if (count < 0) {
                throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
            } else if (_list.getCount() - index < count) {
                throw new ArgumentException("The selected index and count value exceed the list's bounds.");
            } else if (count > 0) {
                // be consistent with ArrayList
                super._version++;
                while (count > 0)
                {
                    _list.RemoveAt(index);
                    count--;
                }
            }
        }

        @Override
        public void Reverse(int index, int count)
        {
            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
            } else if (count < 0) {
                throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
            } else if (_list.getCount() - index < count) {
                throw new ArgumentException("The selected index and count value exceed the list's bounds.");
            } else {
                int i = index;
                int j = index + count - 1;
                while (i < j)
                {
                    Object tmp = _list.GetItem(i);
                    _list.SetItem(i++, _list.GetItem(j));
                    _list.SetItem(j--, tmp);
                }
                super._version++;
            }
        }

        @Override
        public void SetRange(int index, ICollection c)
        {
            ArgumentNullException.ThrowIfNull(c, "c");

            int c_count = c.getCount();

            if (index < 0 || index > _list.getCount() - c_count)
            {
                throw new ArgumentOutOfRangeException("index", "The index must be between 0 and less or equal than the list's size.");
            }

            if (c_count > 0)
            {
                IEnumerator en = c.GetEnumerator();
                while (en.MoveNext())
                {
                    _list.SetItem(index++, en.getCurrent());
                }
                super._version++;
            }
        }

        public ArrayList GetRange(int index, int count)
        {
            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
            } else if (count < 0) {
                throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
            } else if (_list.getCount() - index < count) {
                throw new ArgumentException("The specified index and count parameter values do exceed the list's size.");
            } else {
                return new Range(this, index, count);
            }
        }

        public Object[] ToArray()
        {
            int count = _list.getCount();
            if (count == 0)
                return new Object[0];

            Object[] array = new Object[count];
            _list.CopyTo(array, 0);
            return array;
        }

        @SuppressWarnings("unchecked")
        @RequiresDynamicCode(Message = "The code for an array of the specified type might not be available.")
        public <T> T[] ToArray(Class<T> type)
        {
            ArgumentNullException.ThrowIfNull(type);

            T[] array = (T[])Array.CreateInstance(type, _list.getCount());
            _list.CopyTo(array, 0);
            return array;
        }

        public void TrimToSize()
        {
            // Can't really do much here...
        }

        // This is the enumerator for an IList that's been wrapped in another
        // class that implements all of ArrayList's methods.
        private static final class IListWrapperEnumWrapper
                implements IEnumerator, ICloneable
        {
            private IEnumerator _en = null;
            private int _remaining;
            private int _initialStartIndex; // for reset
            private int _initialCount;      // for reset
            private boolean _firstCall;        // firstCall to MoveNext

            @SuppressWarnings("StatementWithEmptyBody")
            public IListWrapperEnumWrapper(IListWrapper listWrapper, int startIndex, int count)
            {
                _en = listWrapper.GetEnumerator();
                _initialStartIndex = startIndex;
                _initialCount = count;
                while (startIndex-- > 0 && _en.MoveNext()) ;
                _remaining = count;
                _firstCall = true;
            }

            private IListWrapperEnumWrapper() { }

            public IListWrapperEnumWrapper Clone()
            {
                var clone = new IListWrapperEnumWrapper();
                clone._en = (IEnumerator)((ICloneable)_en).Clone();
                clone._initialStartIndex = _initialStartIndex;
                clone._initialCount = _initialCount;
                clone._remaining = _remaining;
                clone._firstCall = _firstCall;
                return clone;
            }

            @Override
            public boolean MoveNext()
            {
                if (_firstCall)
                {
                    _firstCall = false;
                    return _remaining-- > 0 && _en.MoveNext();
                }
                if (_remaining < 0)
                    return false;
                return _en.MoveNext() && _remaining-- > 0;
            }

            @NotNull
            @Override
            public Object getCurrent()
            {
                if (_firstCall) {
                    throw new InvalidOperationException("Enumeration has not yet begun.");
                } else if (_remaining < 0) {
                    throw new InvalidOperationException("Enumeration has been ended.");
                } else {
                    return _en.getCurrent();
                }
            }

            @Override
            @SuppressWarnings("StatementWithEmptyBody")
            public void Reset()
            {
                _en.Reset();
                int startIndex = _initialStartIndex;
                while (startIndex-- > 0 && _en.MoveNext()) ;
                _remaining = _initialCount;
                _firstCall = true;
            }
        }
    }

    private static final class SyncArrayList
        extends ArrayList
    {
        private final Object _root;
        private final ArrayList _list;

        public SyncArrayList(ArrayList list)
        {
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
        public void TrimToSize() { synchronized (_root) { _list.TrimToSize(); } }

        @Override
        public int getCount() { synchronized (_root) { return _list.getCount(); } }

        @Override
        public Object[] ToArray() { synchronized (_root) { return _list.ToArray(); } }

        @Override
        public int GetCapacity() { synchronized (_root) { return _list.GetCapacity(); } }

        @Override
        public void RemoveAt(int index) { synchronized (_root) { _list.RemoveAt(index); } }

        @Override
        public boolean GetIsReadOnly() { synchronized (_root) { return _list.GetIsReadOnly(); } }

        @Override
        public int IndexOf(Object value) { synchronized (_root) { return _list.IndexOf(value); } }

        @Override
        public boolean GetIsFixedSize() { synchronized (_root) { return _list.GetIsFixedSize(); } }

        @Override
        public IEnumerator GetEnumerator() { synchronized (_root) { return _list.GetEnumerator(); } }

        @Override
        public <T> T[] ToArray(Class<T> type) { synchronized (_root) { return _list.ToArray(type); } }

        @Override
        public ArrayList Clone() { synchronized (_root) { return new SyncArrayList(_list.Clone()); } }

        @Override
        public boolean Contains(Object value) { synchronized (_root) { return _list.Contains(value); } }

        @Override
        public int LastIndexOf(Object value) { synchronized (_root) { return _list.LastIndexOf(value); } }

        @Override
        public int BinarySearch(Object value) { synchronized (_root) { return _list.BinarySearch(value); } }

        @Override
        public void Insert(int index, Object value) { synchronized (_root) { _list.Insert(index, value); } }

        @Override
        public void Remove(Object value) throws NotSupportedException { synchronized (_root) { _list.Remove(value); } }

        @Override
        public int Add(Object value) throws NotSupportedException { synchronized (_root) { return _list.Add(value); } }

        @Override
        public void AddRange(ICollection c) throws ArgumentNullException { synchronized (_root) { _list.AddRange(c); } }

        @Override
        public int IndexOf(Object value, int startIndex) { synchronized (_root) { return _list.IndexOf(value, startIndex); } }

        @Override
        public void Reverse(int index, int count) throws ArgumentException { synchronized (_root) { _list.Reverse(index, count); } }

        @Override
        public void SetCapacity(int value) throws ArgumentOutOfRangeException { synchronized (_root) { _list.SetCapacity(value); } }

        @Override
        public Object GetItem(int index) throws ArgumentOutOfRangeException { synchronized (_root) { return _list.GetItem(index); } }

        @Override
        public int LastIndexOf(Object value, int startIndex) { synchronized (_root) { return _list.LastIndexOf(value, startIndex); } }

        @Override
        public int BinarySearch(Object value, IComparer comparer) { synchronized (_root) { return _list.BinarySearch(value, comparer); } }

        @Override
        public void RemoveRange(int index, int count) throws ArgumentException { synchronized (_root) { _list.RemoveRange(index, count); } }

        @Override
        public int IndexOf(Object value, int startIndex, int count) { synchronized (_root) { return _list.IndexOf(value, startIndex, count); } }

        @Override
        public int LastIndexOf(Object value, int startIndex, int count) { synchronized (_root) { return _list.LastIndexOf(value, startIndex, count); } }

        @Override
        public IEnumerator GetEnumerator(int index, int count) throws ArgumentException { synchronized (_root) { return _list.GetEnumerator(index, count); } }

        @Override
        public void SetRange(int index, ICollection c) throws ArgumentNullException, ArgumentOutOfRangeException { synchronized (_root) { _list.SetRange(index, c); } }

        @Override
        public void SetItem(int index, Object value) throws ArgumentOutOfRangeException, NotSupportedException { synchronized (_root) { _list.SetItem(index, value); } }

        @Override
        public void InsertRange(int index, ICollection c) throws ArgumentOutOfRangeException, ArgumentNullException { synchronized (_root) { _list.InsertRange(index, c); } }

        @Override
        public int BinarySearch(int index, int count, Object value, IComparer comparer) { synchronized (_root) { return _list.BinarySearch(index, count, value, comparer); } }

        @Override
        public void CopyTo(Object array, int arrayIndex) throws ArgumentNullException, ArgumentOutOfRangeException { synchronized (_root) { _list.CopyTo(array, arrayIndex); } }
    }

    // Implementation of a generic list subrange. An instance of this class
    // is returned by the default implementation of List.GetRange.
    private static final class Range 
            extends ArrayList
    {
        private ArrayList _baseList;
        private final int _baseIndex;
        private int _baseSize;
        private int _baseVersion;

        public Range(ArrayList list, int index, int count)
        {
            _baseList = list;
            _baseIndex = index;
            _baseSize = count;
            _baseVersion = list._version;
            // we also need to update _version field to make Range of Range work
            super._version = list._version;
        }

        private void InternalUpdateRange()
        {
            if (_baseVersion != _baseList._version)
                throw new InvalidOperationException("The underlying array list has been mutated");
        }
    
        private void InternalUpdateVersion()
        {
            _baseVersion++;
            super._version++;
        }
    
        public int Add(@AllowNull Object value)
        {
            InternalUpdateRange();
            _baseList.Insert(_baseIndex + _baseSize, value);
            InternalUpdateVersion();
            return _baseSize++;
        }
    
        public void AddRange(ICollection c)
        {
            ArgumentNullException.ThrowIfNull(c);
    
            InternalUpdateRange();
            int count = c.getCount();
            if (count > 0)
            {
                _baseList.InsertRange(_baseIndex + _baseSize, c);
                InternalUpdateVersion();
                _baseSize += count;
            }
        }

        public int BinarySearch(int index, int count, @AllowNull Object value, @AllowNull IComparer comparer)
        {
            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
            } else if (count < 0) {
                throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
            } else if (_baseSize - index < count) {
                throw new ArgumentException("The specified index and count parameter values do exceed the list's size.");
            } else {
                InternalUpdateRange();

                int i = _baseList.BinarySearch(_baseIndex + index, count, value, comparer);
                if (i >= 0) return i - _baseIndex;
                return i + _baseIndex;
            }
        }
        
        public int GetCapacity() { return _baseList.GetCapacity(); }

        @Override
        public void SetCapacity(int value)
                throws ArgumentOutOfRangeException
        {
            if (value < getCount())
                throw new ArgumentOutOfRangeException("value", "Capacity must be larger than the list's size.");
        }

        public void Clear()
        {
            InternalUpdateRange();
            if (_baseSize != 0)
            {
                _baseList.RemoveRange(_baseIndex, _baseSize);
                InternalUpdateVersion();
                _baseSize = 0;
            }
        }
    
        public ArrayList Clone()
        {
            InternalUpdateRange();
            Range arrayList = new Range(_baseList, _baseIndex, _baseSize);
            arrayList._baseList = _baseList.Clone();
            return arrayList;
        }
    
        public boolean Contains(@AllowNull Object item)
        {
            InternalUpdateRange();
            if (item == null)
            {
                for (int i = 0; i < _baseSize; i++)
                    if (_baseList.GetItem(_baseIndex + i) == null)
                        return true;
            }
            else
            {
                for (int i = 0; i < _baseSize; i++)
                    if (_baseList.GetItem(_baseIndex + i) instanceof Object o && o.equals(item))
                        return true;
            }
            return false;
        }
    
        public int getCount() { InternalUpdateRange(); return _baseSize; }
    
        public boolean GetIsReadOnly() { return _baseList.GetIsReadOnly(); }

        @Override
        public boolean GetIsFixedSize() { return _baseList.GetIsFixedSize(); }

        @Override
        public boolean getIsSynchronized() { return _baseList.getIsSynchronized(); }

        public IEnumerator GetEnumerator() { return GetEnumerator(0, _baseSize); }
    
        public IEnumerator GetEnumerator(int index, int count)
        {
            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
            } else if (count < 0) {
                throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
            } else if (_baseSize - index < count) {
                throw new ArgumentException("The specified index and count parameter values do exceed the list's size.");
            } else {
                InternalUpdateRange();
                return _baseList.GetEnumerator(_baseIndex + index, count);
            }
        }
    
        public ArrayList GetRange(int index, int count)
        {
            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
            } else if (count < 0) {
                throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
            } else if (_baseSize - index < count) {
                throw new ArgumentException("The specified index and count parameter values do exceed the list's size.");
            } else {
                InternalUpdateRange();
                return new Range(this, index, count);
            }
        }
    
        public Object getSyncRoot() { return _baseList.getSyncRoot(); }

        public int IndexOf(@AllowNull Object value)
        {
            InternalUpdateRange();
            int i = _baseList.IndexOf(value, _baseIndex, _baseSize);
            if (i >= 0) return i - _baseIndex;
            return -1;
        }
    
        public int IndexOf(@AllowNull Object value, int startIndex)
        {
            if (startIndex < 0) {
                throw new ArgumentOutOfRangeException("startIndex", "Index must not be a negative number.");
            } else if (startIndex > _baseSize)
                throw new ArgumentOutOfRangeException("startIndex", "Index must not be larger than the range size.");

            InternalUpdateRange();
            int i = _baseList.IndexOf(value, _baseIndex + startIndex, _baseSize - startIndex);
            if (i >= 0) return i - _baseIndex;
            return -1;
        }
    
        public int IndexOf(@AllowNull Object value, int startIndex, int count)
        {
            if (startIndex < 0 || startIndex > _baseSize)
                throw new ArgumentOutOfRangeException("startIndex", startIndex, "Index must not be a negative number.");
    
            if (count < 0 || (startIndex > _baseSize - count))
                throw new ArgumentOutOfRangeException("count", count, "Count must not be larger than the range size.");

            InternalUpdateRange();
            int i = _baseList.IndexOf(value, _baseIndex + startIndex, count);
            if (i >= 0) return i - _baseIndex;
            return -1;
        }

        public void Insert(int index, @AllowNull Object value)
        {
            if (index < 0 || index > _baseSize) throw new ArgumentOutOfRangeException("index", "The insertion index must be between 0 and less or equal than the list's size.");
    
            InternalUpdateRange();
            _baseList.Insert(_baseIndex + index, value);
            InternalUpdateVersion();
            _baseSize++;
        }
    
        public void InsertRange(int index, ICollection c)
        {
            if (index < 0 || index > _baseSize) throw new ArgumentOutOfRangeException("index", "The insertion index must be between 0 and less or equal than the list's size.");
            ArgumentNullException.ThrowIfNull(c, "c");
    
            InternalUpdateRange();
            int count = c.getCount();
            if (count > 0)
            {
                _baseList.InsertRange(_baseIndex + index, c);
                _baseSize += count;
                InternalUpdateVersion();
            }
        }
    
        public int LastIndexOf(@AllowNull Object value)
        {
            InternalUpdateRange();
            int i = _baseList.LastIndexOf(value, _baseIndex + _baseSize - 1, _baseSize);
            if (i >= 0) return i - _baseIndex;
            return -1;
        }
    
        public int LastIndexOf(@AllowNull Object value, int startIndex)
        {
            return LastIndexOf(value, startIndex, startIndex + 1);
        }
    
        public int LastIndexOf(@AllowNull Object value, int startIndex, int count)
        {
            InternalUpdateRange();
            if (_baseSize == 0)
                return -1;

            if (startIndex < 0) {
                throw new ArgumentOutOfRangeException("startIndex", "Index must not be a negative number.");
            } else if (startIndex >= _baseSize)
                throw new ArgumentOutOfRangeException("startIndex", "Index must not be larger than or equal to the range size.");

            int i = _baseList.LastIndexOf(value, _baseIndex + startIndex, count);
            if (i >= 0) return i - _baseIndex;
            return -1;
        }
    
        // Don't need to override Remove
    
        public void RemoveAt(int index)
        {
            if (index < 0 || index >= _baseSize) throw new ArgumentOutOfRangeException("index", index, "Index must not be larger than or equal to the range size.");
    
            InternalUpdateRange();
            _baseList.RemoveAt(_baseIndex + index);
            InternalUpdateVersion();
            _baseSize--;
        }
    
        public void RemoveRange(int index, int count)
        {
            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
            } else if (count < 0) {
                throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
            } else if (_baseSize - index < count) {
                throw new ArgumentException("The specified index and count parameter values do exceed the list's size.");
            } else {
                InternalUpdateRange();
                // No need to call _bastList.RemoveRange if count is 0.
                // In addition, _baseList won't change the version number if count is 0.
                if (count > 0)
                {
                    _baseList.RemoveRange(_baseIndex + index, count);
                    InternalUpdateVersion();
                    _baseSize -= count;
                }
            }
        }
    
        public void Reverse(int index, int count)
        {
            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
            } else if (count < 0) {
                throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
            } else if (_baseSize - index < count) {
                throw new ArgumentException("The specified index and count parameter values do exceed the list's size.");
            } else {
                InternalUpdateRange();
                _baseList.Reverse(_baseIndex + index, count);
                InternalUpdateVersion();
            }
        }
    
        public void SetRange(int index, ICollection c)
        {
            InternalUpdateRange();
            if (index < 0 || index >= _baseSize)
                throw new ArgumentOutOfRangeException("index", index, "Index must not be larger than or equal to the range size.");
            _baseList.SetRange(_baseIndex + index, c);
            if (c.getCount() > 0)
            {
                InternalUpdateVersion();
            }
        }

        @Override
        public Object GetItem(int index)
                throws ArgumentOutOfRangeException
        {
            if (index < 0 || index >= _baseSize)
                throw new ArgumentOutOfRangeException("index", index, "Index must not be larger than or equal to the range size.");
            InternalUpdateRange();
            return _baseList.GetItem(_baseIndex + index);
        }

        @Override
        public void SetItem(int index, Object value)
                throws ArgumentOutOfRangeException, NotSupportedException
        {
            InternalUpdateRange();
            if (index < 0 || index >= _baseSize)
                throw new ArgumentOutOfRangeException("index", index, "Index must not be larger than or equal to the range size.");
            _baseList.SetItem(_baseIndex + index, value);
            InternalUpdateVersion();
        }

        public void TrimToSize()
        {
            throw new NotSupportedException("Trim to size not supported");
        }
    }

    private static final class SyncIList
            implements IList
    {
        private final IList _list;
        private final Object _root;

        public SyncIList(IList list)
        {
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
        public int getCount() { synchronized (_root) { return _list.getCount(); } }

        @Override
        public void RemoveAt(int index) { synchronized (_root) { _list.RemoveAt(index); } }

        @Override
        public IEnumerator GetEnumerator() { synchronized (_root) { return _list.GetEnumerator(); } }

        @Override
        public void Remove(@AllowNull Object value) { synchronized (_root) { _list.Remove(value); } }

        @Override
        public void CopyTo(Object array, int index) { synchronized (_root) { _list.CopyTo(array, index); } }

        @Override
        public int IndexOf(@AllowNull Object value) { synchronized (_root) { return _list.IndexOf(value); } }

        @Override
        public boolean Contains(@AllowNull Object item) { synchronized (_root) { return _list.Contains(item); } }

        @Override
        public void Insert(int index, @AllowNull Object value) { synchronized (_root) { _list.Insert(index, value); } }

        @Override
        public int Add(Object value) throws NotSupportedException { synchronized (_root) { return _list.Add(value); } }

        @Override
        public Object GetItem(int index) throws ArgumentOutOfRangeException { synchronized (_root) { return _list.GetItem(index); } }

        @Override
        public void SetItem(int index, Object value) throws ArgumentOutOfRangeException, NotSupportedException { synchronized (_root) { _list.SetItem(index, value); } }
    }

    private static final class FixedSizeList
            implements IList
    {
        private final IList _list;

        public FixedSizeList(IList l) { _list = l; }

        @Override
        public boolean GetIsFixedSize() { return true; }

        @Override
        public int getCount() { return _list.getCount(); }

        @Override
        public Object getSyncRoot() { return _list.getSyncRoot(); }

        @Override
        public boolean GetIsReadOnly() { return _list.GetIsReadOnly(); }

        @Override
        public IEnumerator GetEnumerator() { return _list.GetEnumerator(); }

        @Override
        public boolean getIsSynchronized() { return _list.getIsSynchronized(); }

        @Override
        public void CopyTo(Object array, int index) { _list.CopyTo(array, index); }

        @Override
        public int IndexOf(@AllowNull Object value) { return _list.IndexOf(value); }

        @Override
        public boolean Contains(@AllowNull Object obj) { return _list.Contains(obj); }

        @Override
        public void Clear() { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public void RemoveAt(int index) { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public int Add(@AllowNull Object obj) { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public Object GetItem(int index) throws ArgumentOutOfRangeException { return _list.GetItem(index); }

        @Override
        public void Remove(@AllowNull Object value) { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public void Insert(int index, @AllowNull Object obj) { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public void SetItem(int index, Object value) throws ArgumentOutOfRangeException, NotSupportedException { _list.SetItem(index, value); }
    }

    private static final class FixedSizeArrayList
            extends ArrayList
    {
        private final ArrayList _list;

        public FixedSizeArrayList(ArrayList l)
        {
            _list = l;
            super._version = _list._version;
        }

        @Override
        public boolean GetIsFixedSize() { return true; }

        @Override
        public int getCount() { return _list.getCount(); }

        @Override
        public Object[] ToArray() { return _list.ToArray(); }

        @Override
        public int GetCapacity() { return _list.GetCapacity(); }

        @Override
        public Object getSyncRoot() { return _list.getSyncRoot(); }

        @Override
        public boolean GetIsReadOnly() { return _list.GetIsReadOnly(); }

        @Override
        public int IndexOf(Object value) { return _list.IndexOf(value); }

        @Override
        public IEnumerator GetEnumerator() { return _list.GetEnumerator(); }

        @Override
        public <T> T[] ToArray(Class<T> type) { return _list.ToArray(type); }

        @Override
        public boolean Contains(Object value) { return _list.Contains(value); }

        @Override
        public boolean getIsSynchronized() { return _list.getIsSynchronized(); }

        @Override
        public int LastIndexOf(Object value) { return _list.LastIndexOf(value); }

        @Override
        public ArrayList Clone() { return new FixedSizeArrayList(_list.Clone()); }

        @Override
        public int BinarySearch(Object value) { return _list.BinarySearch(value); }

        @Override
        public void Clear() { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public void TrimToSize() { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public void RemoveAt(int index) { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public int IndexOf(Object value, int startIndex) { return _list.IndexOf(value, startIndex); }

        @Override
        public Object GetItem(int index) throws ArgumentOutOfRangeException { return _list.GetItem(index); }

        @Override
        public int LastIndexOf(Object value, int startIndex) { return _list.LastIndexOf(value, startIndex); }

        @Override
        public void Insert(int index, Object value) { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public int BinarySearch(Object value, IComparer comparer) { return _list.BinarySearch(value, comparer); }

        @Override
        public int IndexOf(Object value, int startIndex, int count) { return _list.IndexOf(value, startIndex, count); }

        @Override
        public int LastIndexOf(Object value, int startIndex, int count) { return _list.LastIndexOf(value, startIndex, count); }

        @Override
        public int Add(Object value) throws NotSupportedException { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public void Remove(Object value) throws NotSupportedException { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public IEnumerator GetEnumerator(int index, int count) throws ArgumentException { return _list.GetEnumerator(index, count); }

        @Override
        public void AddRange(ICollection c) throws ArgumentNullException { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public void SetCapacity(int value) throws ArgumentOutOfRangeException { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public void RemoveRange(int index, int count) throws ArgumentException { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public void SetItem(int index, Object value) throws ArgumentOutOfRangeException, NotSupportedException { _list.SetItem(index, value); }

        @Override
        public int BinarySearch(int index, int count, Object value, IComparer comparer) { return _list.BinarySearch(index, count, value, comparer); }

        @Override
        public void CopyTo(Object array, int arrayIndex) throws ArgumentNullException, ArgumentOutOfRangeException { _list.CopyTo(array, arrayIndex); }

        @Override
        public void InsertRange(int index, ICollection c) throws ArgumentOutOfRangeException, ArgumentNullException { throw new NotSupportedException("Fixed-size collection"); }

        @Override
        public void Reverse(int index, int count)
                throws ArgumentException
        {
            _list.Reverse(index, count);
            super._version = _list._version;
        }

        @Override
        public void SetRange(int index, ICollection c)
                throws ArgumentNullException, ArgumentOutOfRangeException
        {
            _list.SetRange(index, c);
            super._version = _list._version;
        }

        @Override
        public ArrayList GetRange(int index, int count)
        {
            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
            } else if (count < 0) {
                throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
            } else if (getCount() - index < count) {
                throw new ArgumentException("The selected index and count value exceed the list's bounds.");
            } else {
                return new Range(this, index, count);
            }
        }
    }

    private static final class ReadOnlyList
        implements IList
    {
        private final IList _list;

        public ReadOnlyList(IList list) { _list = list; }

        @Override
        public boolean GetIsReadOnly() { return true; }

        @Override
        public boolean GetIsFixedSize() { return true; }

        @Override
        public int getCount() { return _list.getCount(); }

        @Override
        public Object getSyncRoot() { return _list.getSyncRoot(); }

        @Override
        public int IndexOf(Object value) { return _list.IndexOf(value); }

        @Override
        public IEnumerator GetEnumerator() { return _list.GetEnumerator(); }

        @Override
        public boolean Contains(Object value) { return _list.Contains(value); }

        @Override
        public boolean getIsSynchronized() { return _list.getIsSynchronized(); }

        @Override
        public Object GetItem(int index) throws ArgumentOutOfRangeException { return _list.GetItem(index); }

        @Override
        public void CopyTo(Object array, int index) throws ArgumentException { _list.CopyTo(array, index); }

        @Override
        public void Clear() throws NotSupportedException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public int Add(Object value) throws NotSupportedException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public void Remove(Object value) throws NotSupportedException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public void RemoveAt(int index) throws ArgumentOutOfRangeException, NotSupportedException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public void SetItem(int index, Object value) throws ArgumentOutOfRangeException, NotSupportedException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public void Insert(int index, Object value) throws ArgumentOutOfRangeException, NotSupportedException, NullReferenceException { throw new NotSupportedException("Read-only collection"); }
    }

    private static final class ReadOnlyArrayList
        extends ArrayList
    {
        private final ArrayList _list;

        public ReadOnlyArrayList(ArrayList list) { _list = list; }

        @Override
        public boolean GetIsReadOnly() { return true; }

        @Override
        public boolean GetIsFixedSize() { return true; }

        @Override
        public int getCount() { return _list.getCount(); }

        @Override
        public Object[] ToArray() { return _list.ToArray(); }

        @Override
        public int GetCapacity() { return _list.GetCapacity(); }

        @Override
        public Object getSyncRoot() { return _list.getSyncRoot(); }

        @Override
        public int IndexOf(Object value) { return _list.IndexOf(value); }

        @Override
        public IEnumerator GetEnumerator() { return _list.GetEnumerator(); }

        @Override
        public <T> T[] ToArray(Class<T> type) { return _list.ToArray(type); }

        @Override
        public boolean Contains(Object value) { return _list.Contains(value); }

        @Override
        public boolean getIsSynchronized() { return _list.getIsSynchronized(); }

        @Override
        public int LastIndexOf(Object value) { return _list.LastIndexOf(value); }

        @Override
        public ArrayList Clone() { return new ReadOnlyArrayList(_list.Clone()); }

        @Override
        public int BinarySearch(Object value) { return _list.BinarySearch(value); }

        @Override
        public void Clear() { throw new NotSupportedException("Read-only collection"); }

        @Override
        public void Reverse() { throw new NotSupportedException("Read-only collection"); }

        @Override
        public void TrimToSize() { throw new NotSupportedException("Read-only collection"); }

        @Override
        public ArrayList GetRange(int index, int count) { return _list.GetRange(index, count); }

        @Override
        public void RemoveAt(int index) { throw new NotSupportedException("Read-only collection"); }

        @Override
        public int IndexOf(Object value, int startIndex) { return _list.IndexOf(value, startIndex); }

        @Override
        public Object GetItem(int index) throws ArgumentOutOfRangeException { return _list.GetItem(index); }

        @Override
        public int LastIndexOf(Object value, int startIndex) { return _list.LastIndexOf(value, startIndex); }

        @Override
        public void Insert(int index, Object value) { throw new NotSupportedException("Read-only collection"); }

        @Override
        public int BinarySearch(Object value, IComparer comparer) { return _list.BinarySearch(value, comparer); }

        @Override
        public int IndexOf(Object value, int startIndex, int count) { return _list.IndexOf(value, startIndex, count); }

        @Override
        public int Add(Object value) throws NotSupportedException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public int LastIndexOf(Object value, int startIndex, int count) { return _list.LastIndexOf(value, startIndex, count); }

        @Override
        public void Remove(Object value) throws NotSupportedException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public void AddRange(ICollection c) throws ArgumentNullException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public IEnumerator GetEnumerator(int index, int count) throws ArgumentException { return _list.GetEnumerator(index, count); }

        @Override
        public void Reverse(int index, int count) throws ArgumentException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public void SetCapacity(int value) throws ArgumentOutOfRangeException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public void RemoveRange(int index, int count) throws ArgumentException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public int BinarySearch(int index, int count, Object value, IComparer comparer) { return _list.BinarySearch(index, count, value, comparer); }

        @Override
        public void CopyTo(Object array, int arrayIndex) throws ArgumentNullException, ArgumentOutOfRangeException { _list.CopyTo(array, arrayIndex); }

        @Override
        public void SetItem(int index, Object value) throws ArgumentOutOfRangeException, NotSupportedException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public void SetRange(int index, ICollection c) throws ArgumentNullException, ArgumentOutOfRangeException { throw new NotSupportedException("Read-only collection"); }

        @Override
        public void InsertRange(int index, ICollection c) throws ArgumentOutOfRangeException, ArgumentNullException { throw new NotSupportedException("Read-only collection"); }
    }

    @Override
    public int getCount() { return _size; }

    @Override
    public Object getSyncRoot() { return this; }

    @Override
    public boolean GetIsReadOnly() { return false; }

    @Override
    public boolean GetIsFixedSize() { return false; }

    public int GetCapacity() { return _items.length; }

    @Override
    public boolean getIsSynchronized() { return false; }

    @Override
    public boolean Contains(@AllowNull Object value) { return IndexOf(value) > -1; }

    // Returns the index of the first occurrence of a given value in a range of
    // this list. The list is searched forwards from beginning to end.
    // The elements of the list are compared to the given value using the
    // Object.Equals method.
    //
    // This method uses the Array.IndexOf method to perform the
    // search.
    //
    public int IndexOf(@AllowNull Object value) { return Array.IndexOf(_items, value, 0, _size); }

    public int IndexOf(@AllowNull Object value, int startIndex)
    {
        if (startIndex > _size) {
            throw new ArgumentOutOfRangeException("startIndex", "Index must be less than the list's size and larger or equal to zero.");
        } else {
            return Array.IndexOf(_items, value, startIndex, _size - startIndex);
        }
    }

    // Returns the index of the first occurrence of a given value in a range of
    // this list. The list is searched forwards, starting at index
    // startIndex and up to count number of elements. The
    // elements of the list are compared to the given value using the
    // Object.Equals method.
    //
    // This method uses the Array.IndexOf method to perform the
    // search.
    //
    public int IndexOf(@AllowNull Object value, int startIndex, int count)
    {
        if (startIndex > _size) {
            throw new ArgumentOutOfRangeException("startIndex", "Index must be less than the list's size and larger or equal to zero.");
        } else if (count < 0 || startIndex > _size - count) {
            throw new ArgumentOutOfRangeException("count", "Count must be greater than or equal to zero and less than the list's size.");
        } else {
            return Array.IndexOf(_items, value, startIndex, count);
        }
    }

    // Returns the index of the last occurrence of a given value in a range of
    // this list. The list is searched backwards, starting at the end
    // and ending at the first element in the list. The elements of the list
    // are compared to the given value using the Object.Equals method.
    //
    // This method uses the Array.LastIndexOf method to perform the
    // search.
    //
    public int LastIndexOf(@AllowNull Object value) { return LastIndexOf(value, _size - 1, _size); }

    // Returns the index of the last occurrence of a given value in a range of
    // this list. The list is searched backwards, starting at index
    // startIndex and up to count elements. The elements of
    // the list are compared to the given value using the Object.Equals
    // method.
    //
    // This method uses the Array.LastIndexOf method to perform the
    // search.
    //
    public int LastIndexOf(@AllowNull Object value, int startIndex, int count)
    {
        if (_size != 0)
        {
            if (startIndex < 0) {
                throw new ArgumentOutOfRangeException("startIndex", "Index must be less than or equal to zero.");
            } else if (count < 0) {
                throw new ArgumentOutOfRangeException("count", "Count must be greater than or equal to zero.");
            }
        }

        if (_size == 0)  // Special case for an empty list
            return -1;

        if (startIndex >= _size || count > startIndex + 1)
            throw new ArgumentOutOfRangeException(startIndex >= _size ? "startIndex" : "count", "Specified count and index values are exceeding the list's bounds.");

        return Array.LastIndexOf(_items, value, startIndex, count);
    }

    // Returns the index of the last occurrence of a given value in a range of
    // this list. The list is searched backwards, starting at index
    // startIndex and ending at the first element in the list. The
    // elements of the list are compared to the given value using the
    // Object.Equals method.
    //
    // This method uses the Array.LastIndexOf method to perform the
    // search.
    //
    public int LastIndexOf(@AllowNull Object value, int startIndex)
    {
        if (startIndex >= _size) {
            throw new ArgumentOutOfRangeException("startIndex", "Index must be less than the list's size and larger or equal to zero.");
        } else {
            return LastIndexOf(value, startIndex, startIndex + 1);
        }
    }

    @Override
    public int Add(Object value)
            throws NotSupportedException
    {
        if (_size == _items.length) EnsureCapacity(_size + 1);
        _items[_size] = value;
        _version++;
        return _size++;
    }

    // Inserts an element into this list at a given index. The size of the list
    // is increased by one. If required, the capacity of the list is doubled
    // before inserting the new element.
    //
    public void Insert(int index, @AllowNull Object value)
    {
        // Note that insertions at the end are legal.
        if (index < 0 || index > _size) throw new ArgumentOutOfRangeException("index", "The insertion index must be between 0 and less or equal than the list's size.");

        if (_size == _items.length) EnsureCapacity(_size + 1);
        if (index < _size)
        {
            Array.Copy(_items, index, _items, index + 1, _size - index);
        }
        _items[index] = value;
        _size++;
        _version++;
    }

    @Override
    public void Remove(@AllowNull Object value)
            throws NotSupportedException
    {
        int index = IndexOf(value);
        if (index >= 0) { RemoveAt(index); }
    }

    public void SetCapacity(int value)
            throws ArgumentOutOfRangeException
    {
        if (value < _size) {
            throw new ArgumentOutOfRangeException("value", "New capacity must not be less than the list's size.");
        } else {
            // We don't want to update the version number when we change the capacity.
            // Some existing applications have dependency on this.
            if (value != _items.length)
            {
                if (value > 0)
                {
                    Object[] newItems = new Object[value];
                    if (_size > 0)
                    {
                        Array.Copy(_items, newItems, _size);
                    }
                    _items = newItems;
                }
                else
                {
                    _items = new Object[_defaultCapacity];
                }
            }
        }
    }

    @Override
    public Object GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0 || index >= _size) {
            throw new ArgumentOutOfRangeException("index", "Index must be less than the list's size, and must not be a negative value.");
        } else {
            return _items[index];
        }
    }

    @Override
    public void SetItem(int index, Object value)
            throws ArgumentOutOfRangeException, NotSupportedException
    {
        if (index < 0 || index >= _size) {
            throw new ArgumentOutOfRangeException("index", "Index must be less than the list's size, and must not be a negative value.");
        } else {
            _items[index] = value;
        }
    }

    // Clears the contents of ArrayList.
    public void Clear()
    {
        if (_size > 0)
        {
            Array.Clear(_items, 0, _size); // Don't need to doc this, but we clear the elements so that the gc can reclaim the references.
            _size = 0;
        }
        _version++;
    }

    // Clones this ArrayList, doing a shallow copy.  (A copy is made of all
    // Object references in the ArrayList, but the Objects pointed to
    // are not cloned).
    public ArrayList Clone()
    {
        ArrayList la = new ArrayList(_size);
        la._size = _size;
        la._version = _version;
        Array.Copy(_items, la._items, _size);
        return la;
    }

    @Override
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public void CopyTo(Object array, int arrayIndex)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (arrayIndex < 0) {
            throw new ArgumentOutOfRangeException("arrayIndex", "Index must not be a negative number.");
        } else {
            try {
                System.arraycopy(_items, 0, array, arrayIndex, _size);
            } catch (IndexOutOfBoundsException e) {
                throw new ArgumentOutOfRangeException("arrayIndex", "The number of elements in the source ICollection is greater than the available space from index to the end of the destination array.");
            }
        }
    }

    // Adds the elements of the given collection to the end of this list. If
    // required, the capacity of the list is increased to twice the previous
    // capacity or the new size, whichever is larger.
    //
    public void AddRange(ICollection c) throws ArgumentNullException { InsertRange(_size, c); }

    // Searches a section of the list for a given element using a binary search
    // algorithm. Elements of the list are compared to the search value using
    // the given IComparer interface. If comparer is null, elements of
    // the list are compared to the search value using the IComparable
    // interface, which in that case must be implemented by all elements of the
    // list and the given search value. This method assumes that the given
    // section of the list is already sorted; if this is not the case, the
    // result will be incorrect.
    //
    // The method returns the index of the given value in the list. If the
    // list does not contain the given value, the method returns a negative
    // integer. The bitwise complement operator (~) can be applied to a
    // negative result to produce the index of the first element (if any) that
    // is larger than the given search value. This is also the index at which
    // the search value should be inserted into the list in order for the list
    // to remain sorted.
    //
    // The method uses the Array.BinarySearch method to perform the
    // search.
    //
    public int BinarySearch(int index, int count, @AllowNull Object value, @AllowNull IComparer comparer)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
        } else if (_size - index < count) {
            throw new ArgumentException("The specified index and count parameter values do exceed the list's size.");
        } else {
            return Array.BinarySearch(_items, index, count, value, comparer);
        }
    }

    public int BinarySearch(@AllowNull Object value) { return BinarySearch(value, null); }

    public int BinarySearch(@AllowNull Object value, @AllowNull IComparer comparer) { return BinarySearch(0, _size, value, comparer); }


    // Inserts the elements of the given collection at a given index. If
    // required, the capacity of the list is increased to twice the previous
    // capacity or the new size, whichever is larger.  Ranges may be added
    // to the end of the list by setting index to the ArrayList's size.
    //
    public void InsertRange(int index, ICollection c)
        throws ArgumentOutOfRangeException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(c, "c");

        if (index < 0 || index > _size)
            throw new ArgumentOutOfRangeException("index", "The insertion index must be between 0 and less or equal than the list's size.");

        int count = c.getCount();
        if (count > 0)
        {
            EnsureCapacity(_size + count);
            // shift existing items
            if (index < _size)
            {
                Array.Copy(_items, index, _items, index + count, _size - index);
            }

            Object[] itemsToInsert = new Object[count];
            c.CopyTo(itemsToInsert, 0);
            Array.Copy(itemsToInsert, 0, _items, index, count); //itemsToInsert.CopyTo(_items, index);
            _size += count;
            _version++;
        }
    }

    // Removes the element at the given index. The size of the list is
    // decreased by one.
    //
    public void RemoveAt(int index)
    {
        if (index < 0 || index >= _size)
            throw new ArgumentOutOfRangeException("index", "The insertion index must be between 0 and less than the list's size.");

        _size--;
        if (index < _size)
        {
            Array.Copy(_items, index + 1, _items, index, _size - index);
        }
        _items[_size] = null;
        _version++;
    }

    // Removes a range of elements from this list.
    //
    public void RemoveRange(int index, int count)
            throws ArgumentOutOfRangeException, ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
        } else if (_size - index < count) {
            throw new ArgumentException("The selected index and count value exceed the list's bounds.");
        } else if (count > 0)
        {
            int i = _size;
            _size -= count;
            if (index < _size)
            {
                Array.Copy(_items, index + count, _items, index, _size - index);
            }
            while (i > _size) _items[--i] = null;
            _version++;
        }
    }

    public void Reverse() { Reverse(0, getCount()); }

    // Reverses the elements in a range of this list. Following a call to this
    // method, an element in the range given by index and count
    // which was previously located at index i will now be located at
    // index: index + (index + count - i - 1).
    //
    // This method uses the Array.Reverse method to reverse the
    // elements.
    //
    public void Reverse(int index, int count)
        throws ArgumentOutOfRangeException, ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
        } else if (_size - index < count) {
            throw new ArgumentException("The selected index and count value exceed the list's bounds.");
        } else {
            Array.Reverse(_items, index, count);
            _version++;
        }
    }

    // Sets the elements starting at the given index to the elements of the
    // given collection.
    //
    public void SetRange(int index, ICollection c)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(c, "c");

        int count = c.getCount();
        if (index < 0 || index > _size - count) throw new ArgumentOutOfRangeException("index", "The index must be between 0 and less or equal than the list's size.");

        if (count > 0)
        {
            c.CopyTo(_items, index);
            _version++;
        }
    }

    @NotNull
    public ArrayList GetRange(int index, int count)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
        } else if (_size - index < count) {
            throw new ArgumentException("The selected index and count value exceed the list's bounds.");
        } else {
            return new Range(this, index, count);
        }
    }

    // ToArray returns a new array of a particular type containing the contents
    // of the ArrayList.  This requires copying the ArrayList and potentially
    // downcasting all elements.  This copy may fail and is an O(n) operation.
    // Internally, this implementation calls Array.Copy.
    //
    @SuppressWarnings("unchecked")
    @RequiresDynamicCode(Message = "The code for an array of the specified type might not be available.")
    public <T> T[] ToArray(Class<T> type)
    {
        ArgumentNullException.ThrowIfNull(type, "type");

        T[] array = (T[])Array.CreateInstance(type, _size);
        Array.Copy(_items, array, _size);
        return array;
    }

    // ToArray returns a new Object array containing the contents of the ArrayList.
    // This requires copying the ArrayList, which is an O(n) operation.
    @NotNull
    public Object[] ToArray()
    {
        if (_size == 0) {
            return new Object[0];
        } else {
            Object[] array = new Object[_size];
            Array.Copy(_items, array, _size);
            return array;
        }
    }

    public void TrimToSize() { SetCapacity(_size); }

    @NotNull
    @Override
    public IEnumerator GetEnumerator() { return new ArrayListEnumeratorSimple(this); }

    @NotNull
    public IEnumerator GetEnumerator(int index, int count)
            throws ArgumentOutOfRangeException, ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index must not be a negative number.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count must not be a negative number.");
        } else if (_size - index < count) {
            throw new ArgumentException("The specified index and count parameter values do exceed the list's size.");
        } else {
            return new ArrayListEnumerator(this, index, count);
        }
    }
}
