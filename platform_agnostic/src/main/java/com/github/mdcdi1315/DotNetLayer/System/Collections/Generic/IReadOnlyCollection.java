package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

/**
 * Represents a strongly-typed, read-only collection of elements.
 * @param <T> The type of the elements.
 */
public interface IReadOnlyCollection<T>
        extends IEnumerable<T>
{
    /**
     * Gets the number of elements in the collection.
     * @return The number of elements in the collection.
     */
    int getCount();
}