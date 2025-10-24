package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.System.ValueType;

/**
 * Defines a key/value pair that can be set or retrieved.
 * @param <TKey> The type of the key.
 * @param <TValue> The type of the value.
 */
@ClassIsDotNetStruct
public final class KeyValuePair<TKey, TValue>
    extends ValueType
{
    private TKey tk;
    private TValue tv;

    public KeyValuePair()
    {
        tk = null;
        tv = null;
    }

    /**
     * Initializes a new instance of the {@link KeyValuePair} structure with the specified key and value.
     * @param key The object defined in each key/value pair.
     * @param value The definition associated with key.
     */
    public KeyValuePair(TKey key , TValue value)
    {
        tk = key;
        tv = value;
    }

    /**
     * Gets the key in the key/value pair.
     * @return A {@link TKey} that is the key of the {@link KeyValuePair}.
     */
    public TKey getKey() {
        return tk;
    }

    /**
     * Gets the value in the key/value pair.
     * @return A {@link TValue} that is the value of the {@link KeyValuePair}.
     */
    public TValue getValue() {
        return tv;
    }

    /**
     * Returns a string representation of the {@link KeyValuePair}, using the string representations of the key and value.
     * @return A string representation of the {@link KeyValuePair}, which includes the string representations of the key and value.
     */
    @Override
    public String ToString() {
        if (tk == null && tv == null) {
            return "Key = null , Value = null";
        } else if (tk == null) {
            return String.format("Key = null , Value = %s" , tv);
        } else if (tv == null) {
            return String.format("Key = %s , Value = null" , tk);
        } else {
            return String.format("Key = %s , Value = %s" , tk , tv);
        }
    }
}
