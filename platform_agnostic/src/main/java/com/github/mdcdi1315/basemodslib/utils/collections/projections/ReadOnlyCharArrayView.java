package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.ISupportsCloning;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.CollectionHelpers;

/**
 * Provides a read-only view of a {@code char} array as an {@link ICharEnumerable} instance. <br />
 * It does also expose the {@link ITraversableCollection} interface for accessing individual
 * elements + the length of the provided array.
 */
public final class ReadOnlyCharArrayView
    implements ICharEnumerable,
        ISupportsCloning<Character>,
        ITraversableCollection<Character>,
        ISynchronized
{
    private final char[] array;
    private final int start, bound;

    /**
     * Initializes a new instance of the {@link ReadOnlyCharArrayView} class.
     * @param array The array to project as a {@link ICharEnumerable} instance.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public ReadOnlyCharArrayView(char[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        this.start = 0;
        this.array = array;
        this.bound = array.length;
    }

    /**
     * Initializes a new instance of the {@link ReadOnlyCharArrayView} class,
     * from the specified portion of the target array.
     * @param array The array to initialize the class from.
     * @param offset The offset in {@code array} that the array's elements will be viewable from.
     * @param count The number of elements in {@code array} that will be viewable.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentException {@code offset + count} exceed the array's bounds.
     * @throws ArgumentOutOfRangeException {@code offset} and/or {@code count} are less than zero.
     */
    public ReadOnlyCharArrayView(char[] array, int offset, int count)
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
    public Character GetItem(int index)
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
    public char GetUnboxedItem(int index)
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
    public ReadOnlyCharArrayView Clone() { return new ReadOnlyCharArrayView(this.array, this.start, GetCount()); }

    @Pure
    @NotNull
    @Override
    public CharArrayEnumerator GetEnumerator() { return new CharArrayEnumerator(this.array, start, GetCount()); }

    @NotNull
    @Override
    public ReadOnlyCharArrayView Slice(int count) throws ArgumentException { return new ReadOnlyCharArrayView(array, start, count); }

    @NotNull
    @Override
    public ReadOnlyCharArrayView Slice(int index, int count) throws ArgumentException { return new ReadOnlyCharArrayView(array, start + index, count); }

    @NotNull
    @Override
    public String toString()
    {
        int len = GetCount();

        return StringUtils.Concat(
                "ReadOnlyShortArrayView (",
                CollectionHelpers.GetStringSafe(len),
                ") ",
                CollectionHelpers.PutArrayContentsToString(array, start, len)
        );
    }
}
