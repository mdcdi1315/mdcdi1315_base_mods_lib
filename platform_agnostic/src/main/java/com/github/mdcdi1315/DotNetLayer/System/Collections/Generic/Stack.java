package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImpl;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNullWhen;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImplOptions;

/**
 * Represents a variable size last-in-first-out (LIFO) collection of instances of the same specified type.
 * @param <T> Specifies the type of elements in the stack.
 */
public class Stack<T>
    implements IEnumerable<T>, IReadOnlyCollection<T>, ICollection
{
    private Object[] _array; // Storage for stack elements. Do not rename (binary serialization)
    private int _size; // Number of items in the stack. Do not rename (binary serialization)
    private int _version; // Used to keep enumerator in sync w/ collection. Do not rename (binary serialization)

    private static final int DefaultCapacity = 4;

    /**
     * Initializes a new instance of the {@link Stack} class that is empty and has the default initial capacity.
     */
    public Stack()
    {
        _array = new Object[0];
    }

    /**
     * Initializes a new instance of the {@link Stack} class that is empty and has the specified initial capacity or the default initial capacity, whichever is greater.
     * @param capacity The initial number of elements that the {@link Stack} can contain.
     * @throws ArgumentOutOfRangeException {@code capacity} is less than zero.
     */
    // Create a stack with a specific initial capacity.  The initial capacity
    // must be a non-negative number.
    public Stack(int capacity)
        throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity value cannot be negative.");
        } else {
            // mdcdi1315: Default Stack class seems to not obey the API rule "has the specified initial capacity or the default initial capacity, whichever is greater".
            // So, we fix it here ourselves.
            _array = new Object[Math.max(capacity, DefaultCapacity)];
        }
    }

    /**
     * Initializes a new instance of the {@link Stack} class that contains elements copied from the
     * specified collection and has sufficient capacity to accommodate the number of elements copied.
     * @param collection The collection to copy elements from.
     * @throws ArgumentNullException {@code collection} is {@code null}.
     */
    // Fills a Stack with the contents of a particular collection.  The items are
    // pushed onto the stack in the same order they are read by the enumerator.
    public Stack(IEnumerable<T> collection)
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

        // _array = EnumerableHelpers.ToArray(collection, out _size);
    }

    /**
     * Gets the total numbers of elements the internal data structure can hold without resizing.
     */
    public int GetCapacity() { return _array.length; }

    /**
     * Gets the number of elements contained in the {@link Stack}.
     * @return The number of elements contained in the {@link Stack}.
     */
    @Override
    public int getCount() { return _size; }

    @Override
    public Object getSyncRoot() { return this; }

    @Override
    public boolean getIsSynchronized() { return false; }

    /**
     * Removes all objects from the {@link Stack}.
     */
    public void Clear()
    {
        // if (RuntimeHelpers.IsReferenceOrContainsReferences<T>())
        // {
        Array.Clear(_array, 0, _size); // Don't need to doc this but we clear the elements so that the gc can reclaim the references.
        // }
        _size = 0;
        _version++;
    }

    /**
     * Determines whether an element is in the {@link Stack}.
     * @param item The object to locate in the {@link Stack}. The value can be {@code null} for reference types.
     * @return {@code true} if {@code item} is found in the {@link Stack}; otherwise, {@code false}.
     */
    public boolean Contains(@AllowNull T item)
    {
        // Compare items using the default equality comparer

        // PERF: Internally Array.LastIndexOf calls
        // EqualityComparer<T>.Default.LastIndexOf, which
        // is specialized for different types. This
        // boosts performance since instead of making a
        // virtual method call each iteration of the loop,
        // via EqualityComparer<T>.Default.Equals, we
        // only make one virtual call to EqualityComparer.LastIndexOf.

        return _size != 0 && Array.LastIndexOf(_array, item, _size - 1) != -1;
    }

    /**
     * Copies the {@link Stack} to an existing one-dimensional {@link java.lang.reflect.Array}, starting at the specified array index.
     * @param array The one-dimensional {@link java.lang.reflect.Array} that is the destination of the elements copied from {@link Stack}. The {@link java.lang.reflect.Array} must have zero-based indexing.
     * @param arrayIndex The zero-based index in {@code array} at which copying begins.
     * @throws ArgumentException The number of elements in the source {@link Stack} is greater than the available space from {@code arrayIndex} to the end of the destination {@code array}.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code arrayIndex} is less than zero.
     */
    public void CopyTo(T[] array, int arrayIndex)
        throws ArgumentException, ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array);

        if (arrayIndex < 0 || arrayIndex > array.length)
        {
            throw new ArgumentOutOfRangeException("arrayIndex", arrayIndex, "Array index must be less or equal than the array's length.");
        }

        if (array.length - arrayIndex < _size)
        {
            throw new ArgumentException("Invalid offset and length.");
        }

        // Debug.Assert(array != _array);
        int srcIndex = 0;
        int dstIndex = arrayIndex + _size;
        while (srcIndex < _size)
        {
            array[--dstIndex] = (T)_array[srcIndex++];
        }
    }

    @Override
    public void CopyTo(Object array, int index)
            throws ArgumentException
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
            throw new ArgumentException("Invalid offset and length for the given Stack and input array.");
        }

        try {
            System.arraycopy(_array, 0, array, index, _size);
            // Array.Copy(_array, 0, array, index, _size);
            Array.Reverse((T[])array, index, _size);
        } catch (ArrayStoreException r) {
            throw new ArgumentException("Incompatible array type detected.", "array");
        }
    }

    /**
     * Sets the capacity to the actual number of elements in the {@link Stack}, if that number is less than 90 percent of current capacity.
     */
    public void TrimExcess()
    {
        int threshold = (int)(_array.length * 0.9);
        if (_size < threshold)
        {
            // Array.Resize(ref _array, _size);
            Object[] new_array = new Object[_size];
            if (_size > 0) {
                Array.Copy(_array, new_array, _size);
            }
            _array = new_array;
        }
    }

    /**
     * Sets the capacity of a {@link Stack} object to a specified number of entries.
     * @param capacity The new capacity.
     * @throws ArgumentOutOfRangeException Passed capacity is lower than 0 or entries count.
     */
    public void TrimExcess(int capacity)
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative value.");
        } else if (capacity < _size) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be less than the stack's size.");
        } else if (capacity != _array.length) {
            // Array.Resize(ref _array, capacity);
            Object[] new_array = new Object[_size];
            if (_size > 0) {
                Array.Copy(_array, new_array, _size);
            }
            _array = new_array;
        }
    }

    /**
     * Returns the object at the top of the {@link Stack} without removing it.
     * @return The object at the top of the {@link Stack}.
     * @throws InvalidOperationException The {@link Stack} is empty.
     */
    // Returns the top object on the stack without removing it.  If the stack
    // is empty, Peek throws an InvalidOperationException.
    @NotNull
    public T Peek()
        throws InvalidOperationException
    {
        int size = _size - 1;
        Object[] array = _array;

        if (size < 0 || size >= array.length)
        {
            ThrowForEmptyStack();
        }

        return (T)array[size];
    }

    /**
     * Returns a value that indicates whether there is an object at the top of the {@link Stack}, and if one is present, copies it to the {@code result} parameter.
     * The object is not removed from the {@link Stack}.
     * @param result If present, the object at the top of the {@link Stack}; otherwise, the default value of {@link T}.
     * @return {@code true} if there is an object at the top of the {@link Stack}; {@code false} if the {@link Stack} is empty.
     * @throws AssertionError [.NET LAYER] If {@code result} is {@code null}.
     */
    public boolean TryPeek(@MaybeNullWhen(ReturnValue = false) @DotNetByRefParameter(ByRefParameterType.OUT) ByRefParameter<T> result)
    {
        ByRefParameter.AssertEOut(result);
        int size = _size - 1;
        Object[] array = _array;
        if (size < 0 || size >= array.length) {
            result.Value = null;
            return false;
        } else {
            result.Value = (T)array[size];
            return true;
        }
    }

    /**
     * Removes and returns the object at the top of the {@link Stack}.
     * @return The object removed from the top of the {@link Stack}.
     * @throws InvalidOperationException The {@link Stack} is empty.
     */
    // Pops an item from the top of the stack.  If the stack is empty, Pop
    // throws an InvalidOperationException.
    @NotNull
    public T Pop()
        throws InvalidOperationException
    {
        int size = _size - 1;
        Object[] array = _array;

        // if (_size == 0) is equivalent to if (size == -1), and this case
        // is covered with (uint)size, thus allowing bounds check elimination
        // https://github.com/dotnet/coreclr/pull/9773
        if (size < 0 || size >= array.length)
        {
            ThrowForEmptyStack();
        }

        _version++;
        _size = size;
        T item = (T)array[size];
        // if (RuntimeHelpers.IsReferenceOrContainsReferences<T>())
        // {
            array[size] = null;     // Free memory quicker.
        // }
        return item;
    }

    /**
     * Returns a value that indicates whether there is an object at the top of the {@link Stack},
     * and if one is present, copies it to the {@code result} parameter, and removes it from the {@link Stack}.
     * @param result If present, the object at the top of the {@link Stack}; otherwise, the default value of {@link T}.
     * @return {@code true} if there is an object at the top of the {@link Stack}; {@code false} if the {@link Stack} is empty.
     */
    public boolean TryPop(@MaybeNullWhen(ReturnValue = false) @DotNetByRefParameter(ByRefParameterType.OUT) ByRefParameter<T> result)
    {
        ByRefParameter.AssertEOut(result);
        int size = _size - 1;
        Object[] array = _array;

        if (size < 0 || size >= array.length)
        {
            result.Value = null;
            return false;
        }

        _version++;
        _size = size;
        result.Value = (T)array[size];
        // if (RuntimeHelpers.IsReferenceOrContainsReferences<T>())
        // {
            array[size] = null;
        // }
        return true;
    }

    /**
     * Inserts an object at the top of the {@link Stack}.
     * @param item The object to push onto the {@link Stack}. The value can be {@code null} for reference types.
     */
    // Pushes an item to the top of the stack.
    public void Push(T item)
    {
        int size = _size;
        Object[] array = _array;

        if (size > -1 && size < array.length) {
            array[size] = item;
            _version++;
            _size = size + 1;
        } else {
            PushWithResize(item);
        }
    }

    // Non-inline from Stack.Push to improve its code quality as uncommon path
    //    [MethodImpl(MethodImplOptions.NoInlining)]
    @MethodImpl(GetValue = MethodImplOptions.NoInlining)
    private void PushWithResize(T item)
    {
        // Debug.Assert(_size == _array.length);
        Grow(_size + 1);
        _array[_size] = item;
        _version++;
        _size++;
    }

    /**
     * Ensures that the capacity of this Stack is at least the specified {@code capacity}.
     * If the current capacity of the Stack is less than specified {@code capacity},
     * the capacity is increased by continuously twice current capacity until it is at least the specified {@code capacity}.
     * @param capacity The minimum capacity to ensure.
     * @return The new capacity of this stack.
     * @throws ArgumentOutOfRangeException {@code capacity} is a negative value.
     */
    public int EnsureCapacity(int capacity)
        throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative value.");
        } else {
            if (_array.length < capacity)
            {
                Grow(capacity);
            }

            return _array.length;
        }
    }

    private void Grow(int capacity)
    {
        // Debug.Assert(_array.length < capacity);

        long newcapacity = _array.length == 0 ? DefaultCapacity : 2L * _array.length;

        // Allow the list to grow to maximum possible capacity (~2G elements) before encountering overflow.
        // Note that this check works even when _items.Length overflowed thanks to the (uint) cast.
        if (newcapacity > Array.MaxLength) newcapacity = Array.MaxLength;

        // If computed capacity is still less than specified, set to the original argument.
        // Capacities exceeding Array.MaxLength will be surfaced as OutOfMemoryException by Array.Resize.
        if (newcapacity < capacity) newcapacity = capacity;

        // Array.Resize(ref _array, newcapacity);
        Object[] new_array = new Object[(int)newcapacity];
        if (_size > 0) {
            Array.Copy(_array, new_array, _size);
        }
        _array = new_array;
    }

    @Override
    public Enumerator<T> GetEnumerator() { return new Enumerator<>(this); }

    private void ThrowForEmptyStack()
    {
        // Debug.Assert(_size == 0);
        throw new InvalidOperationException("The Stack is empty.");
    }

    @ClassIsDotNetStruct
    public static final class Enumerator<T>
            extends ValueType
            implements IEnumerator<T>
    {
        private final Stack<T> _stack;
        private final int _version;
        private int _index;
        private T _currentElement;

        // Implicit parameterless constructor declaration as defined by the ValueType semantics.
        public Enumerator()
        {
            super();
            _stack = null;
            _version = 0;
        }

        private Enumerator(Stack<T> stack)
        {
            super();
            _stack = stack;
            _version = stack._version;
            _index = stack.getCount();
            _currentElement = null;
        }

        @Override
        public boolean MoveNext()
        {
            if (_version != _stack._version)
            {
                ThrowInvalidVersion();
            }

            Object[] array = _stack._array;
            int index = _index - 1;
            if (index > -1 && index < array.length) {
                // Debug.Assert(index < _stack.Count);
                _currentElement = (T)array[index];
                _index = index;
                return true;
            } else {
                _currentElement = null;
                _index = -1;
                return false;
            }
        }

        @Override
        public void Dispose() { _index = -1; }

        @Override
        public T getCurrent() { return _currentElement; }

        // public T Current => _currentElement!;

        // object? System.Collections.IEnumerator.Current => Current;

        public void Reset()
                throws InvalidOperationException
        {
            if (_version != _stack._version)
            {
                ThrowInvalidVersion();
            }

            _currentElement = null;
            _index = _stack.getCount();
        }

        private static void ThrowInvalidVersion()
                throws InvalidOperationException
        {
            throw new InvalidOperationException("Enumeration failed because the collection is modified.");
        }
    }
}
