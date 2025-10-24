package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

/**
 * Represents a collection of objects that can be individually accessed by index.
 * @param <T> The type of elements in the list.
 */
// [DefaultMember("Item")]
public interface IList<T>
        extends ICollection<T>, IEnumerable<T>
{
    // Original reference: T this[int index] { get; set; }

    /**
     * Gets the element at the specified index.
     * @param index The zero-based index of the element to get.
     * @return The element at the specified index.
     * @exception com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException index is not a valid index in the {@link IList}.
     */
    T getItem(int index);

    /**
     * Sets the element at the specified index.
     * @param index The zero-based index of the element to set.
     * @param item The element to set at `index`.
     * @exception com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException index is not a valid index in the {@link IList}.
     * @exception com.github.mdcdi1315.DotNetLayer.System.NotSupportedException The {@link IList} is read-only.
     */
    void setItem(int index , T item);

    /**
     * Determines the index of a specific item in the {@link IList}.
     * @param item The object to locate in the {@link IList}.
     * @return The index of item if found in the list; otherwise, -1.
     */
    int IndexOf(T item);

    /**
     * Inserts an item to the {@link IList} at the specified index.
     * @param index The zero-based index at which item should be inserted.
     * @param item The object to insert into the {@link IList}.
     * @exception com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException index is not a valid index in the {@link IList}.
     * @exception com.github.mdcdi1315.DotNetLayer.System.NotSupportedException The {@link IList} is read-only.
     */
    void Insert(int index, T item);

    /**
     * Removes the {@link IList} item at the specified index.
     * @param index The zero-based index of the item to remove.
     * @exception com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException index is not a valid index in the {@link IList}.
     * @exception com.github.mdcdi1315.DotNetLayer.System.NotSupportedException The {@link IList} is read-only.
     */
    void RemoveAt(int index);
}