package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Provides the base interface for primitive integer enumerators. <br />
 * Extends from the {@link IEnumerator} interface.
 */
public interface IIntEnumerator
    extends IEnumerator<Integer>
{
    /**
     * Gets the element in the collection at the current position of the enumerator as an unboxed integer.
     * @return The value of the {@link #getCurrent()} method, but the integer is unboxed.
     */
    default int getUncastedCurrent() { return getCurrent(); }
}
