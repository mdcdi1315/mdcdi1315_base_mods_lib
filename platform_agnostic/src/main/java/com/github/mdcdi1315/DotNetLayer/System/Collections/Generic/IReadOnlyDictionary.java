package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNullWhen;

/**
 * Represents a generic read-only collection of key/value pairs.
 * @param <TKey> The type of keys in the read-only dictionary.
 * @param <TValue> The type of values in the read-only dictionary.
 */
public interface IReadOnlyDictionary<TKey, TValue>
        extends IReadOnlyCollection<KeyValuePair<TKey, TValue>>
{
    /**
     * Determines whether the read-only dictionary contains an element that has the specified key.
     * @param key The key to locate.
     * @return {@code true} if the read-only dictionary contains an element that has the specified key; otherwise, {@code false}.
     * @throws ArgumentNullException {@code key} is {@code null}.
     */
    boolean ContainsKey(TKey key) throws ArgumentNullException;

    /**
     * Gets the value that is associated with the specified key.
     * @param key The key to locate.
     * @param value When this method returns, the value associated with the specified key, if the key is found; otherwise, the default value for the type of the {@code value} parameter.
     *              This parameter is passed uninitialized.
     * @return {@code true} if the object that implements the {@link IReadOnlyDictionary} interface contains an element that has the specified key; otherwise, {@code false}.
     * @throws ArgumentNullException {@code key} is {@code null}.
     */
    boolean TryGetValue(TKey key, @DotNetByRefParameter(ByRefParameterType.OUT) @MaybeNullWhen(ReturnValue = false) ByRefParameter<TValue> value) throws ArgumentNullException;

    /**
     * Gets the element that has the specified key in the read-only dictionary.
     * @param key The key to locate.
     * @return The element that has the specified key in the read-only dictionary.
     * @throws ArgumentNullException {@code key} is {@code null}.
     * @throws KeyNotFoundException The property is retrieved and {@code key} is not found.
     */
    @MaybeNull
    TValue GetItem(TKey key) throws ArgumentNullException, KeyNotFoundException;

    /**
     * Gets an enumerable collection that contains the keys in the read-only dictionary.
     * @return An enumerable collection that contains the keys in the read-only dictionary.
     */
    IEnumerable<TKey> GetKeys();

    /**
     * Gets an enumerable collection that contains the values in the read-only dictionary.
     * @return An enumerable collection that contains the values in the read-only dictionary.
     */
    IEnumerable<TValue> GetValues();
}
