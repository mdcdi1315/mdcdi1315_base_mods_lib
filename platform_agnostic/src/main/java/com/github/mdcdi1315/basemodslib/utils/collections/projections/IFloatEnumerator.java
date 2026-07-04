package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Provides the base interface for primitive single-precision floating-point integer enumerators. <br />
 * Extends from the {@link IEnumerator} interface.
 */
public interface IFloatEnumerator
        extends IEnumerator<Float>
{
    /**
     * Gets the element in the collection at the current position of the enumerator as an unboxed single-precision floating-point integer.
     * @return The value of the {@link #getCurrent()} method, but the single-precision floating-point integer is unboxed.
     */
    default float getUncastedCurrent() { return getCurrent(); }
}