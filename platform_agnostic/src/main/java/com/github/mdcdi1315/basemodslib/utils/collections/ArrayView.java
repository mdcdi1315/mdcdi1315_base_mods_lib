package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IReadOnlyList;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Provides an {@link IList} implementation for Java arrays. <br />
 * Conventionally called as array view since the array is projected as a {@link IList} object. <br />
 * (As such, any changes performed to the array will be visible to the {@link ArrayView}, and vice versa).
 * @param <T> The type of the elements the array does provide.
 * @apiNote While the {@link #getIsReadOnly()} method reports {@code false}, any method that would modify the array size (Addition, insertion, removal of element) does throw a {@link NotSupportedException}. <br />
 * The {@link #Clear()} method instead of removing all the array elements, it assigns {@code null} to them by using the {@link Array#Clear(Object[], int, int)} method to the array. <br />
 * Additionally, the {@link #getItem(int)} and {@link #setItem(int, Object)} methods do throw {@link IndexOutOfRangeException} if an out-of-array bounds index is provided to them.
 * @since 1.0.31
 */
public class ArrayView<T>
    extends BaseEnumerable<T>
    implements IList<T>, IReadOnlyList<T>, ITraversableCollection<T>, ISupportsCloning<T>
{
    private final T[] array;

    /**
     * Instantiates the {@link ArrayView} class from the specified array.
     * @param array The array to project.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public ArrayView(T[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.array = array, "array");
    }

    @Override
    @MaybeNull
    public T getItem(int index)
    {
        try {
            return array[index];
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfRangeException(e.getMessage());
        }
    }

    @Override
    public void setItem(int index, @AllowNull T item)
    {
        try {
            array[index] = item;
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfRangeException(e.getMessage());
        }
    }

    @Override
    @MaybeNull
    public T GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        try {
            return array[index];
        } catch (IndexOutOfBoundsException e) {
            throw new ArgumentOutOfRangeException("index", e.getMessage());
        }
    }

    @Override
    public int GetCount() { return array.length; }

    @Override
    public int getCount() { return array.length; }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    public boolean Contains(T item) { return IndexOf(item) > -1; }

    @Override
    public int IndexOf(T item) { return Array.IndexOf(array, item); }

    @Override
    public void Clear() { Array.Clear(array, 0, array.length); }

    @Override
    public IEnumerator<T> GetEnumerator() { return ArrayEnumerator.Of(this.array); }

    @Override
    public void CopyTo(T[] array, int arrayIndex) { Array.Copy(this.array, 0, array, arrayIndex, this.array.length); }

    // Terminally unsupported methods

    @Override
    public void Add(T item)
    {
        throw new NotSupportedException("Adding elements is not supported on array view objects");
    }

    @Override
    public void Insert(int index, T item)
    {
        throw new NotSupportedException("Insertion is not supported on array view objects");
    }

    @Override
    public void RemoveAt(int index)
    {
        throw new NotSupportedException("Removal is not supported on array view objects");
    }

    @Override
    public boolean Remove(T item)
    {
        throw new NotSupportedException("Removal is not supported on array view objects");
    }

    // Extensioned implementations

    private static <T> T[] CreateArray(T[] array, int index, int count)
    {
        T[] target = (T[]) Array.CreateInstance(array.getClass().componentType(), count);
        System.arraycopy(array, index, target, 0, count);
        return target;
    }

    /**
     * {@inheritDoc}
     * @apiNote Note, although that the {@link ArrayView} class is intended to project a Java array,
     * cloning and/or slicing instances of does detach it from the array, as an array copy must be performed.
     */
    @Override
    public ArrayView<T> Slice(int index, int count)
            throws ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (index + count > array.length) {
            throw new ArgumentException("Index and count parameters exceed the backing array length.");
        } else {
            return new ArrayView<>(CreateArray(array, index, count));
        }
    }

    /**
     * {@inheritDoc}
     * @apiNote Note, although that the {@link ArrayView} class is intended to project a Java array,
     * cloning and/or slicing instances of does detach it from the array, as an array copy must be performed.
     */
    @Override
    public ArrayView<T> Slice(int count) throws ArgumentException { return Slice(0, count); }

    /**
     * {@inheritDoc}
     * @apiNote Note, although that the {@link ArrayView} class is intended to project a Java array,
     * cloning and/or slicing instances of does detach it from the array, as an array copy must be performed.
     */
    @Override
    public ArrayView<T> Clone() { return new ArrayView<>(CreateArray(array, 0, array.length)); }
}
