package com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.DictionaryEntry;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.Dictionary;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.KeyValuePair;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import java.text.Collator;

/**
 * Implements a hash table with the key and the value strongly typed to be strings rather than objects.
 */
public class StringDictionary
    implements IEnumerable
{
    private final Dictionary<String, String> dictionary;

    /**
     * Initializes a new instance of the {@link StringDictionary} class.
     */
    public StringDictionary()
    {
        dictionary = new Dictionary<>(10, new StringInvariantComparer());
    }

    private record Enumerator(com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator<KeyValuePair<String, String>> enumerator)
            implements IEnumerator
    {
        @Override
        public Object getCurrent()
        {
            var c = enumerator.getCurrent();
            return new DictionaryEntry(c.getKey(), c.getValue());
        }

        @Override
        public void Reset() throws InvalidOperationException { enumerator.Reset(); }

        @Override
        public boolean MoveNext() throws InvalidOperationException { return enumerator.MoveNext(); }
    }

    private static final class StringInvariantComparer
        implements IEqualityComparer<String>
    {
        private final Collator c;

        public StringInvariantComparer()
        {
            c = Collator.getInstance();
            c.setStrength(Collator.PRIMARY);
        }

        @Override
        public int GetHashCode(String obj) { return obj.hashCode(); }

        @Override
        public boolean Equals(String x, String y) { return c.equals(x, y); }
    }

    /**
     * Gets the number of key/value pairs in the {@link StringDictionary}.
     * @return The number of key/value pairs in the {@link StringDictionary}. <br />
     * Retrieving the value of this method is an O(1) operation.
     */
    public int GetCount() { return dictionary.getCount(); }

    /**
     * Gets a value indicating whether access to the {@link StringDictionary} is synchronized (thread safe).
     * @return {@code true} if access to the {@link StringDictionary} is synchronized (thread safe); otherwise, {@code false}.
     */
    public boolean GetIsSynchronized() { return true; }

    /**
     * Gets the value associated with the specified key.
     * @param key The key whose value to get.
     * @return The value associated with the specified key. If the specified key is not found, it returns {@code null}.
     * @throws ArgumentNullException {@code key} is {@code null}.
     */
    @MaybeNull
    public String GetItem(String key)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        return dictionary.getItem(key);
    }

    /**
     * Sets the value associated with the specified key.
     * @param key The key whose value to set.
     * @param value The value associated with the specified key. If the specified key is not found, it creates a new entry with the specified key.
     */
    public void SetItem(String key, @AllowNull String value)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        dictionary.setItem(key , value);
    }

    /**
     * Gets a collection of keys in the {@link StringDictionary}.
     * @return An {@link ICollection} that provides the keys in the {@link StringDictionary}.
     */
    public ICollection GetKeys() { return (ICollection)dictionary.getKeys(); }

    /**
     * Gets an object that can be used to synchronize access to the {@link StringDictionary}.
     * @return An {@link Object} that can be used to synchronize access to the {@link StringDictionary}.
     */
    public Object GetSyncRoot() { return dictionary; }

    /**
     * Gets a collection of values in the {@link StringDictionary}.
     * @return An {@link ICollection} that provides the values in the {@link StringDictionary}.
     */
    public ICollection GetValues() { return (ICollection)dictionary.getValues(); }

    /**
     * Adds an entry with the specified key and value into the {@link StringDictionary}.
     * @param key The key of the entry to add.
     * @param value The value of the entry to add. The value can be {@code null}.
     * @throws ArgumentNullException {@code key} is {@code null}.
     * @throws ArgumentException An entry with the same key already exists in the {@link StringDictionary}.
     * @throws NotSupportedException The {@link StringDictionary} is read-only.
     */
    public void Add(String key, @MaybeNull String value)
        throws ArgumentException
    {
        ArgumentNullException.ThrowIfNull(key);

        dictionary.Add(key, value);
    }

    /**
     * Removes all entries from the {@link StringDictionary}.
     */
    public void Clear() { dictionary.Clear(); }

    /**
     * Determines if the {@link StringDictionary} contains a specific key.
     * @param key The key to locate in the {@link StringDictionary}.
     * @return {@code true} if the {@link StringDictionary} contains an entry with the specified key; otherwise, {@code false}.
     * @throws ArgumentNullException The key is {@code null}.
     */
    public boolean ContainsKey(String key)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        return dictionary.ContainsKey(key);
    }

    /**
     * Determines if the {@link StringDictionary} contains a specific value.
     * @param value The value to locate in the {@link StringDictionary}. The value can be {@code null}.
     * @return {@code true} if the {@link StringDictionary} contains an element with the specified value; otherwise, {@code false}.
     */
    public boolean ContainsValue(@MaybeNull String value) { return dictionary.ContainsValue(value); }

    /**
     * Copies the string dictionary values to a one-dimensional array instance at the specified index.
     * @param array The one-dimensional array that is the destination of the values copied from the {@link StringDictionary}.
     * @param index The index in the array where copying begins.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code index} is less than zero.
     * @throws ArgumentException {@code array} is multidimensional.<br />
     * -or- <br />
     * The number of elements in the source {@link ICollection} is greater than the available space from index to the end of the destination array. <br />
     * -or- <br />
     * The type of the source {@link ICollection} cannot be cast automatically to the type of the destination array.
     */
    public void CopyTo(Object array, int index)
            throws ArgumentException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index must be into the provided array bounds.");
        } else {
            try (var enumerator = dictionary.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    java.lang.reflect.Array.set(array, index++, enumerator.getCurrent().getValue());
                }
            }
        }
    }

    /**
     * Returns an enumerator that iterates through the string dictionary.
     * @return An {@link IEnumerator} that iterates through the string dictionary.
     */
    public IEnumerator GetEnumerator() { return new Enumerator(dictionary.GetEnumerator()); }

    /**
     * Removes the entry with the specified key from the string dictionary.
     * @param key The key of the entry to remove.
     * @throws ArgumentNullException {@code key} is {@code null}.
     * @throws NotSupportedException The {@link StringDictionary} is read-only.
     */
    public void Remove(String key)
        throws ArgumentNullException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        dictionary.Remove_Ordinal2(key);
    }
}
