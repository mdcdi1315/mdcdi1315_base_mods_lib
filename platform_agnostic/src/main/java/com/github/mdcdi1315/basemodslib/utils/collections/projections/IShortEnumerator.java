package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Provides the base interface for primitive {@code short} integer enumerators. <br />
 * Extends from the {@link IEnumerator} interface.
 */
public interface IShortEnumerator
        extends IEnumerator<Short>
{
    /**
     * Gets the element in the collection at the current position of the enumerator as an unboxed {@code short} integer.
     * @return The value of the {@link #getCurrent()} method, but the {@code short} integer is unboxed.
     */
    default short getUncastedCurrent() { return getCurrent(); }
}
