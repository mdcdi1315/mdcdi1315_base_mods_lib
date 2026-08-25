package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Specifies the base interface for traversable collections; that is, collections that their individual elements can be accessed by simple 32-bit integers.
 * @param <T> The type of the elements to be traversed.
 * @since 1.0.18
 */
public interface ITraversableCollection<T>
    extends IEnumerable<T>
{
    /**
     * Gets the number of elements contained in the current traversable collection.
     * @return The number of elements contained in the current traversable collection.
     */
    int GetCount();

    /**
     * Gets the specified item at the specified index.
     * @param index The item located at {@code index}.
     * @return The item's value at {@code index}.
     * @throws ArgumentOutOfRangeException {@code index} is negative or outside the current collection bounds.
     */
    @MaybeNull
    T GetItem(int index) throws ArgumentOutOfRangeException;
}
