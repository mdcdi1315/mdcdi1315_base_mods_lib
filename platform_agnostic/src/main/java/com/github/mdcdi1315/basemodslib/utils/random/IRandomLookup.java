package com.github.mdcdi1315.basemodslib.utils.random;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.util.Optional;

/**
 * Provides a way to get multiple random elements from an enumerable collection.
 * @param <T> The type of the element that the collection holds.
 */
public interface IRandomLookup<T>
{
    /**
     * Gets the collection that contains the elements.
     * @return The original collection where random elements are picked from.
     */
    @NotNull
    IEnumerable<T> GetCollection();

    /**
     * Gets a random element from the elements of the collection.
     * @return A random element selected by the elements contained in
     * the return value of the {@link #GetCollection()} method.
     */
    @NotNull
    Optional<T> GetRandomElement();

    /**
     * Gets the random source that is used to get random elements.
     * @return The random source.
     */
    @NotNull
    IRandomSource GetRandomSource();
}
