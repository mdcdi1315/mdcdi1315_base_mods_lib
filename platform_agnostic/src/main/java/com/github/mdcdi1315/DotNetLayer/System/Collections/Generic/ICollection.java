package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

/**
 * Defines methods to manipulate generic collections.
 * @param <T> The type of the elements in the collection.
 */
public interface ICollection<T>
    extends IEnumerable<T>
{
    /**
     * Gets the number of elements contained in the {@link ICollection}.
     * @return The number of elements contained in the {@link ICollection}.
     */
    int getCount();

    /**
     * Gets a value indicating whether the {@link ICollection} is read-only.
     * @return true if the {@link ICollection} is read-only; otherwise, false.
     */
    boolean getIsReadOnly();

    /**
     * Adds an item to the {@link ICollection}.
     * @param item The object to add to the {@link ICollection}.
     * @exception com.github.mdcdi1315.DotNetLayer.System.NotSupportedException The {@link ICollection} is read-only.
     */
    void Add(T item);

    /**
     *  Removes all items from the {@link ICollection}.
     *  @exception com.github.mdcdi1315.DotNetLayer.System.NotSupportedException The {@link ICollection} is read-only.
     */
    void Clear();

    /**
     * Determines whether the {@link ICollection} contains a specific value.
     * @param item The object to locate in the {@link ICollection}.
     * @return true if item is found in the {@link ICollection}; otherwise, false.
     */
    boolean Contains(T item);

    /**
     * Copies the elements of the {@link ICollection} to an array, starting at a particular array index.
     * @param array The one-dimensional System.Array that is the destination of the elements copied from {@link ICollection}. The array must have zero-based indexing.
     * @param arrayIndex The zero-based index in `array` at which copying begins.
     * @exception com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException array is null.
     * @exception com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException arrayIndex is less than 0.
     * @exception com.github.mdcdi1315.DotNetLayer.System.ArgumentException The number of elements in the source
     * {@link ICollection} is greater than the available space from arrayIndex to the end of the destination array.
     */
    void CopyTo(T[] array, int arrayIndex);

    /**
     * Removes the first occurrence of a specific object from the {@link ICollection}.
     * @param item The object to remove from the {@link ICollection}.
     * @return true if item was successfully removed from the {@link ICollection}; otherwise, false.
     * This method also returns false if item is not found in the original {@link ICollection}.
     * @exception com.github.mdcdi1315.DotNetLayer.System.NotSupportedException The {@link ICollection} is read-only.
     */
    boolean Remove(T item);
}
