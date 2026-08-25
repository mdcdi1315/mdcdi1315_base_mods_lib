package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;

import com.github.mdcdi1315.DotNetLayer.System.ValueType;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Defines a dictionary key/value pair that can be set or retrieved.
 */
@ClassIsDotNetStruct
public final class DictionaryEntry
    extends ValueType
{
    @AllowNull
    private Object key;
    @AllowNull
    private Object value;

    // Implicit ValueType constructor
    public DictionaryEntry() { key = null; value = null; }

    /**
     * Initializes an instance of the {@link DictionaryEntry} type with the specified key and value.
     * @param key The object defined in each key/value pair.
     * @param value The definition associated with {@code key}.
     */
    public DictionaryEntry(Object key, Object value)
    {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key in the key/value pair.
     * @return The key in the key/value pair.
     */
    @MaybeNull
    public Object GetKey() { return key; }

    /**
     * Gets the value in the key/value pair.
     * @return The value in the key/value pair.
     */
    @MaybeNull
    public Object GetValue() { return value; }

    /**
     * Sets the key in the key/value pair.
     * @param key The new key of the key/value pair.
     */
    public void SetKey(Object key) { this.key = key; }

    /**
     * Sets the value in the key/value pair.
     * @param value The new value of the key/value pair.
     */
    public void SetValue(Object value) { this.value = value; }

    @Override
    public String ToString() { return String.format("DictionaryEntry[Key=%s, Value=%s]", key, value); }
}
