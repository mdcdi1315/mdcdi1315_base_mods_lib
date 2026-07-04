package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.IndexOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.ISupportsCloning;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;

/**
 * Provides a read-only view of a {@code int} array as an {@link IIntEnumerable} instance. <br />
 * It does also expose the {@link ITraversableCollection} interface for accessing individual
 * elements + the length of the provided array.
 */
public final class ReadOnlyIntArrayView
    implements IIntEnumerable,
        ISupportsCloning<Integer>,
        ITraversableCollection<Integer>,
        ISynchronized
{
    private final int[] array;
    private final int start, bound;

    /**
     * Initializes a new instance of the {@link ReadOnlyIntArrayView} class.
     * @param array The array to project as a {@link IIntEnumerable} instance.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public ReadOnlyIntArrayView(int[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        this.start = 0;
        this.array = array;
        this.bound = array.length;
    }

    /**
     * Initializes a new instance of the {@link ReadOnlyIntArrayView} class,
     * from the specified portion of the target array.
     * @param array The array to initialize the class from.
     * @param offset The offset in {@code array} that the array's elements will be viewable from.
     * @param count The number of elements in {@code array} that will be viewable.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentException {@code offset + count} exceed the array's bounds.
     * @throws ArgumentOutOfRangeException {@code offset} and/or {@code count} are less than zero.
     */
    public ReadOnlyIntArrayView(int[] array, int offset, int count)
            throws ArgumentNullException, ArgumentOutOfRangeException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (offset < 0) {
            throw new ArgumentOutOfRangeException("offset", "Offset cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if ((this.bound = offset + count) > array.length) {
            throw new ArgumentException("Specified offset and count parameters are out of the given array bounds.");
        } else {
            this.array = array;
            this.start = offset;
        }
    }

    @NotNull
    @Override
    public Integer GetItem(int index)
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
    public int GetUnboxedItem(int index)
            throws IndexOutOfRangeException
    {
        int base_line_index = start + index;
        if (base_line_index >= bound || index < start) {
            throw new IndexOutOfRangeException("Index exceeds the array's bounds.");
        } else {
            return array[index];
        }
    }

    @NotNull
    @Override
    public String toString()
    {
        int len = GetCount();

        StringBuilder sb = new StringBuilder("ReadOnlyShortArrayView (")
                .append(len)
                .append(") { ");

        for (int I = 0; I < len; I++)
        {
            sb.append(array[start + I]);
            if ((I + 1) < len) { sb.append(", "); }
        }

        return sb.append(" }").toString();
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
    public ReadOnlyIntArrayView Clone() { return new ReadOnlyIntArrayView(this.array, this.start, GetCount()); }

    @Pure
    @NotNull
    @Override
    public IntArrayEnumerator GetEnumerator() { return new IntArrayEnumerator(this.array, start, bound - start); }

    @NotNull
    @Override
    public ReadOnlyIntArrayView Slice(int count) throws ArgumentException { return new ReadOnlyIntArrayView(array, start, count); }

    @NotNull
    @Override
    public ReadOnlyIntArrayView Slice(int index, int count) throws ArgumentException { return new ReadOnlyIntArrayView(array, start + index, count); }
}
