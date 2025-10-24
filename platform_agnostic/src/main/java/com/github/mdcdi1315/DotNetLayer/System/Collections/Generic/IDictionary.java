package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;


import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.OverloadedMethod;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNullWhen;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;

/**
 * Represents a generic collection of key/value pairs.
 * @param <TKey> The type of keys in the dictionary.
 * @param <TValue> The type of values in the dictionary.
 */
//    [DefaultMember("Item")]
public interface IDictionary<TKey, TValue>
        extends ICollection<KeyValuePair<TKey, TValue>>,
        IEnumerable<KeyValuePair<TKey, TValue>>
{
    /**
     * Gets the element with the specified key.
     * @param key The key of the element to get.
     * @return The element with the specified key.
     * @exception ArgumentNullException key is null.
     * @exception KeyNotFoundException The key is not found.
     */
    TValue getItem(TKey key);

    /**
     * Sets the element with the specified key.
     * @param key The key of the element to set.
     * @param value The new value to set to the element at key.
     * @exception ArgumentNullException key is null.
     * @exception NotSupportedException The {@link IDictionary} is read-only.
     */
    void setItem(TKey key, TValue value);

    /**
     * Gets an {@link ICollection} containing the keys of the System.Collections.Generic.IDictionary`2.
     * @return An {@link ICollection} containing the keys of the object that implements {@link IDictionary}.
     */
    ICollection<TKey> getKeys();

    /**
     * Gets an {@link ICollection} containing the values in the System.Collections.Generic.IDictionary`2.
     * @return An {@link ICollection} containing the values of the object that implements {@link IDictionary}.
     */
    ICollection<TValue> getValues();

    /**
     * Adds an element with the provided key and value to the {@link IDictionary}.
     * @param key The object to use as the key of the element to add.
     * @param value The object to use as the value of the element to add.
     * @exception ArgumentNullException key is null.
     * @exception ArgumentException An element with the same key already exists in the {@link IDictionary}.
     * @exception NotSupportedException The {@link IDictionary} is read-only.
     */
    void Add(TKey key, TValue value);

    /**
     *  Determines whether the {@link IDictionary} contains an element with the specified key.
     * @param key The key to locate in the {@link IDictionary}.
     * @return true if the {@link IDictionary} contains an element with the key; otherwise, false.
     * @exception ArgumentNullException key is null.
     */
    boolean ContainsKey(TKey key);

    /**
     * Removes the element with the specified key from the {@link IDictionary}.
     * @param key The key of the element to remove.
     * @return true if the element is successfully removed; otherwise, false.
     * This method also returns false if key was not found in the original {@link IDictionary}.
     * @exception ArgumentNullException key is null.
     * @exception NotSupportedException The {@link IDictionary} is read-only.
     */
    @OverloadedMethod("Remove")
    boolean Remove_Ordinal2(TKey key);

    /**
     * Gets the value associated with the specified key.
     * @param key The key whose value to get.
     * @param value When this method returns, the value associated with the specified key, if the
     * key is found; otherwise, the default value for the type of the value parameter.
     * This parameter is passed uninitialized.
     * @return true if the object that implements {@link IDictionary} contains an element with the specified key; otherwise, false.
     * @exception ArgumentNullException key is null.
     */
    boolean TryGetValue(TKey key, @MaybeNullWhen(ReturnValue = false) @DotNetByRefParameter(ByRefParameterType.OUT) ByRefParameter<TValue> value);
}