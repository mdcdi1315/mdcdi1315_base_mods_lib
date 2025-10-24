package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

/**
 * Exposes the enumerator, which supports a simple iteration over a collection of a specified type.
 * @param <T> The type of objects to enumerate.
 */
public interface IEnumerable<T>
    extends com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerable
{
    /**
     * Returns an enumerator that iterates through the collection.
     * @return An enumerator that can be used to iterate through the collection.
     */
    IEnumerator<T> GetEnumerator();
}
