package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.Reflection.DefaultMember;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Represents a nongeneric collection of key/value pairs.
 */
@DefaultMember(MemberName = "Item")
public interface IDictionary
    extends ICollection
{
    /**
     * Gets a value indicating whether the {@link IDictionary} object has a fixed size.
     * @return {@code true} if the {@link IDictionary} object has a fixed size; otherwise, {@code false}.
     */
    boolean GetIsFixedSize();

    /**
     * Gets a value indicating whether the {@link IDictionary} object is read-only.
     * @return {@code true} if the {@link IDictionary} object is read-only; otherwise, {@code false}.
     */
    boolean GetIsReadOnly();

    /**
     * Gets an {@link ICollection} object containing the keys of the {@link IDictionary} object.
     * @return An {@link ICollection} object containing the keys of the {@link IDictionary} object.
     */
    ICollection GetKeys();

    /**
     * Gets an {@link ICollection} object containing the values in the {@link IDictionary} object.
     * @return An {@link ICollection} object containing the values in the {@link IDictionary} object.
     */
    ICollection GetValues();

    /**
     * Adds an element with the provided key and value to the {@link IDictionary} object.
     * @param key The {@link Object} to use as the key of the element to add.
     * @param value The {@link Object} to use as the value of the element to add.
     * @throws ArgumentNullException {@code key} is {@code null}.
     * @throws ArgumentException An element with the same key already exists in the {@link IDictionary} object.
     * @throws NotSupportedException The {@link IDictionary} is read-only. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * The {@link IDictionary} has a fixed size.
     */
    void Add(Object key, @AllowNull Object value) throws ArgumentNullException, ArgumentException, NotSupportedException;

    /**
     * Removes all elements from the {@link IDictionary} object.
     * @throws NotSupportedException The {@link IDictionary} object is read-only.
     */
    void Clear() throws NotSupportedException;

    /**
     * Determines whether the {@link IDictionary} object contains an element with the specified key.
     * @param key The key to locate in the {@link IDictionary} object.
     * @return {@code true} if the {@link IDictionary} contains an element with the key; otherwise, {@code false}.
     * @throws ArgumentNullException {@code key} is {@code null}.
     */
    boolean Contains(Object key) throws ArgumentNullException;

    /**
     * Returns an {@link IDictionaryEnumerator} object for the {@link IDictionary} object.
     * @return An {@link IDictionaryEnumerator} object for the {@link IDictionary} object.
     */
    IDictionaryEnumerator GetEnumerator();

    /**
     * Removes the element with the specified key from the {@link IDictionary} object.
     * @param key The key of the element to remove.
     * @throws ArgumentNullException {@code key} is {@code null}.
     * @throws NotSupportedException The {@link IDictionary} object is read-only. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * The {@link IDictionary} has a fixed size.
     */
    void Remove(Object key) throws ArgumentNullException, NotSupportedException;

    /**
     * Gets the element with the specified key.
     * @param key The key of the element to get.
     * @return The element with the specified key, or {@code null} if the key does not exist.
     * @throws ArgumentNullException {@code key} is {@code null}.
     */
    @MaybeNull
    Object GetItem(Object key) throws ArgumentNullException;

    /**
     * Sets the element with the specified key.
     * @param key The key of the element to set.
     * @param value The new value of the element.
     * @throws ArgumentNullException {@code key} is {@code null}.
     * @throws NotSupportedException The {@link IDictionary} object is read-only. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code key} does not exist in the collection, and the {@link IDictionary} has a fixed size.
     */
    void SetItem(Object key, @AllowNull Object value) throws ArgumentNullException, NotSupportedException;
}
