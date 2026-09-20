package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.CollectionHelpers;

/**
 * Provides an {@link IFloatEnumerator} implementation for concrete single-precision floating-point arrays. <br />
 * Note that this enumerator returns the elements in the reverse order than they are declared. <br />
 * For arrays that are of the {@link Float} boxed type, use the {@link com.github.mdcdi1315.basemodslib.utils.collections.ReversedArrayEnumerator} class instead.
 * @since 1.0.38
 */
public final class ReversedFloatArrayEnumerator
    extends BaseFloatEnumerator
{
    private int index;
    private float[] array;
    private final int start, bound;

    /**
     * Initializes a new instance of the {@link ReversedFloatArrayEnumerator} class.
     * @param array The array to initialize the reversed single-precision floating-point array enumerator from.
     * @throws ArgumentNullException {@code array} is {@code null}.
     */
    public ReversedFloatArrayEnumerator(float[] array)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        this.bound = -1;
        this.array = array;
        this.start = this.index = array.length;
    }

    /**
     * Initializes a new instance of the {@link ReversedFloatArrayEnumerator} class.
     * @param array The array to initialize the reversed single-precision floating-point array enumerator from.
     * @param index The index to start enumerating items from the array.
     * @param count The number of items that this enumerator will return.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     * @throws ArgumentException {@code index} + {@code count} value is greater than the array's bounds.
     */
    public ReversedFloatArrayEnumerator(float[] array, int index, int count)
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
    @Override
    protected void ResetImpl() { index = this.start; }

    @Pure
    @NotNull
    @Override
    public Float getCurrent() { return array[index]; }

    @Pure
    @Override
    public float getUncastedCurrent() { return array[index]; }

    @Pure
    @Override
    protected boolean MoveNextImpl() { return index-- > bound; }

    @Pure
    @Override
    public void Dispose() { super.Dispose(); index = start; array = null; }
}
