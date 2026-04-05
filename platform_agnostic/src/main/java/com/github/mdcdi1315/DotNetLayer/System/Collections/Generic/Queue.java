package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNullWhen;

/**
 * Represents a first-in, first-out collection of objects.
 * @param <T> Specifies the type of elements in the queue.
 */
public class Queue<T>
    implements IEnumerable<T>, ICollection, IReadOnlyCollection<T>
{
    private Object[] _array;
    private int _head;       // The index from which to dequeue if the queue isn't empty.
    private int _tail;       // The index at which to enqueue if the queue isn't full.
    private int _size;       // Number of elements.
    private int _version;

    /**
     * Initializes a new instance of the {@link Queue} class that is empty and has the default initial capacity.
     */
    // Creates a queue with room for capacity objects. The default initial
    // capacity and grow factor are used.
    public Queue()
    {
        _array = new Object[0];
    }

    /**
     * Initializes a new instance of the {@link Queue} class that is empty and has the specified initial capacity.
     * @param capacity The initial number of elements that the {@link Queue} can contain.
     * @throws ArgumentOutOfRangeException {@code capacity} is less than zero.
     */
    // Creates a queue with room for capacity objects. The default grow factor
    // is used.
    public Queue(int capacity)
        throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative value.");
        } else {
            _array = new Object[capacity];
        }
    }

    /**
     * Initializes a new instance of the {@link Queue} class that contains elements copied from the specified collection and has sufficient capacity to accommodate the number of elements copied.
     * @param collection The collection whose elements are copied to the new {@link Queue}.
     * @throws ArgumentNullException {@code collection} is {@code null}.
     */
    // Fills a Queue with the elements of an ICollection.  Uses the enumerator
    // to get each of the elements.
    public Queue(IEnumerable<T> collection)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(collection, "collection");

        if (collection instanceof ICollection c) {
            _array = new Object[_size = c.getCount()];
            c.CopyTo(_array, 0);
        } else {
            IEnumerator<T> e = collection.GetEnumerator();
            List<Object> obj_list = new List<>(15);
            try {
                while (e.MoveNext()) { obj_list.Add(e.getCurrent()); }
            } finally {
                e.Dispose();
            }
            _array = new Object[_size = obj_list.getCount()];
            obj_list.CopyTo(_array, 0);
        }

        if (_size != _array.length) { _tail = _size; }

        /**
        _array = EnumerableHelpers.ToArray(collection, out _size);
        if (_size != _array.Length) _tail = _size;
         */
    }

    @Override
    public int getCount() { return _size; }

    @Override
    public Object getSyncRoot() { return this; }

    @Override
    public boolean getIsSynchronized() { return false; }

    @Override
    public Enumerator<T> GetEnumerator() { return new Enumerator<>(this); }

    @Override
    public void CopyTo(Object array, int index)
            throws ArgumentException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        int length = java.lang.reflect.Array.getLength(array);

        if (index < 0 || index > length)
        {
            // ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.arrayIndex, ExceptionResource.ArgumentOutOfRange_IndexMustBeLessOrEqual);
            throw new ArgumentOutOfRangeException("index", "Array index must be less than or equal to the array's length.");
        }

        if (length - index < _size)
        {
            // ThrowHelper.ThrowArgumentException(ExceptionResource.Argument_InvalidOffLen);
            throw new ArgumentException("Invalid offset and length for the given Queue and input array.");
        }

        int numToCopy = _size;
        if (numToCopy == 0) return;

        try {
            int firstPart = Math.min(_array.length - _head, numToCopy);
            System.arraycopy(_array, _head, array, index, firstPart);
            numToCopy -= firstPart;
            if (numToCopy > 0) {
                System.arraycopy(_array, 0, array, index + _array.length - _head, numToCopy);
            }
        } catch (ArrayStoreException e) {
            throw new ArgumentException("Incompatible array type detected.");
        }
    }

    /**
     * Removes all objects from the {@link Queue}.
     */
    public void Clear()
    {
        if (_size != 0)
        {
            if (_head < _tail) {
                Array.Clear(_array, _head, _size);
            } else {
                Array.Clear(_array, _head, _array.length - _head);
                Array.Clear(_array, 0, _tail);
            }

            _size = 0;
        }

        _head = 0;
        _tail = 0;
        _version++;
    }

    /**
     * Copies the {@link Queue} elements to an existing one-dimensional {@link java.lang.reflect.Array}, starting at the specified array index.
     * @param array The one-dimensional {@link java.lang.reflect.Array} that is the destination of the elements copied from {@link Queue}. The {@link java.lang.reflect.Array} must have zero-based indexing.
     * @param arrayIndex The zero-based index in {@code array} at which copying begins.
     * @throws ArgumentException The number of elements in the source {@link Queue} is greater than the available space from {@code arrayIndex} to the end of the destination array.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code arrayIndex} is less than zero.
     */
    // CopyTo copies a collection into an Array, starting at a particular
    // index into the array.
    public void CopyTo(T[] array, int arrayIndex)
        throws ArgumentException, ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array, "array");

        if (arrayIndex < 0 || arrayIndex > array.length)
        {
            // ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.arrayIndex, ExceptionResource.ArgumentOutOfRange_IndexMustBeLessOrEqual);
            throw new ArgumentOutOfRangeException("arrayIndex", "Array index must be less than or equal to the array's length.");
        }

        if (array.length - arrayIndex < _size)
        {
            // ThrowHelper.ThrowArgumentException(ExceptionResource.Argument_InvalidOffLen);
            throw new ArgumentException("Invalid offset and length for the given Queue and input array.");
        }

        int numToCopy = _size;
        if (numToCopy == 0) return;

        int firstPart = Math.min(_array.length - _head, numToCopy);
        Array.Copy(_array, _head, array, arrayIndex, firstPart);
        numToCopy -= firstPart;
        if (numToCopy > 0)
        {
            Array.Copy(_array, 0, array, arrayIndex + _array.length - _head, numToCopy);
        }
    }

    // Increments the index wrapping it if necessary.
    private int MoveNext(int index)
    {
        // It is tempting to use the remainder operator here but it is actually much slower
        // than a simple comparison and a rarely taken branch.
        // JIT produces better code than with ternary operator ?:
        int tmp = index + 1;
        if (tmp == _array.length) { tmp = 0; }
        return tmp;
    }

    // PRIVATE Grows or shrinks the buffer to hold capacity objects. Capacity
    // must be >= _size.
    private void SetCapacity(int capacity)
    {
        // Debug.Assert(capacity >= _size);
        Object[] newarray = new Object[capacity];
        if (_size > 0)
        {
            if (_head < _tail)
            {
                Array.Copy(_array, _head, newarray, 0, _size);
            }
            else
            {
                Array.Copy(_array, _head, newarray, 0, _array.length - _head);
                Array.Copy(_array, 0, newarray, _array.length - _head, _tail);
            }
        }

        _array = newarray;
        _head = 0;
        _tail = (_size == capacity) ? 0 : _size;
        _version++;
    }

    /**
     * Sets the capacity to the actual number of elements in the {@link Queue}, if that number is less than 90 percent of current capacity.
     */
    public void TrimExcess()
    {
        int threshold = (int)(_array.length * 0.9);
        if (_size < threshold) { SetCapacity(_size); }
    }

    /**
     * Sets the capacity of a {@link Queue} object to the specified number of entries.
     * @param capacity The new capacity.
     * @throws ArgumentOutOfRangeException Passed capacity is lower than entries count.
     */
    public void TrimExcess(int capacity)
        throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative value.");
        } else if (capacity < _size) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be less than the queue's size.");
        } else if (capacity != _array.length) {
            SetCapacity(capacity);
        }
    }

    private void Grow(int capacity)
    {
        // Debug.Assert(_array.Length < capacity);

        final long GrowFactor = 2L;
        final int MinimumGrow = 4;

        long newcapacity = GrowFactor * _array.length;

        // Allow the list to grow to maximum possible capacity (~2G elements) before encountering overflow.
        // Note that this check works even when _items.Length overflowed thanks to the (uint) cast
        if (newcapacity > Array.MaxLength) newcapacity = Array.MaxLength;

        // Ensure minimum growth is respected.
        newcapacity = Math.max(newcapacity, _array.length + MinimumGrow);

        // If the computed capacity is still less than specified, set to the original argument.
        // Capacities exceeding Array.MaxLength will be surfaced as OutOfMemoryException by Array.Resize.
        if (newcapacity < capacity) newcapacity = capacity;

        SetCapacity((int)newcapacity);
    }

    /**
     * Adds an object to the end of the {@link Queue}.
     * @param item The object to add to the {@link Queue}. The value can be {@code null} for reference types.
     */
    // Adds item to the tail of the queue.
    public void Enqueue(@AllowNull T item)
    {
        if (_size == _array.length)
        {
            Grow(_size + 1);
        }

        _array[_tail] = item;
        _tail = MoveNext(_tail);
        _size++;
        _version++;
    }

    /**
     * Removes and returns the object at the beginning of the {@link Queue}.
     * @return The object that is removed from the beginning of the {@link Queue}.
     * @throws InvalidOperationException The {@link Queue} is empty.
     */
    // Removes the object at the head of the queue and returns it. If the queue
    // is empty, this method throws an
    // InvalidOperationException.
    public T Dequeue()
        throws InvalidOperationException
    {
        int head = _head;
        Object[] array = _array;

        if (_size == 0) { ThrowForEmptyQueue(); }

        T removed = (T)array[head];
        array[head] = null;
        _head = MoveNext(_head);
        _size--;
        _version++;
        return removed;
    }

    /**
     * Returns the object at the beginning of the {@link Queue} without removing it.
     * @return The object at the beginning of the {@link Queue}.
     * @throws InvalidOperationException The {@link Queue} is empty.
     */
    // Returns the object at the head of the queue. The object remains in the
    // queue. If the queue is empty, this method throws an
    // InvalidOperationException.
    public T Peek()
        throws InvalidOperationException
    {
        if (_size == 0)
        {
            ThrowForEmptyQueue();
        }

        return (T)_array[_head];
    }

    /**
     * Returns a value that indicates whether there is an object at the beginning of the {@link Queue}, and if one is present, copies it to the result parameter. The object is not removed from the {@link Queue}.
     * @param result If present, the object at the beginning of the {@link Queue}; otherwise, the default value of {@link T}.
     * @return {@code true} if there is an object at the beginning of the {@link Queue}; {@code false} if the {@link Queue} is empty.
     * @throws AssertionError [.NET LAYER] If {@code result} is {@code null}.
     */
    public boolean TryPeek(@MaybeNullWhen(ReturnValue = false) @DotNetByRefParameter(ByRefParameterType.OUT) ByRefParameter<T> result)
    {
        ByRefParameter.AssertEOut(result);
        if (_size == 0) {
            result.Value = null;
            return false;
        } else {
            result.Value = (T)_array[_head];
            return true;
        }
    }

    /**
     * Removes the object at the beginning of the {@link Queue}, and copies it to the result parameter.
     * @param result The removed object.
     * @return {@code true} if the object is successfully removed; {@code false} if the {@link Queue} is empty.
     * @throws AssertionError [.NET LAYER] If {@code result} is {@code null}.
     */
    public boolean TryDequeue(@MaybeNullWhen(ReturnValue = false) @DotNetByRefParameter(ByRefParameterType.OUT) ByRefParameter<T> result)
    {
        ByRefParameter.AssertEOut(result);
        int head = _head;
        Object[] array = _array;

        if (_size == 0)
        {
            result.Value = null;
            return false;
        }

        result.Value = (T)array[head];
        array[head] = null;
        _head = MoveNext(_head);
        _size--;
        _version++;
        return true;
    }

    /**
     * Determines whether an element is in the {@link Queue}.
     * @param item The object to locate in the {@link Queue}. The value can be {@code null} for reference types.
     * @return {@code true} if {@code item} is found in the {@link Queue}; otherwise, {@code false}.
     */
    // Returns true if the queue contains at least one object equal to item.
    // Equality is determined using EqualityComparer<T>.Default.Equals().
    public boolean Contains(@AllowNull T item)
    {
        if (_size == 0) {
            return false;
        } else if (_head < _tail) {
            return Array.IndexOf(_array, item, _head, _size) >= 0;
        } else {
            // We've wrapped around. Check both partitions, the least recently enqueued first.
            return
                    Array.IndexOf(_array, item, _head, _array.length - _head) >= 0 ||
                            Array.IndexOf(_array, item, 0, _tail) >= 0;
        }
    }

    private void ThrowForEmptyQueue()
    {
        // Debug.Assert(_size == 0);
        // throw new InvalidOperationException(SR.InvalidOperation_EmptyQueue);
        throw new InvalidOperationException("The Queue is empty.");
    }

    // Implements an enumerator for a Queue.  The enumerator uses the
    // internal version number of the list to ensure that no modifications are
    // made to the list while an enumeration is in progress.
    @ClassIsDotNetStruct
    public static final class Enumerator<T>
            extends ValueType
            implements IEnumerator<T>
    {
        private final Queue<T> _queue;
        private final int _version;
        private int _i;
        private T _currentElement;

        public Enumerator()
        {
            super();
            _queue = null;
            _version = 0;
        }

        private Enumerator(Queue<T> queue)
        {
            super();
            _i = -1;
            _queue = queue;
            _currentElement = null;
            _version = queue._version;
        }

        public void Dispose()
        {
            _i = -2;
            _currentElement = null;
        }

        public boolean MoveNext()
        {
            if (_version != _queue._version) {
                throw new InvalidOperationException("The collection was modified while it is enumerated.");
                // ThrowHelper.ThrowInvalidOperationException_InvalidOperation_EnumFailedVersion();
            } else {
                Queue<T> q = _queue;
                int size = q._size;

                int offset = _i + 1;
                if (offset > -1 && offset < size) {
                    _i = offset;

                    Object[] array = q._array;
                    int index = q._head + offset;
                    if (index > -1 && index < array.length) {
                        _currentElement = (T)array[index];
                    } else {
                        // The index has wrapped around the end of the array. Shift the index and then
                        // get the current element. It is tempting to dedup this dereferencing with that
                        // in the if block above, but the if block above avoids a bounds check for the
                        // accesses that are in that portion, whereas these still incur it.
                        index -= array.length;
                        _currentElement = (T)array[index];
                    }

                    return true;
                } else {
                    _i = -2;
                    _currentElement = null;
                    return false;
                }
            }
        }

        @Override
        public T getCurrent() { return _currentElement; }

        public void Reset()
        {
            if (_version != _queue._version) {
                throw new InvalidOperationException("The collection was modified while it is enumerated.");
            } else {
                _i = -1;
                _currentElement = null;
            }
        }
    }
}
