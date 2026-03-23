package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImpl;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImplOptions;

import java.lang.reflect.Type;

/**
 * Represents a strongly typed list of objects that can be accessed by index. Provides methods to search, sort, and manipulate lists.
 * @param <T> The type of elements in the list.
 */
public class List<T>
    implements IList<T> , IReadOnlyList<T>
{
    /**
     * Enumerates the elements of a {@link List}.
     */
    public final class Enumerator
            implements IEnumerator<T>
    {
        private final List<T> _list;
        private int _index;
        private final int _version;
        @AllowNull
        private T _current;

        private Enumerator(List<T> list)
        {
            _list = list;
            _index = 0;
            _version = list._version;
            _current = null;
        }

        @Override
        public void Dispose()  { _current = null; }

        @Override
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

        @Override
        public T getCurrent() { return _current; }

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

        @Override
        public void Reset()
        {
            if (_version != _list._version) {
                throw new InvalidOperationException("Enumeration failed.");
                //ThrowHelper.ThrowInvalidOperationException_InvalidOperation_EnumFailedVersion();
            } else {
                _index = 0;
                _current = null;
            }
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

    /**
     * Initializes a new instance of the {@link List} class that is empty and has the default initial capacity.
     */
    public List()
    {
        _items = CreateArrayOfSize(DefaultCapacity);
    }

    /**
     * Initializes a new instance of the {@link List} class that is empty and has the specified initial capacity.
     * @param capacity The number of elements that the new list can initially store.
     * @throws ArgumentOutOfRangeException {@code capacity} is less than 0.
     */
    public List(int capacity)
            throws ArgumentOutOfRangeException
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

    /**
     * Initializes a new instance of the {@link List} class that contains elements copied from the specified collection and has sufficient capacity to accommodate the number of elements copied.
     * @param collection The collection whose elements are copied to the new list.
     * @throws ArgumentNullException {@code collection} is {@code null}.
     * @apiNote The elements are copied onto the {@link List} in the same order they are read by the enumerator of the collection. <br />
     * This constructor is an O(<i>n</i>) operation, where <i>n</i> is the number of elements in collection.
     */
    public List(IEnumerable<T> collection)
        throws ArgumentNullException
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
            _items = CreateArrayOfSize(10);
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

    /**
     * Gets the total number of elements the internal data structure can hold without resizing.
     * @return The number of elements that the {@link List} can contain before resizing is required.
     */
    public int getCapacity() { return _items.length; }

    /**
     * Sets the total number of elements the internal data structure can hold without resizing.
     * @param value The new number of elements that the {@link List} can contain before resizing is required.
     * @throws ArgumentOutOfRangeException Capacity is set to a value that is less than {@link #getCount()}.
     */
    public void setCapacity(int value)
            throws ArgumentOutOfRangeException
    {
        if (value < _size) {
            throw new ArgumentOutOfRangeException("value" , "Small capacity for the given contents of the List object");
            //ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.value, ExceptionResource.ArgumentOutOfRange_SmallCapacity);
        } else if (value != _items.length)
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
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity" , "A non-negative number is required.");
            //ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.capacity, ExceptionResource.ArgumentOutOfRange_NeedNonNegNum);
        } else if (_items.length < capacity) {
            Grow(capacity);
        }

        return _items.length;
    }

    /**
     * Gets the element at the specified index.
     * @param index The zero-based index of the element to get.
     * @return The element at the specified index.
     * @throws ArgumentOutOfRangeException {@code index} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code index} is equal to or greater than {@link #getCount()}.
     */
    @Override
    public T getItem(int index)
        throws ArgumentOutOfRangeException
    {
        if (index < 0 && index >= _size) {
            throw new ArgumentOutOfRangeException("index" , "Index must not be negative and less than the size of the list.");
        } else {
            return _items[index];
        }
    }

    /**
     * Sets the element at the specified index.
     * @param index The zero-based index of the element to set.
     * @param item The element to set at `index`.
     * @throws ArgumentOutOfRangeException {@code index} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code index} is equal to or greater than {@link #getCount()}.
     */
    @Override
    public void setItem(int index, @AllowNull T item)
        throws ArgumentOutOfRangeException
    {
        if (index < 0 && index >= _size) {
            throw new ArgumentOutOfRangeException("index" , "Index must not be negative and less than the size of the list.");
        } else {
            _items[index] = item;
            _version++;
        }
    }

    private void AddWithResize(T item)
    {
        // Debug.Assert(_size == _items.length);
        int size = _size;
        Grow(size + 1);
        _size = size + 1;
        _items[size] = item;
    }

    /**
     * Increase the capacity of this list to at least the specified {@code capacity}.
     * @param capacity The minimum capacity to ensure.
     */
    void Grow(int capacity)
    {
        //Debug.Assert(_items.Length < capacity);

        long newCapacity = _items.length == 0 ? DefaultCapacity : 2L * _items.length;

        // Allow the list to grow to maximum possible capacity (~2G elements) before encountering overflow.
        // mdcdi1315: The above is now worked around by upcasting to long - so it won't overflow (under normal conditions of course)
        // The original source had this as (int) and to work it around, it used an unsigned integer check, which of we cannot have in Java.
        if (newCapacity > Array.MaxLength) { newCapacity = Array.MaxLength; }

        // If the computed capacity is still less than specified, set to the original argument.
        // Capacities exceeding Array.MaxLength will be surfaced as OutOfMemoryException by Array.Resize.
        if (newCapacity < capacity) newCapacity = capacity;

        setCapacity((int)newCapacity);
    }

    // Adds the given object to the end of this list. The size of the list is
    // increased by one. If required, the capacity of the list is doubled
    // before adding the new element.
    //

    /**
     * Adds an object to the end of the {@link List}.
     * @param item The object to be added to the end of the {@link List}. The value can be {@code null} for reference types.
     */
    @Override
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
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

    /**
     * Adds the elements of the specified collection to the end of the {@link List}.
     * @param collection The collection whose elements should be added to the end of the {@link List}.
     *                   The collection itself cannot be {@code null}, but it can contain elements that are {@code null}, if type {@link T} is a reference type.
     * @throws ArgumentNullException {@code collection} is {@code null}.
     */
    public void AddRange(IEnumerable<T> collection)
        throws ArgumentNullException
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

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the first occurrence within the entire {@link List}.
     * @param match The {@link Predicate} delegate that defines the conditions of the element to search for.
     * @return The first element that matches the conditions defined by the specified predicate, if found; otherwise, the default value for type T. (Effectively {@code null} for Java)
     * @throws ArgumentNullException {@code match} is {@code null}.
     */
    @MaybeNull
    public T Find(Predicate<T> match)
        throws ArgumentNullException
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

    /**
     * Retrieves all the elements that match the conditions defined by the specified predicate.
     * @param match The {@link Predicate} delegate that defines the conditions of the element to search for.
     * @return A {@link List} containing all the elements that match the conditions defined by the specified predicate, if found; otherwise, an empty {@link List}.
     * @throws ArgumentNullException {@code match} is {@code null}.
     */
    public List<T> FindAll(Predicate<T> match)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(match , "match");

        List<T> list = new List<>(_items.length);
        T item;
        for (int i = 0; i < _size; i++)
        {
            if (match.predicate(item = _items[i]))
            {
                list.Add(item);
            }
        }
        list.TrimExcess();
        return list;
    }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the zero-based index of the first occurrence within the entire {@link List}.
     * @param match The {@link Predicate} delegate that defines the conditions of the element to search for.
     * @return The zero-based index of the first occurrence of an element that matches the conditions defined by {@code match}, if found; otherwise, -1.
     * @throws ArgumentNullException {@code match} is {@code null}.
     */
    public int FindIndex(Predicate<T> match) throws ArgumentNullException { return FindIndex(0, _size, match); }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the zero-based index of the
     * first occurrence within the range of elements in the {@link List} that extends from the specified index to the last element.
     * @param startIndex The zero-based starting index of the search.
     * @param match The {@link Predicate} delegate that defines the conditions of the element to search for.
     * @return The zero-based index of the first occurrence of an element that matches the conditions defined by {@code match}, if found; otherwise, -1.
     * @throws ArgumentNullException {@code match} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code startIndex} is outside the range of valid indexes for the {@link List}.
     */
    public int FindIndex(int startIndex, Predicate<T> match) throws ArgumentOutOfRangeException, ArgumentNullException { return FindIndex(startIndex, _size - startIndex, match); }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the zero-based index of the first
     * occurrence within the range of elements in the {@link List} that starts at the specified index and contains the specified number of elements.
     * @param startIndex The zero-based starting index of the search.
     * @param count The number of elements in the section to search.
     * @param match The {@link Predicate} delegate that defines the conditions of the element to search for.
     * @return The zero-based index of the first occurrence of an element that matches the conditions defined by {@code match}, if found; otherwise, -1.
     * @throws ArgumentNullException {@code match} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code startIndex} is outside the range of valid indexes for the {@link List}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code count} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code startIndex} and {@code count} do not specify a valid section in the {@link List}. <br /> <br />
     */
    public int FindIndex(int startIndex, int count, Predicate<T> match)
            throws ArgumentOutOfRangeException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(match , "match");
        if (startIndex > _size) {
            throw new ArgumentOutOfRangeException("startIndex" , "Start index must be less or equal than the collection length.");
            //ThrowHelper.ThrowStartIndexArgumentOutOfRange_ArgumentOutOfRange_IndexMustBeLessOrEqual();
        } else if (count < 0 || startIndex > _size - count) {
            throw new ArgumentOutOfRangeException("count" , "count must not be negative and be less than the size of the collection.");
            //ThrowHelper.ThrowCountArgumentOutOfRange_ArgumentOutOfRange_Count();
        } else {
            int endIndex = startIndex + count;
            for (int i = startIndex; i < endIndex; i++)
            {
                if (match.predicate(_items[i])) return i;
            }
            return -1;
        }
    }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the last occurrence within the entire {@link List}.
     * @param match The {@link Predicate} delegate that defines the conditions of the element to search for.
     * @return The last element that matches the conditions defined by the specified predicate, if found; otherwise, the default value for type {@link T}.
     * @throws ArgumentNullException {@code match} is {@code null}.
     */
    @MaybeNull
    public T FindLast(Predicate<T> match)
            throws ArgumentNullException
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

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the zero-based index of the last occurrence within the entire {@link List}.
     * @param match The {@link Predicate} delegate that defines the conditions of the element to search for.
     * @return The zero-based index of the last occurrence of an element that matches the conditions defined by match, if found; otherwise, -1.
     * @throws ArgumentNullException {@code match} is {@code null}.
     */
    public int FindLastIndex(Predicate<T> match) throws ArgumentNullException { return FindLastIndex(0, match); }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the zero-based index
     * of the last occurrence within the range of elements in the {@link List} that extends from the first element to the specified index.
     * @param startIndex The zero-based starting index of the backward search.
     * @param match The {@link Predicate} delegate that defines the conditions of the element to search for.
     * @return The zero-based index of the last occurrence of an element that matches the conditions defined by {@code match}, if found; otherwise, -1.
     * @throws ArgumentOutOfRangeException {@code startIndex} is outside the range of valid indexes for the {@link List}.
     * @throws ArgumentNullException {@code match} is {@code null}.
     */
    public int FindLastIndex(int startIndex, Predicate<T> match) throws ArgumentOutOfRangeException, ArgumentNullException { return FindLastIndex(startIndex, _size - startIndex, match); }

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the zero-based index of the last
     * occurrence within the range of elements in the {@link List} that contains the specified number of elements and ends at the specified index.
     * @param startIndex The zero-based starting index of the backward search.
     * @param count The number of elements in the section to search.
     * @param match The {@link Predicate} delegate that defines the conditions of the element to search for.
     * @return The zero-based index of the last occurrence of an element that matches the conditions defined by {@code match}, if found; otherwise, -1.
     * @throws ArgumentOutOfRangeException {@code startIndex} is outside the range of valid indexes for the {@link List}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code count} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code startIndex} and {@code count} do not specify a valid section in the {@link List}.
     * @throws ArgumentNullException {@code match} is {@code null}.
     */
    public int FindLastIndex(int startIndex, int count, Predicate<T> match)
            throws ArgumentOutOfRangeException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(match , "match");

        if (_size == 0) {
            // Special case for 0 length List
            if (startIndex != -1) {
                throw new ArgumentOutOfRangeException("index" , "Index must not be negative and less than the size of the list.");
                // ThrowHelper.ThrowStartIndexArgumentOutOfRange_ArgumentOutOfRange_IndexMustBeLess();
            }
        } else if (startIndex < 0 || startIndex >= _size) // Make sure we're not out of range
        {
            throw new ArgumentOutOfRangeException("index" , "Index must not be negative and less than the size of the list.");
            // ThrowHelper.ThrowStartIndexArgumentOutOfRange_ArgumentOutOfRange_IndexMustBeLess();
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
            if (match.predicate(_items[i])) { return i; }
        }
        return -1;
    }

    /**
     * Performs the specified action on each element of the {@link List}.
     * @param action The {@link Action1} delegate to perform on each element of the {@link List}.
     * @throws ArgumentNullException {@code action} is {@code null}.
     * @throws InvalidOperationException An element in the collection has been modified.
     */
    public void ForEach(Action1<T> action)
            throws ArgumentNullException, InvalidOperationException
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

    /**
     * Searches for the specified object and returns the zero-based index of the first occurrence within the entire {@link List}.
     * @param item The object to locate in the {@link List}. The value can be {@code null} for reference types.
     * @return The zero-based index of the first occurrence of item within the entire {@link List}, if found; otherwise, -1.
     */
    @Override
    public int IndexOf(@AllowNull T item) { return Array.IndexOf(_items , item , 0 , _size); }

    /**
     * Searches for the specified object and returns the zero-based index of the first occurrence within the range of elements in the {@link List} that extends from the specified index to the last element.
     * @param item The object to locate in the {@link List}. The value can be null for reference types.
     * @param index The zero-based starting index of the search. 0 (zero) is valid in an empty list.
     * @return The zero-based index of the first occurrence of item within the range of elements in the {@link List} that extends from index to the last element, if found; otherwise, -1.
     * @throws ArgumentOutOfRangeException {@code index} is outside the range of valid indexes for the {@link List}.
     */
    public int IndexOf(@AllowNull T item, int index) throws ArgumentOutOfRangeException { return IndexOf(item, index, _size - index); }

    /**
     * Searches for the specified object and returns the zero-based index of the first occurrence within the range
     * of elements in the {@link List} that starts at the specified index and contains the specified number of elements.
     * @param item The object to locate in the {@link List}. The value can be {@code null} for reference types.
     * @param index The zero-based starting index of the search. 0 (zero) is valid in an empty list.
     * @param count The number of elements in the section to search.
     * @return The zero-based index of the first occurrence of {@code item} within the range of elements in the {@link List} that starts at {@code index} and contains {@code count} number of elements, if found; otherwise, -1.
     * @throws ArgumentOutOfRangeException {@code startIndex} is outside the range of valid indexes for the {@link List}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code count} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code startIndex} and {@code count} do not specify a valid section in the {@link List}.
     */
    public int IndexOf(@AllowNull T item, int index, int count)
            throws ArgumentOutOfRangeException
    {
        if (index > _size)
            throw new ArgumentOutOfRangeException("index" , "Index must not be greater than the size of the list.");
            // ThrowHelper.ThrowArgumentOutOfRange_IndexMustBeLessOrEqualException();

        if (count < 0 || index > _size - count)
            throw new ArgumentOutOfRangeException("count" , "Count must not be negative and be less than the size of the collection.");
            // ThrowHelper.ThrowCountArgumentOutOfRange_ArgumentOutOfRange_Count();

        if (_size == 0 && index == 0) {
            return -1;
        } else {
            return Array.IndexOf(_items, item, index, count);
        }
    }

    /**
     * Creates a shallow copy of a range of elements in the source {@link List}.
     * @param index The zero-based {@link List} index at which the range starts.
     * @param count The number of elements in the range.
     * @return A shallow copy of a range of elements in the source {@link List}.
     * @throws ArgumentOutOfRangeException {@code index} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code count} is less than 0.
     * @throws ArgumentException {@code index} and {@code count} do not denote a valid range of elements in the {@link List}.
     */
    public List<T> GetRange(int index, int count)
            throws ArgumentOutOfRangeException, ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index" , "Index must not be negative.");
            //ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count" , "Count must not be negative.");
            //ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.count, ExceptionResource.ArgumentOutOfRange_NeedNonNegNum);
        } else if (_size - index < count) {
            throw new ArgumentException("Invalid offset and length.");
            // ThrowHelper.ThrowArgumentException(ExceptionResource.Argument_InvalidOffLen);
        } else {
            List<T> list = new List<>(count);
            Array.Copy(_items, index, list._items, 0, count);
            list._size = count;
            return list;
        }
    }

    // Inserts an element into this list at a given index. The size of the list
    // is increased by one. If required, the capacity of the list is doubled
    // before inserting the new element.
    //

    /**
     * Inserts an element into the {@link List} at the specified index.
     * @param index The zero-based index at which {@code item} should be inserted.
     * @param item The object to insert. The value can be {@code null} for reference types.
     * @throws ArgumentOutOfRangeException {@code index} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code index} is greater than {@link #getCount()}. <br /> <br />
     */
    @Override
    public void Insert(int index, @AllowNull T item)
        throws ArgumentOutOfRangeException
    {
        // Note that insertions at the end are legal.
        if (index < 0 && index > _size)
        {
            throw new ArgumentOutOfRangeException("index" , "Insertion index was invalid.");
            //ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.index, ExceptionResource.ArgumentOutOfRange_ListInsert);
        } else {
            if (_size == _items.length) { Grow(_size + 1); }
            if (index < _size) {
                Array.Copy(_items, index, _items, index + 1, _size - index);
            }
            _items[index] = item;
            _size++;
            _version++;
        }
    }

    /**
     * Gets the number of elements contained in the {@link List}.
     * @return The number of elements contained in the {@link List}.
     */
    @Override
    public int getCount() { return _size; }

    @Override
    public boolean getIsReadOnly() { return false; }

    /**
     * Removes all elements from the {@link List}.
     */
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

    /**
     * Determines whether an element is in the {@link List}.
     * @param item The object to locate in the {@link List}. The value can be {@code null} for reference types.
     * @return {@code true} if {@code item} is found in the {@link List}; otherwise, {@code false}.
     */
    @Override
    public boolean Contains(@AllowNull T item) { return IndexOf(item) > -1; }

    /**
     * Copies the entire {@link List} to a compatible one-dimensional array, starting at the specified index of the target array.
     * @param array The one-dimensional System.Array that is the destination of the elements copied from {@link List}. The array must have zero-based indexing.
     * @param arrayIndex The zero-based index in {@code array} at which copying begins.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code arrayIndex} is less than 0.
     * @throws ArgumentException The number of elements in the source {@link List} is greater than the available space from {@code arrayIndex} to the end of the destination array.
     */
    @Override
    public void CopyTo(T[] array, int arrayIndex)
            throws ArgumentNullException, ArgumentOutOfRangeException, ArgumentException
    {
        Array.Copy(_items , 0 , array , arrayIndex , _size);
    }

    /**
     * Removes the first occurrence of a specific object from the {@link List}.
     * @param item The object to remove from the {@link List}. The value can be {@code null} for reference types.
     * @return {@code true} if {@code item} is successfully removed; otherwise, {@code false}. This method also returns {@code false} if {@code item} was not found in the {@link List}.
     */
    // Removes the first occurrence of the given element, if found.
    // The size of the list is decreased by one if successful.
    @Override
    public boolean Remove(T item)
    {
        int index = IndexOf(item);
        if (index > -1) {
            RemoveAt(index);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Enumerator GetEnumerator() { return new Enumerator(this); }

    // Sets the capacity of this list to the size of the list. This method can
    // be used to minimize a list's memory overhead once it is known that no
    // new elements will be added to the list. To completely clear a list and
    // release all memory referenced by the list, execute the following
    // statements:
    //
    // list.Clear();
    // list.TrimExcess();
    //
    /**
     * Sets the capacity to the actual number of elements in the {@link List}, if that number is less than a threshold value.
     * @implNote The current threshold of 90 percent might change in future releases.
     */
    public void TrimExcess()
    {
        int threshold = (int)(((double)_items.length) * 0.9);
        if (_size < threshold)
        {
            setCapacity(_size);
        }
    }

    /**
     * Determines whether every element in the {@link List} matches the conditions defined by the specified predicate.
     * @param match The {@link Predicate} delegate that defines the conditions to check against the elements.
     * @return {@code true} if every element in the {@link List} matches the conditions defined by the specified predicate; otherwise, {@code false}. If the list has no elements, the return value is {@code true}.
     * @throws ArgumentNullException {@code match} is {@code null}.
     */
    public boolean TrueForAll(Predicate<T> match)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(match , "match");

        for (int i = 0; i < _size; i++)
        {
            if (!match.predicate(_items[i])) { return false; }
        }
        return true;
    }

    // ToArray returns an array containing the contents of the List.
    // This requires copying the List, which is an O(n) operation.

    /**
     * Returns the contents of this List object to an array.
     * @return The contents of the object to an array.
     * @deprecated Due to Java restrictions, it is not possible to create an array of type T without knowing it's class.
     * The class cannot be determined by JVM at run-time, so this will throw casting exceptions due to incorrect detection.
     */
    @Deprecated(forRemoval = true, since = "1.0.2")
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
    /**
     * Reverses the order of the elements in the specified range.
     * @param index The zero-based starting index of the range to reverse.
     * @param count The number of elements in the range to reverse.
     * @throws ArgumentOutOfRangeException {@code index} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code count} is less than 0.
     * @throws ArgumentException {@code index} and {@code count} do not denote a valid range of elements in the {@link List}.
     */
    public void Reverse(int index, int count)
            throws ArgumentOutOfRangeException, ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index" , "Must not be negative.");
            //ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count" , "Must not be negative.");
            //ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.count, ExceptionResource.ArgumentOutOfRange_NeedNonNegNum);
        } else if (_size - index < count) {
            throw new ArgumentException("The given offset and length were outside the array bounds.");
            //ThrowHelper.ThrowArgumentException(ExceptionResource.Argument_InvalidOffLen);
        } else {
            if (count > 1) {
                Array.Reverse(_items, index, count);
            }
            _version++;
        }
    }

    /**
     * Reverses the order of the elements in the entire {@link List}.
     */
    // Reverses the elements in this list.
    public void Reverse() { Reverse(0, getCount()); }

    /**
     * Removes a range of elements from the {@link List}.
     * @param index The zero-based starting index of the range of elements to remove.
     * @param count The number of elements to remove.
     * @throws ArgumentOutOfRangeException {@code index} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code count} is less than 0.
     * @throws ArgumentException {@code index} and {@code count} do not denote a valid range of elements in the {@link List}.
     */
    // Removes a range of elements from this list.
    public void RemoveRange(int index, int count)
        throws ArgumentOutOfRangeException, ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index" , "Must not be negative.");
            //ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count" , "Must not be negative.");
            //ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.count, ExceptionResource.ArgumentOutOfRange_NeedNonNegNum);
        } else if (_size - index < count) {
            throw new ArgumentException("The given offset and length were outside the array bounds.");
            //ThrowHelper.ThrowArgumentException(ExceptionResource.Argument_InvalidOffLen);
        } else if (count > 0) {
            _size -= count;
            if (index < _size) {
                Array.Copy(_items, index + count, _items, index, _size - index);
            }

            _version++;
            // if (RuntimeHelpers.IsReferenceOrContainsReferences<T>()) {
            Array.Clear(_items, _size, count);
            // }
        }
    }

    /**
     * Removes the element at the specified index of the {@link List}.
     * @param index The zero-based index of the element to remove.
     * @throws ArgumentOutOfRangeException {@code index} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code index} is equal to or greater than {@link #getCount()}.
     */
    // Removes the element at the given index. The size of the list is
    // decreased by one.
    public void RemoveAt(int index)
        throws ArgumentOutOfRangeException
    {
        if (index < 0 && index >= _size) {
            throw new ArgumentOutOfRangeException("index" , "Index must not be negative and be less than the size of the list.");
            //ThrowHelper.ThrowArgumentOutOfRange_IndexMustBeLessException();
        } else {
            _size--;
            if (index < _size) {
                Array.Copy(_items, index + 1, _items, index, _size - index);
            }
            // if (RuntimeHelpers.IsReferenceOrContainsReferences<T>()) {
            _items[_size] = null;
            // }
            _version++;
        }
    }

    /**
     * Removes all the elements that match the conditions defined by the specified predicate.
     * @param match The {@link Predicate} delegate that defines the conditions of the elements to remove.
     * @return The number of elements removed from the {@link List}.
     * @throws ArgumentNullException {@code match} is {@code null}.
     */
    // This method removes all items which matches the predicate.
    // The complexity is O(n).
    public int RemoveAll(Predicate<T> match)
        throws ArgumentNullException
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

        // if (RuntimeHelpers.IsReferenceOrContainsReferences<T>()) {
        Array.Clear(_items, freeIndex, _size - freeIndex); // Clear the elements so that the gc can reclaim the references.
        // }

        int result = _size - freeIndex;
        _size = freeIndex;
        _version++;
        return result;
    }
}