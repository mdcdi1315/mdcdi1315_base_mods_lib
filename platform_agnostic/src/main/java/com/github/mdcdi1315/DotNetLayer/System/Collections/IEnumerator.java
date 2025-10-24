package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

/**
 * Supports a simple iteration over a non-generic collection.
 */
public interface IEnumerator
{
    /**
     * Gets the element in the collection at the current position of the enumerator.
     * @return The element in the collection at the current position of the enumerator.
     */
    Object getCurrent();

    /**
     * Advances the enumerator to the next element of the collection.
     * @return true if the enumerator was successfully advanced to the next element; false if the enumerator has passed the end of the collection.
     * @exception InvalidOperationException The collection was modified after the enumerator was created.
     */
    boolean MoveNext();

    /**
     * Sets the enumerator to its initial position, which is before the first element in the collection.
     * @exception InvalidOperationException The collection was modified after the enumerator was created.
     */
    void Reset();
}
