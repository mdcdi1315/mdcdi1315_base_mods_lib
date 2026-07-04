package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Provides the base interface for primitive {@code long} integer enumerators. <br />
 * Extends from the {@link IEnumerator} interface.
 */
public interface ILongEnumerator
    extends IEnumerator<Long>
{
    /**
     * Gets the element in the collection at the current position of the enumerator as an unboxed {@code long} integer.
     * @return The value of the {@link #getCurrent()} method, but the {@code long} integer is unboxed.
     */
    default long getUncastedCurrent() { return getCurrent(); }
}
