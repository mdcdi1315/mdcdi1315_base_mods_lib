package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.CollectionHelpers;

/**
 * Provides an {@link ILongEnumerator} implementation for concrete long arrays. <br />
 * Note that this enumerator returns the elements in the reverse order than they are declared. <br />
 * For arrays that are of the {@link Long} boxed type, use the {@link com.github.mdcdi1315.basemodslib.utils.collections.ReversedArrayEnumerator} class instead.
 * @since 1.0.38
 */
public final class ReversedLongArrayEnumerator
    extends BaseLongEnumerator
{
    private int index;
    private long[] array;
    private final int start, bound;

    /**
     * Initializes a new instance of the {@link ReversedLongArrayEnumerator} class.
     * @param array The array to initialize the reversed long array enumerator from.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public ReversedLongArrayEnumerator(long[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        this.bound = -1;
        this.array = array;
        this.start = this.index = array.length;
    }

    /**
     * Initializes a new instance of the {@link ReversedLongArrayEnumerator} class.
     * @param array The array to initialize the reversed long array enumerator from.
     * @param index The index to start enumerating items from the array.
     * @param count The number of items that this enumerator will return.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     * @throws ArgumentException {@code index} + {@code count} value is greater than the array's bounds.
     */
    public ReversedLongArrayEnumerator(long[] array, int index, int count)
            throws ArgumentNullException, ArgumentOutOfRangeException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else {
            CollectionHelpers.CheckIndexCountInsideCollectionBound(index, count, array.length);
            this.array = array;
            this.index = this.start = (this.bound = (index - 1)) + count;
        }
    }

    @Pure
    @NotNull
    @Override
    public Long getCurrent() { return array[index]; }

    @Pure
    @Override
    protected void ResetImpl() { index = this.start; }

    @Pure
    @Override
    public long getUncastedCurrent() { return array[index]; }

    @Pure
    @Override
    protected boolean MoveNextImpl() { return index-- > bound; }

    @Pure
    @Override
    public void Dispose() { super.Dispose(); index = start; array = null; }
}
