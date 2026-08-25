package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IReadOnlyList;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNullWhen;

import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.IsReadOnly;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

import java.util.Arrays;

/**
 * Delimits a section of a one-dimensional array.
 * @param <T> The type of the elements in the array segment.
 */
// Note: users should make sure they copy the fields out of an ArraySegment onto their stack
// then validate that the fields describe valid bounds within the array.  This must be done
// because assignments to value types are not atomic, and also because one thread reading
// three fields from an ArraySegment may not see the same ArraySegment from one call to another
// (ie, users could assign a new value to the old location).
@IsReadOnly
@ClassIsDotNetStruct
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public final class ArraySegment<T>
    extends ValueType
    implements IList<T>, IReadOnlyList<T>
{
    private final @AllowNull T[] _array; // Do not rename (binary serialization)
    private final int _offset; // Do not rename (binary serialization)
    private final int _count; // Do not rename (binary serialization)

    // Implicit constructor definition required by ValueType definition.
    public ArraySegment()
    {
        _array = null;
        _offset = 0;
        _count = 0;
    }

    /**
     * Initializes a new instance of the {@link ArraySegment} structure that delimits all the elements in the specified array.
     * @param array The array to wrap.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public ArraySegment(T[] array)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");

        _array = array;
        _offset = 0;
        _count = array.length;
    }

    /**
     * Initializes a new instance of the {@link ArraySegment} structure that delimits the specified range of the elements in the specified array.
     * @param array The array containing the range of elements to delimit.
     * @param offset The zero-based index of the first element in the range.
     * @param count The number of elements in the range.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentException {@code offset} and {@code count} do not specify a valid range in {@code array}.
     * @throws ArgumentOutOfRangeException {@code offset} or {@code count} is less than 0.
     */
    public ArraySegment(T[] array, int offset, int count)
            throws ArgumentNullException, ArgumentException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value");
        } else if (offset < 0) {
            throw new ArgumentOutOfRangeException("offset", "Offset cannot be a negative value");
        } else if (offset > array.length) {
            throw new ArgumentOutOfRangeException("offset", "Offset cannot be a greater than the array size");
        } else if (count > (array.length - offset)) {
            throw new ArgumentException("The current selection of count and offset values does exceed the array's bounds.");
        } else {
            _array = array;
            _offset = offset;
            _count = count;
        }
    }

    /**
     * Gets the original array containing the range of elements that the array segment delimits.
     * @return The original array that was passed to the constructor, and that contains the range delimited by the {@link ArraySegment}.
     */
    @MaybeNull
    public T[] GetArray() { return _array; }

    @Override
    public int getCount() { return _count; }

    /**
     * Gets the number of elements in the range delimited by the array segment.
     * @return The number of elements in the range delimited by the {@link ArraySegment}.
     */
    public int GetCount() { return _count; }

    /**
     * Gets the position of the first element in the range delimited by the array segment, relative to the start of the original array.
     * @return The position of the first element in the range delimited by the {@link ArraySegment}, relative to the start of the original array.
     */
    public int GetOffset() { return _offset; }

    @Override
    // the indexer setter does not throw an exception although IsReadOnly is true.
    // This is to match the behavior of arrays.
    public boolean getIsReadOnly() { return true; }

    @Override
    public T getItem(int index)
    {
        ThrowInvalidOperationIfDefault();
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value");
        } else if (index >= _count) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be outside of the collection's bounds.");
        } else {
            // Throws NPE as needed.
            return _array[_offset + index];
        }
    }

    @Override
    public void setItem(int index, @AllowNull T item)
    {
        ThrowInvalidOperationIfDefault();
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value");
        } else if (index >= _count) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be outside of the collection's bounds.");
        } else {
            // Throws NPE as needed.
            _array[_offset + index] = item;
        }
    }

    @Override
    public int IndexOf(T item)
    {
        ThrowInvalidOperationIfDefault();
        int index = Array.IndexOf(_array, item, _offset, _count);
        return index >= 0 ? index - _offset : -1;
    }

    @Override
    public void Clear() { throw new NotSupportedException("Read-only collection"); }

    @Override
    public void Add(T item) { throw new NotSupportedException("Read-only collection"); }

    @Override
    public boolean Remove(T item) { throw new NotSupportedException("Read-only collection"); }

    @Override
    public void RemoveAt(int index) { throw new NotSupportedException("Read-only collection"); }

    @Override
    public void Insert(int index, T item) { throw new NotSupportedException("Read-only collection"); }

    @Override
    public boolean Contains(@AllowNull T item) { return IndexOf(item) > -1; }

    @Override
    public void CopyTo(T[] array, int arrayIndex)
    {
        ThrowInvalidOperationIfDefault();
        Array.Copy(_array, _offset, array, arrayIndex, _count);
    }

    /**
     * Copies the contents of this instance into the specified destination array of the same type {@link T}.
     * @param array The array of type {@link T} into which the contents of this instance will be copied.
     * @throws InvalidOperationException The underlying array of this instance is {@code null}.
     */
    public void CopyTo(T[] array) throws InvalidOperationException { CopyTo(array, 0); }

    /**
     * Copies the contents of this instance into the specified destination array segment of the same type {@link T}.
     * @param destination The array segment into which the contents of this instance will be copied.
     * @throws InvalidOperationException The underlying array of this instance is {@code null}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * The underlying array of {@code destination} is {@code null}.
     * @throws ArgumentException The length of the underlying array of this instance is larger than the length of the underlying array of {@code destination}.
     */
    public void CopyTo(ArraySegment<T> destination)
            throws InvalidOperationException, ArgumentException
    {
        ValueType.ValidateNonNullStructure(destination);
        ThrowInvalidOperationIfDefault();
        destination.ThrowInvalidOperationIfDefault();

        if (_count > destination._count)
        {
            // ThrowHelper.ThrowArgumentException_DestinationTooShort();
            throw new ArgumentException("Destination array segment is too short.");
        }

        Array.Copy(_array, _offset, destination._array, destination._offset, _count);
    }

    /**
     * Returns an enumerator that can be used to iterate through the array segment.
     * @return An enumerator that can be used to iterate through the array segment.
     * @throws InvalidOperationException The underlying array is {@code null}.
     */
    @Override
    public Enumerator<T> GetEnumerator()
        throws InvalidOperationException
    {
        ThrowInvalidOperationIfDefault();
        return new Enumerator<>(this);
    }

    /**
     * Returns the hash code for the current instance.
     * @return A 32-bit signed integer hash code.
     */
    @Override
    public int GetHashCode() { return _array == null ? 0 : HashCode.Combine(_offset, _count, Arrays.hashCode(_array)); }

    /**
     * Determines whether the specified {@link ArraySegment} structure is equal to the current instance.
     * @param obj The structure to compare with the current instance.
     * @return {@code true} if the specified {@link ArraySegment} structure is equal to the current instance; otherwise, {@code false}.
     * @apiNote Two {@link ArraySegment} objects are considered to be equal if all the following conditions are met:
     * <ul>
     *     <li>They reference the same array.</li>
     *     <li>They begin at the same index in the array.</li>
     *     <li>They have the same number of elements.</li>
     * </ul>
     */
    public boolean Equals(ArraySegment<T> obj)
    {
        ValueType.ValidateNonNullStructure(obj);
        return obj._array == _array && obj._offset == _offset && obj._count == _count;
    }

    /**
     * Determines whether the specified object is equal to the current instance.
     * @param obj The object to compare with the current instance.
     * @return {@code true} if the specified object is a {@link ArraySegment} structure and is equal to the current instance; otherwise, {@code false}.
     * @apiNote Two {@link ArraySegment} objects are considered to be equal if all the following conditions are met:
     * <ul>
     *     <li>They reference the same array.</li>
     *     <li>They begin at the same index in the array.</li>
     *     <li>They have the same number of elements.</li>
     * </ul>
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean Equals(@NotNullWhen(ReturnValue = true) @AllowNull Object obj) { return obj instanceof ArraySegment other && Equals((ArraySegment<T>) other); }

    /**
     * Forms a slice out of the current array segment starting at the specified index.
     * @param index The index at which to begin the slice.
     * @return An array segment that consists of all elements of the current array segment from index to the end of the array segment.
     * @throws InvalidOperationException The underlying array of this instance is {@code null}.
     * @throws ArgumentOutOfRangeException {@code index} is greater than the length of the underlying array of this instance.
     */
    @NotNull
    public ArraySegment<T> Slice(int index)
            throws InvalidOperationException, ArgumentOutOfRangeException
    {
        ThrowInvalidOperationIfDefault();
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value");
        } else if (index > _count) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be outside of the collection's bounds.");
        } else {
            return new ArraySegment<>(_array, _offset + index, _count - index);
        }
    }

    /**
     * Forms a slice of the specified length out of the current array segment starting at the specified index.
     * @param index The index at which to begin the slice.
     * @param count The desired length of the slice.
     * @return An array segment of {@code count} elements starting at {@code index}.
     * @throws InvalidOperationException The underlying array of this instance is {@code null}.
     * @throws ArgumentOutOfRangeException {@code index} is greater than the length of the underlying array of this instance. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code count} is greater than the length of the underlying array of this instance - {@code index}.
     */
    @NotNull
    public ArraySegment<T> Slice(int index, int count)
            throws InvalidOperationException, ArgumentOutOfRangeException
    {
        ThrowInvalidOperationIfDefault();

        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value");
        } else if (index > _count) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be outside of the collection's bounds.");
        } else if (count > (_count - index)) {
            throw new ArgumentException("The current selection of count and index values does exceed the segment's bounds.");
        } else {
            return new ArraySegment<>(_array, _offset + index, count);
        }
    }

    /**
     * Copies the contents of this array segment into a new array.
     * @return An array containing the data in the current array segment.
     * @throws InvalidOperationException {@code default(ArraySegment<T>)} cannot be converted to an array.
     */
    // This API can be actually supported; because _array cannot never be null,
    // so we always know the class of the array, so it's component type can be easily deduced.
    @NotNull // Or will throw InvalidOperationException when the array is null.
    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    public T[] ToArray()
        throws InvalidOperationException
    {
        ThrowInvalidOperationIfDefault();
        Object array = Array.CreateInstance(_array.getClass().componentType(), _count);
        System.arraycopy(_array, _offset, array, 0, _count);
        return (T[])array;
    }

    @StackTraceHidden
    private void ThrowInvalidOperationIfDefault()
    {
        if (_array == null)
        {
            throw new InvalidOperationException("A null array has been provided.");
        }
    }

    /**
     * Provides an enumerator for the elements of an {@link ArraySegment}.
     * @param <T> The type of the elements in the array segment.
     */
    @ClassIsDotNetStruct
    public static final class Enumerator<T>
        extends ValueType
        implements IEnumerator<T>
    {
        private final @AllowNull T[] _array;
        private final int _start;
        private final int _end; // cache Offset + Count, since it's a little slow
        private int _current;

        public Enumerator()
        {
            _array = null;
            _start = 0;
            _end = 0;
            _current = 0;
        }

        private Enumerator(ArraySegment<T> segment)
        {
            _array = segment._array;
            _end = (_start = segment._offset) + segment._count;
            _current = _start - 1;
        }

        @Override
        public T getCurrent()
        {
            if (_current < _start) {
                // ThrowHelper.ThrowInvalidOperationException_InvalidOperation_EnumNotStarted();
                throw new InvalidOperationException("Enumeration has not been started yet.");
            } else if (_current >= _end) {
                // ThrowHelper.ThrowInvalidOperationException_InvalidOperation_EnumEnded();
                throw new InvalidOperationException("Enumeration has been finished.");
            } else {
                return _array[_current];
            }
        }

        @Override
        public void Reset() throws InvalidOperationException { _current = _start - 1; }

        @Override
        public boolean MoveNext() throws InvalidOperationException { return ++_current < _end; }

        @Override
        public void Dispose() { }
    }
}
