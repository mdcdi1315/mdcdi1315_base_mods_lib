package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.NullReferenceException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Represents a non-generic collection of objects that can be individually accessed by index.
 */
public interface IList
    extends ICollection
{
    /**
     * Gets a value indicating whether the {@link IList} has a fixed size.
     * @return {@code true} if the {@link IList} has a fixed size; otherwise, {@code false}.
     */
    boolean GetIsFixedSize();

    /**
     * Gets a value indicating whether the {@link IList} is read-only.
     * @return {@code true} if the {@link IList} is read-only; otherwise, {@code false}.
     */
    boolean GetIsReadOnly();

    /**
     * Adds an item to the {@link IList}.
     * @param value The object to add to the {@link IList}.
     * @return The position into which the new element was inserted, or -1 to indicate that the item was not inserted into the collection.
     * @throws NotSupportedException The {@link IList} is read-only. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * The {@link IList} has a fixed size.
     */
    int Add(@AllowNull Object value) throws NotSupportedException;

    /**
     * Removes all items from the {@link IList}.
     * @throws NotSupportedException The {@link IList} is read-only.
     */
    void Clear() throws NotSupportedException;

    /**
     * Determines whether the {@link IList} contains a specific value.
     * @param value The object to locate in the {@link IList}.
     * @return {@code true} if the {@link Object} is found in the {@link IList}; otherwise, {@code false}.
     */
    boolean Contains(@AllowNull Object value);

    /**
     * Determines the index of a specific item in the {@link IList}.
     * @param value The object to locate in the {@link IList}.
     * @return The index of {@code value} if found in the list; otherwise, -1.
     */
    int IndexOf(@AllowNull Object value);

    /**
     * Inserts an item to the {@link IList} at the specified index.
     * @param index The zero-based index at which {@code value} should be inserted.
     * @param value The object to insert into the {@link IList}.
     * @throws ArgumentOutOfRangeException {@code index} is not a valid index in the {@link IList}.
     * @throws NotSupportedException The {@link IList} is read-only. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * The {@link IList} has a fixed size.
     * @throws NullReferenceException {@code value} is {@code null} reference in the {@link IList}.
     */
    void Insert(int index, @AllowNull Object value) throws ArgumentOutOfRangeException, NotSupportedException, NullReferenceException;

    /**
     * Removes the first occurrence of a specific object from the {@link IList}.
     * @param value The object to remove from the {@link IList}.
     * @throws NotSupportedException The {@link IList} is read-only. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * The {@link IList} has a fixed size.
     */
    void Remove(@AllowNull Object value) throws NotSupportedException;

    /**
     * Removes the {@link IList} item at the specified index.
     * @param index The zero-based index of the item to remove.
     * @throws ArgumentOutOfRangeException {@code index} is not a valid index in the {@link IList}.
     * @throws NotSupportedException The {@link IList} is read-only. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * The {@link IList} has a fixed size.
     */
    void RemoveAt(int index) throws ArgumentOutOfRangeException, NotSupportedException;

    /**
     * Gets the element at the specified index.
     * @param index The zero-based index of the element to get.
     * @return The element at the specified index.
     * @throws ArgumentOutOfRangeException {@code index} is not a valid index in the {@link IList}.
     */
    @MaybeNull
    Object GetItem(int index) throws ArgumentOutOfRangeException;

    /**
     * Sets the element at the specified index.
     * @param index The zero-based index of the element to set.
     * @param value The element at the specified index.
     * @throws NotSupportedException The {@link IList} is read-only.
     * @throws ArgumentOutOfRangeException {@code index} is not a valid index in the {@link IList}.
     */
    void SetItem(int index, @MaybeNull Object value) throws ArgumentOutOfRangeException, NotSupportedException;
}
