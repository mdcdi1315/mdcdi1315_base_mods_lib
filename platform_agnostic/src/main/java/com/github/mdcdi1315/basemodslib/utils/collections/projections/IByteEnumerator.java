package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Provides the base interface for primitive {@code byte} integer enumerators. <br />
 * Extends from the {@link IEnumerator} interface.
 */
public interface IByteEnumerator
        extends IEnumerator<Byte>
{
    /**
     * Gets the element in the collection at the current position of the enumerator as an unboxed {@code byte} integer.
     * @return The value of the {@link #getCurrent()} method, but the {@code byte} integer is unboxed.
     */
    default byte getUncastedCurrent() { return getCurrent(); }
}