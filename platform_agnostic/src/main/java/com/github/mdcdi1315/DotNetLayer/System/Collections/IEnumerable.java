package com.github.mdcdi1315.DotNetLayer.System.Collections;

/**
 * Exposes an enumerator, which supports a simple iteration over a non-generic collection.
  */
public interface IEnumerable
{
    /**
     * Returns an enumerator that iterates through a collection.
     * @return An {@link IEnumerator} object that can be used to iterate through the collection.
     */
    IEnumerator GetEnumerator();
}