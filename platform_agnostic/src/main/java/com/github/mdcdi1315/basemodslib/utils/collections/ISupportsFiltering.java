package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

/**
 * Provides a declaration for collections that return an instance of the same type,
 * but they are able to return a 'filtered' instance that contains only those
 * elements that pass a specified predicate.
 * @param <T> The type of the elements in the implemented collection.
 * @since 1.0.26
 */
public interface ISupportsFiltering<T>
    extends IEnumerable<T>
{
    /**
     * Filters the elements of the current collection and returns all the items that pass the predicate.
     * @param predicate The {@link Predicate} to filter the elements of the current collection by.
     * @return A new collection containing only the elements that pass the {@link Predicate}.
     * @throws ArgumentNullException {@code predicate} is {@code null}.
     */
    ISupportsFiltering<T> FilterBy(Predicate<T> predicate) throws ArgumentNullException;
}
