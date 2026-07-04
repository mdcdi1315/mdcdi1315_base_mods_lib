package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Provides the base interface for primitive character enumerators. <br />
 * Extends from the {@link IEnumerator} interface.
 */
public interface ICharEnumerator
    extends IEnumerator<Character>
{
    /**
     * Gets the element in the collection at the current position of the enumerator as an unboxed character.
     * @return The value of the {@link #getCurrent()} method, but the character is unboxed.
     */
    default char getUncastedCurrent() { return getCurrent(); }
}
