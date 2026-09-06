package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * Provides an {@link IShortEnumerator} implementation for concrete short arrays. <br />
 * For arrays that are of the {@link Short} boxed type, use the {@link com.github.mdcdi1315.basemodslib.utils.collections.ArrayEnumerator} class instead.
 */
@Pure
public final class ShortArrayEnumerator
    extends BaseShortEnumerator
{
    private int index;
    private short[] array;
    private final int start, bound;

    /**
     * Initializes a new instance of the {@link ShortArrayEnumerator} class.
     * @param array The array to initialize the short array enumerator from.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public ShortArrayEnumerator(short[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        this.array = array;
        this.bound = array.length;
        this.start = this.index = -1;
    }

    /**
     * Initializes a new instance of the {@link ShortArrayEnumerator} class.
     * @param array The array to initialize the short array enumerator from.
     * @param index The index to start enumerating items from the array.
     * @param count The number of items that this enumerator will return.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     * @throws ArgumentException {@code index} + {@code count} value is greater than the array's bounds.
     */
    public ShortArrayEnumerator(short[] array, int index, int count)
            throws ArgumentNullException, ArgumentOutOfRangeException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if ((this.bound = index + count) > array.length) {
            throw new ArgumentException("Specified index and count parameters are out of the given array bounds.");
        } else {
            this.array = array;
            this.start = this.index = index - 1;
        }
    }

    @Pure
    @NotNull
    @Override
    public Short getCurrent() { return array[index]; }

    @Pure
    @Override
    public short getUncastedCurrent() { return array[index]; }

    @Pure
    @Override
    protected void ResetImpl() { index = start; }

    @Pure
    @Override
    protected boolean MoveNextImpl() { return ++index < bound; }

    @Pure
    @Override
    public void Dispose() { super.Dispose(); index = start; array = null; }
}
