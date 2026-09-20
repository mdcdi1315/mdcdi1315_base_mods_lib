package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.ISupportsCloning;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.CollectionHelpers;

/**
 * Provides a read-only view of a {@code double} array as an {@link IDoubleEnumerable} instance. <br />
 * It does also expose the {@link ITraversableCollection} interface for accessing individual
 * elements + the length of the provided array.
 */
public final class ReadOnlyDoubleArrayView
    implements IDoubleEnumerable,
        ISupportsCloning<Double>,
        ITraversableCollection<Double>,
        ISynchronized
{
    private final double[] array;
    private final int start, bound;

    /**
     * Initializes a new instance of the {@link ReadOnlyDoubleArrayView} class.
     * @param array The array to project as a {@link IDoubleEnumerable} instance.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public ReadOnlyDoubleArrayView(double[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        this.start = 0;
        this.array = array;
        this.bound = array.length;
    }

    /**
     * Initializes a new instance of the {@link ReadOnlyDoubleArrayView} class,
     * from the specified portion of the target array.
     * @param array The array to initialize the class from.
     * @param offset The offset in {@code array} that the array's elements will be viewable from.
     * @param count The number of elements in {@code array} that will be viewable.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentException {@code offset + count} exceed the array's bounds.
     * @throws ArgumentOutOfRangeException {@code offset} and/or {@code count} are less than zero.
     */
    public ReadOnlyDoubleArrayView(double[] array, int offset, int count)
            throws ArgumentNullException, ArgumentOutOfRangeException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (offset < 0) {
            throw new ArgumentOutOfRangeException("offset", "Offset cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else {
            CollectionHelpers.CheckIndexCountInsideCollectionBound(offset, count, array.length);
            this.array = array;
            this.bound = (this.start = offset) + count;
        }
    }

    @NotNull
    @Override
    public Double GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        int base_line_index = start + index;
        if (base_line_index >= bound || index < start) {
            throw new ArgumentOutOfRangeException("index", "Index exceeds the array's bounds.");
        } else {
            return array[index];
        }
    }

    /**
     * Performs similarly as the {@link #GetItem(int)} method, but returns the value unboxed.
     * @param index The position of the element to get.
     * @return The element at {@code index}.
     * @throws IndexOutOfRangeException {@code index} is negative -or- is outside the array's bounds.
     */
    public double GetUnboxedItem(int index)
            throws IndexOutOfRangeException
    {
        int base_line_index = start + index;
        if (base_line_index >= bound || index < start) {
            throw new IndexOutOfRangeException("Index exceeds the array's bounds.");
        } else {
            return array[index];
        }
    }

    @Pure
    @Override
    public int GetCount() { return bound - start; }

    /**
     * Creates a new clone of this {@link ReadOnlyByteArrayView} object, inheriting the
     * same array, as well as the bounds that the current object has been defined.
     * @return A new instance of the {@link ReadOnlyByteArrayView} class.
     */
    @Pure
    @NotNull
    @Override
    public ReadOnlyDoubleArrayView Clone() { return new ReadOnlyDoubleArrayView(this.array, this.start, GetCount()); }

    @Pure
    @NotNull
    @Override
    public DoubleArrayEnumerator GetEnumerator() { return new DoubleArrayEnumerator(this.array, start, GetCount()); }

    @NotNull
    @Override
    public ReadOnlyDoubleArrayView Slice(int count) throws ArgumentException { return new ReadOnlyDoubleArrayView(array, start, count); }

    @NotNull
    @Override
    public ReadOnlyDoubleArrayView Slice(int index, int count) throws ArgumentException { return new ReadOnlyDoubleArrayView(array, start + index, count); }

    @NotNull
    @Override
    public String toString()
    {
        int len = GetCount();

        return StringUtils.Concat(
                "ReadOnlyDoubleArrayView (",
                CollectionHelpers.GetStringSafe(len),
                ") ",
                CollectionHelpers.PutArrayContentsToString(array, start, len)
        );
    }
}
