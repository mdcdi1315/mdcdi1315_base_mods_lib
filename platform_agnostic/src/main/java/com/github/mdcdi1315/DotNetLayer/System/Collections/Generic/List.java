package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.google.common.primitives.UnsignedInteger;

import java.lang.reflect.Type;

public class List<T>
    implements IList<T> , IReadOnlyList<T>
{

    public final class Enumerator
            implements IEnumerator<T>
    {
        private final List<T> _list;
        private int _index;
        private final int _version;
        @MaybeNull
        private T _current;

        private Enumerator(List<T> list)
        {
            _list = list;
            _index = 0;
            _version = list._version;
            _current = null;
        }

        public void Dispose()
        {
            _current = null;
        }

        public boolean MoveNext()
        {
            List<T> localList = _list;

            if (_version == localList._version && (_index < localList._size))
            {
                _current = localList._items[_index];
                _index++;
                return true;
            }
            return MoveNextRare();
        }

        private boolean MoveNextRare()
        {
            if (_version != _list._version)
            {
                throw new InvalidOperationException("Enumeration failed.");
                //ThrowHelper.ThrowInvalidOperationException_InvalidOperation_EnumFailedVersion();
            }

            _index = _list._size + 1;
            _current = null;
            return false;
        }

        public T getCurrent()
        {
            return _current;
        }

        /*
        object? IEnumerator.Current
        {
            get
            {
                if (_index == 0 || _index == _list._size + 1)
                {
                    ThrowHelper.ThrowInvalidOperationException_InvalidOperation_EnumOpCantHappen();
                }
                return Current;
            }
        }*/

        public void Reset()
        {
            if (_version != _list._version)
            {
                throw new InvalidOperationException("Enumeration failed.");
                //ThrowHelper.ThrowInvalidOperationException_InvalidOperation_EnumFailedVersion();
            }

            _index = 0;
            _current = null;
        }
    }

    private final int DefaultCapacity = 4;

    @SuppressWarnings("unchecked")
    private @NotNull T[] CreateArrayOfSize(int len)
    {
        try {
            Type gentype = getClass().getTypeParameters()[0].getBounds()[0];
            return (T[]) Array.CreateInstance(Class.forName(gentype.getTypeName()), len);
        } catch (ClassNotFoundException ignored) {}
        return null;
    }

    T[] _items; // Do not rename (binary serialization)
    int _size; // Do not rename (binary serialization)
    int _version; // Do not rename (binary serialization)

    public List()
    {
        _items = CreateArrayOfSize(DefaultCapacity);
    }

    public List(int capacity)
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "capacity must not be negative!!");
        }

        if (capacity == 0)
            _items = CreateArrayOfSize(0);
        else
            _items = CreateArrayOfSize(capacity);
    }

    // Constructs a List, copying the contents of the given collection. The
    // size and capacity of the new list will both be equal to the size of the
    // given collection.
    //
    public List(IEnumerable<T> collection)
    {
        ArgumentNullException.ThrowIfNull(collection , "collection");

        if (collection instanceof ICollection<T> c)
        {
            int count = c.getCount();
            if (count == 0)
            {
                _items = CreateArrayOfSize(0);
            }
            else
            {
                _items = CreateArrayOfSize(count);
                c.CopyTo(_items, 0);
                _size = count;
            }
        } else {
            _items = CreateArrayOfSize(0);
            IEnumerator<T> en = collection.GetEnumerator();
            try
            {
                while (en.MoveNext())
                {
                    Add(en.getCurrent());
                }
            } finally {
                en.Dispose();
            }
        }
    }

    public int getCapacity()
    {
        return _items.length;
    }

    public void setCapacity(int value)
    {
        if (value < _size)
        {
            throw new ArgumentOutOfRangeException("value" , "Small capacity for the given contents of the List object");
            //ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.value, ExceptionResource.ArgumentOutOfRange_SmallCapacity);
        }

        if (value != _items.length)
        {
            if (value > 0)
            {
                if (_size > 0) {
                    T[] items = CreateArrayOfSize(value);
                    Array.Copy(_items , items , _size);
                    _items = items;
                } else {
                    _items = CreateArrayOfSize(value);
                }
            } else {
                _items = CreateArrayOfSize(DefaultCapacity);
            }
        }
    }

    /**
     * Ensures that the capacity of this list is at least the specified {@literal capacity}.
     * If the current capacity of the list is less than specified {@literal capacity},
     * the capacity is increased by continuously twice current capacity until it is at least the specified {@literal capacity}.
     * @param capacity The minimum capacity to ensure.
     * @return The new capacity of this list.
     * @exception ArgumentOutOfRangeException {@literal capacity} was a negative integer.
     */
    public int EnsureCapacity(int capacity)
    {
        if (capacity < 0)
        {
            throw new ArgumentOutOfRangeException("capacity" , "A non-negative number is required.");
            //ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.capacity, ExceptionResource.ArgumentOutOfRange_NeedNonNegNum);
        }
        if (_items.length < capacity)
        {
            Grow(capacity);
        }

        return _items.length;
    }

    @Override
    public T getItem(int index) {
        if (index < 0 && index >= _size)
        {
            throw new ArgumentOutOfRangeException("index" , "Index must not be negative and less than the size of the list.");
        }
        return _items[index];
    }

    @Override
    public void setItem(int index, T item) {
        if (index < 0 && index >= _size)
        {
            throw new ArgumentOutOfRangeException("index" , "Index must not be negative and less than the size of the list.");
        }
        _items[index] = item;
        _version++;
    }

    private void AddWithResize(T item)
    {
        // Debug.Assert(_size == _items.length);
        int size = _size;
        Grow(size + 1);
        _size = size + 1;
        _items[size] = item;
    }

    /// <summary>
    /// Increase the capacity of this list to at least the specified {@code capacity}.
    /// </summary>
    /// <param name="capacity">The minimum capacity to ensure.</param>
    void Grow(int capacity)
    {
        //Debug.Assert(_items.Length < capacity);

        int newCapacity = _items.length == 0 ? DefaultCapacity : 2 * capacity;

        // Allow the list to grow to maximum possible capacity (~2G elements) before encountering overflow.
        // Note that this check works even when _items.Length overflowed thanks to the (uint) cast
        try {
            if (UnsignedInteger.valueOf(newCapacity).compareTo(UnsignedInteger.valueOf(Array.MaxLength)) > 0) {
                newCapacity = Array.MaxLength;
            }
        } catch (IllegalArgumentException e) {
            newCapacity = Array.MaxLength;
        }

        // If the computed capacity is still less than specified, set to the original argument.
        // Capacities exceeding Array.MaxLength will be surfaced as OutOfMemoryException by Array.Resize.
        if (newCapacity < capacity) newCapacity = capacity;

        setCapacity(newCapacity);
    }

    // Adds the given object to the end of this list. The size of the list is
    // increased by one. If required, the capacity of the list is doubled
    // before adding the new element.
    //
    //[MethodImpl(MethodImplOptions.AggressiveInlining)]
    @Override
    public void Add(T item)
    {
        _version++;
        T[] array = _items;
        int size = _size;
        if (size < array.length) {
            _size = size + 1;
            array[size] = item;
        } else {
            AddWithResize(item);
        }
    }

    // Adds the elements of the given collection to the end of this list. If
    // required, the capacity of the list is increased to twice the previous
    // capacity or the new size, whichever is larger.
    //
    public void AddRange(IEnumerable<T> collection)
    {
        ArgumentNullException.ThrowIfNull(collection , "collection");

        if (collection instanceof ICollection<T> c)
        {
            int count = c.getCount();
            if (count > 0)
            {
                if (_items.length - _size < count)
                {
                    Grow(_size + count);
                }

                c.CopyTo(_items, _size);
                _size += count;
                _version++;
            }
        } else {
            IEnumerator<T> en = collection.GetEnumerator();
            try {
                while (en.MoveNext())
                {
                    Add(en.getCurrent());
                }
            } finally {
                en.Dispose();
            }
        }
    }

    public T Find(Predicate<T> match)
    {
        ArgumentNullException.ThrowIfNull(match , "match");

        T item;
        for (int i = 0; i < _size; i++)
        {
            if (match.predicate(item = _items[i]))
            {
                return item;
            }
        }
        return null;
    }

    public List<T> FindAll(Predicate<T> match)
    {
        ArgumentNullException.ThrowIfNull(match , "match");

        List<T> list = new List<>();
        T item;
        for (int i = 0; i < _size; i++)
        {
            if (match.predicate(item = _items[i]))
            {
                list.Add(item);
            }
        }
        return list;
    }

    public int FindIndex(Predicate<T> match)
    {
        return FindIndex(0, _size, match);
    }

    public int FindIndex(int startIndex, Predicate<T> match)
    {
        return FindIndex(startIndex, _size - startIndex, match);
    }

    public int FindIndex(int startIndex, int count, Predicate<T> match)
    {
        if (startIndex > _size)
        {
            throw new ArgumentOutOfRangeException("startIndex" , "Start index must be less or equal than the collection length.");
            //ThrowHelper.ThrowStartIndexArgumentOutOfRange_ArgumentOutOfRange_IndexMustBeLessOrEqual();
        }

        if (count < 0 || startIndex > _size - count)
        {
            throw new ArgumentOutOfRangeException("count" , "count must not be negative and be less than the size of the collection.");
            //ThrowHelper.ThrowCountArgumentOutOfRange_ArgumentOutOfRange_Count();
        }

        ArgumentNullException.ThrowIfNull(match , "match");

        int endIndex = startIndex + count;
        for (int i = startIndex; i < endIndex; i++)
        {
            if (match.predicate(_items[i])) return i;
        }
        return -1;
    }

    public T FindLast(Predicate<T> match)
    {
        ArgumentNullException.ThrowIfNull(match , "match");

        T item;
        for (int i = _size - 1; i >= 0; i--)
        {
            if (match.predicate(item = _items[i]))
            {
                return item;
            }
        }
        return null;
    }

    public int FindLastIndex(int startIndex, int count, Predicate<T> match)
    {
        ArgumentNullException.ThrowIfNull(match , "match");

        if (_size == 0)
        {
            // Special case for 0 length List
            if (startIndex != -1)
            {
                throw new ArgumentOutOfRangeException("index" , "Index must not be negative and less than the size of the list.");
               // ThrowHelper.ThrowStartIndexArgumentOutOfRange_ArgumentOutOfRange_IndexMustBeLess();
            }
        }
        else
        {
            // Make sure we're not out of range
            if (startIndex < 0 || startIndex >= _size)
            {
                throw new ArgumentOutOfRangeException("index" , "Index must not be negative and less than the size of the list.");
                //ThrowHelper.ThrowStartIndexArgumentOutOfRange_ArgumentOutOfRange_IndexMustBeLess();
            }
        }

        // 2nd have of this also catches when startIndex == MAXINT, so MAXINT - 0 + 1 == -1, which is < 0.
        if (count < 0 || startIndex - count + 1 < 0)
        {
            throw new ArgumentOutOfRangeException("count" , "count must not be negative and be less than the size of the collection.");
            //ThrowHelper.ThrowCountArgumentOutOfRange_ArgumentOutOfRange_Count();
        }

        int endIndex = startIndex - count;
        for (int i = startIndex; i > endIndex; i--)
        {
            if (match.predicate(_items[i]))
            {
                return i;
            }
        }
        return -1;
    }

    public void ForEach(Action1<T> action)
    {
        ArgumentNullException.ThrowIfNull(action , "action");

        int version = _version;

        for (int i = 0; i < _size; i++)
        {
            if (version != _version)
            {
                break;
            }
            action.action(_items[i]);
        }

        if (version != _version)
        {
            throw new InvalidOperationException("Enumeration failed.");
            //ThrowHelper.ThrowInvalidOperationException_InvalidOperation_EnumFailedVersion();
        }
    }

    // Returns the index of the first occurrence of a given value in a range of
    // this list. The list is searched forwards from beginning to end.
    // The elements of the list are compared to the given value using the
    // Object.Equals method.
    //
    // This method uses the Array.IndexOf method to perform the
    // search.
    //
    @Override
    public int IndexOf(T item)
    {
        return Array.IndexOf(_items , item , 0 , _size);
    }

    public List<T> GetRange(int index, int count)
    {
        if (index < 0)
        {
            throw new ArgumentOutOfRangeException("index" , "Index must not be negative.");
            //ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
        }

        if (count < 0)
        {
            throw new ArgumentOutOfRangeException("count" , "Count must not be negative.");
            //ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.count, ExceptionResource.ArgumentOutOfRange_NeedNonNegNum);
        }

        if (_size - index < count)
        {
            throw new ArgumentException("Invalid offset and length.");
            // ThrowHelper.ThrowArgumentException(ExceptionResource.Argument_InvalidOffLen);
        }

        List<T> list = new List<>(count);
        Array.Copy(_items, index, list._items, 0, count);
        list._size = count;
        return list;
    }

    // Inserts an element into this list at a given index. The size of the list
    // is increased by one. If required, the capacity of the list is doubled
    // before inserting the new element.
    //
    @Override
    public void Insert(int index, T item)
    {
        // Note that insertions at the end are legal.
        if (index < 0 && index > _size)
        {
            throw new ArgumentOutOfRangeException("index" , "Insertion index was invalid.");
            //ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.index, ExceptionResource.ArgumentOutOfRange_ListInsert);
        }
        if (_size == _items.length) Grow(_size + 1);
        if (index < _size)
        {
            Array.Copy(_items, index, _items, index + 1, _size - index);
        }
        _items[index] = item;
        _size++;
        _version++;
    }

    @Override
    public int getCount() {
        return _size;
    }

    @Override
    public boolean getIsReadOnly() {
        return false;
    }

    // Clears the contents of List.
    @Override
    public void Clear()
    {
        _version++;
        int size = _size;
        _size = 0;
        if (size > 0) {
            Array.Clear(_items, 0, size); // Clear the elements so that the gc can reclaim the references.
        }
    }

    @Override
    public boolean Contains(T item) {
        return Array.IndexOf(_items , item , 0 , _size) > -1;
    }

    @Override
    public void CopyTo(T[] array, int arrayIndex) {
        Array.Copy(_items , 0 , array , arrayIndex , _size);
    }

    // Removes the first occurrence of the given element, if found.
    // The size of the list is decreased by one if successful.
    @Override
    public boolean Remove(T item)
    {
        int index = IndexOf(item);
        if (index >= 0)
        {
            RemoveAt(index);
            return true;
        }

        return false;
    }

    @Override
    public Enumerator GetEnumerator() {
        return new Enumerator(this);
    }

    // Sets the capacity of this list to the size of the list. This method can
    // be used to minimize a list's memory overhead once it is known that no
    // new elements will be added to the list. To completely clear a list and
    // release all memory referenced by the list, execute the following
    // statements:
    //
    // list.Clear();
    // list.TrimExcess();
    //
    public void TrimExcess()
    {
        int threshold = (int)(((double)_items.length) * 0.9);
        if (_size < threshold)
        {
            setCapacity(_size);
        }
    }

    public boolean TrueForAll(Predicate<T> match)
    {
        ArgumentNullException.ThrowIfNull(match , "match");

        for (int i = 0; i < _size; i++)
        {
            if (!match.predicate(_items[i]))
            {
                return false;
            }
        }
        return true;
    }

    // ToArray returns an array containing the contents of the List.
    // This requires copying the List, which is an O(n) operation.
    public T[] ToArray()
    {
        if (_size == 0) {
            return CreateArrayOfSize(0);
        }

        T[] array = CreateArrayOfSize(_size);
        Array.Copy(_items, array, _size);
        return array;
    }

    // Reverses the elements in a range of this list. Following a call to this
    // method, an element in the range given by index and count
    // which was previously located at index i will now be located at
    // index: index + (index + count - i - 1).
    //
    public void Reverse(int index, int count)
    {
        if (index < 0)
        {
            throw new ArgumentOutOfRangeException("index" , "Must not be negative.");
            //ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
        }

        if (count < 0)
        {
            throw new ArgumentOutOfRangeException("count" , "Must not be negative.");
            //ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.count, ExceptionResource.ArgumentOutOfRange_NeedNonNegNum);
        }

        if (_size - index < count)
            throw new ArgumentException("The given offset and length were outside the array bounds.");
            //ThrowHelper.ThrowArgumentException(ExceptionResource.Argument_InvalidOffLen);

        if (count > 1) {
            Array.Reverse(_items, index, count);
        }
        _version++;
    }

    // Reverses the elements in this list.
    public void Reverse()
    {
        Reverse(0, getCount());
    }

    // Removes a range of elements from this list.
    public void RemoveRange(int index, int count)
    {
        if (index < 0)
        {
            throw new ArgumentOutOfRangeException("index" , "Must not be negative.");
            //ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
        }

        if (count < 0)
        {
            throw new ArgumentOutOfRangeException("count" , "Must not be negative.");
            //ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.count, ExceptionResource.ArgumentOutOfRange_NeedNonNegNum);
        }

        if (_size - index < count)
            throw new ArgumentException("The given offset and length were outside the array bounds.");
            //ThrowHelper.ThrowArgumentException(ExceptionResource.Argument_InvalidOffLen);

        if (count > 0)
        {
            _size -= count;
            if (index < _size)
            {
                Array.Copy(_items, index + count, _items, index, _size - index);
            }

            _version++;
            Array.Clear(_items, _size, count);
            /*
            if (RuntimeHelpers.IsReferenceOrContainsReferences<T>())
            {
                Array.Clear(_items, _size, count);
            }
             */
        }
    }

    // Removes the element at the given index. The size of the list is
    // decreased by one.
    public void RemoveAt(int index)
    {
        if (index < 0 && index >= _size)
        {
            throw new ArgumentOutOfRangeException("index" , "Index must not be negative and be less than the size of the list.");
            //ThrowHelper.ThrowArgumentOutOfRange_IndexMustBeLessException();
        }
        _size--;
        if (index < _size)
        {
            Array.Copy(_items, index + 1, _items, index, _size - index);
        }
        _items[_size] = null;
        /*
        if (RuntimeHelpers.IsReferenceOrContainsReferences<T>())
        {
            _items[_size] = default!;
        }
         */
        _version++;
    }

    // This method removes all items which matches the predicate.
    // The complexity is O(n).
    public int RemoveAll(Predicate<T> match)
    {
        ArgumentNullException.ThrowIfNull(match , "match");

        int freeIndex = 0;   // the first free slot in items array

        // Find the first item which needs to be removed.
        while (freeIndex < _size && !match.predicate(_items[freeIndex])) freeIndex++;
        if (freeIndex >= _size) return 0;

        int current = freeIndex + 1;
        while (current < _size)
        {
            // Find the first item which needs to be kept.
            while (current < _size && match.predicate(_items[current])) current++;

            if (current < _size)
            {
                // copy item to the free slot.
                _items[freeIndex++] = _items[current++];
            }
        }

        /*
        if (RuntimeHelpers.IsReferenceOrContainsReferences<T>())
        {
            Array.Clear(_items, freeIndex, _size - freeIndex); // Clear the elements so that the gc can reclaim the references.
        }
         */
        Array.Clear(_items, freeIndex, _size - freeIndex); // Clear the elements so that the gc can reclaim the references.

        int result = _size - freeIndex;
        _size = freeIndex;
        _version++;
        return result;
    }
}