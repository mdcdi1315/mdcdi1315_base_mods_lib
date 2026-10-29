package com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IDictionary;
import com.github.mdcdi1315.DotNetLayer.System.Reflection.DefaultMember;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Represents an indexed collection of key/value pairs.
 */
@DefaultMember(MemberName = "Item")
public interface IOrderedDictionary
    extends IDictionary
{
    /**
     * Gets the element at the specified index.
     * @param index The zero-based index of the element to get.
     * @return The element at the specified index.
     * @throws ArgumentOutOfRangeException {@code index} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code index} is equal to or greater than {@link #getCount()}.
     */
    @AllowNull
    Object GetItem(int index) throws ArgumentOutOfRangeException;

    /**
     * Sets the element at the specified index.
     * @param index The zero-based index of the element to set.
     * @param value The element at the specified index.
     * @throws ArgumentOutOfRangeException {@code index} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code index} is equal to or greater than {@link #getCount()}.
     */
    void SetItem(int index, @AllowNull Object value) throws ArgumentOutOfRangeException;

    /**
     * Inserts a key/value pair into the collection at the specified index.
     * @param index The zero-based index at which the key/value pair should be inserted.
     * @param key The object to use as the key of the element to add.
     * @param value The object to use as the value of the element to add. The value can be {@code null}.
     * @throws ArgumentOutOfRangeException {@code index} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code index} is equal to or greater than {@link #getCount()}.
     * @throws ArgumentException An element with the same key already exists in the {@link IOrderedDictionary} collection.
     * @throws NotSupportedException The {@link IOrderedDictionary} collection is read-only. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * The {@link IOrderedDictionary} collection has a fixed size.
     */
    void Insert(int index, Object key, @AllowNull Object value) throws ArgumentOutOfRangeException, ArgumentException, NotSupportedException;

    /**
     * Removes the element at the specified index.
     * @param index The zero-based index of the element to remove.
     * @throws ArgumentOutOfRangeException {@code index} is less than 0. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code index} is equal to or greater than {@link #getCount()}.
     * @throws NotSupportedException The {@link IOrderedDictionary} collection is read-only. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * The {@link IOrderedDictionary} collection has a fixed size.
     */
    void RemoveAt(int index) throws ArgumentOutOfRangeException, NotSupportedException;
}
