package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.OverflowException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

/**
 * Provides common services to all the collection types that are implemented using fixed arrays.
 * @since 1.0.19
 */
public interface IArrayBasedCollection
{
    /**
     * Removes the elements that cannot be accessed by any means through the public members of the type to reclaim memory. <br />
     * This is done by resizing the backed array.
     */
    void TrimExcess();

    /**
     * Attempts to ensure to the current object that {@code n_elements} elements are existing and empty. <br />
     * If not, the backing array must be appropriately resized.
     * @param n_elements The number of elements needed to be ensured of.
     * @throws OverflowException Adding {@code n_elements} would cause the collection to overflow.
     * @throws ArgumentOutOfRangeException {@code n_elements} is negative.
     */
    void EnsureCapacity(int n_elements) throws OverflowException, ArgumentOutOfRangeException;
}