package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Provides the base interface for primitive double-precision floating-point integer enumerators. <br />
 * Extends from the {@link IEnumerator} interface.
 */
public interface IDoubleEnumerator
        extends IEnumerator<Double>
{
    /**
     * Gets the element in the collection at the current position of the enumerator as an unboxed double-precision floating-point integer.
     * @return The value of the {@link #getCurrent()} method, but the double-precision floating-point integer is unboxed.
     */
    default double getUncastedCurrent() { return getCurrent(); }
}
