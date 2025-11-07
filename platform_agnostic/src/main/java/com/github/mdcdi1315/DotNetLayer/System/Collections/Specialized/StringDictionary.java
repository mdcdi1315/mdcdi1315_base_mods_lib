package com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Implements a hash table with the key and the value strongly typed to be strings rather than objects.
 */
public class StringDictionary
    implements IEnumerable
{
    // This is currently implemented with the below hash table implementation.
    // It is desirable to move to a better and more .NET-like alternative at some point,
    // however this one does currently keep the semantics as is in .NET .
    private final Hashtable<String , String> contents;

    private record KeysCollection(Hashtable<String, String> actual)
            implements ICollection
    {
        @Override
        public int getCount() {
            return actual.size();
        }

        @Override
        public boolean getIsSynchronized() {
            return true;
        }

        @Override
        public Object getSyncRoot() {
            return actual;
        }

        @Override
        public void CopyTo(Object array, int index)
                throws ArgumentException {
            actual.forEach(new CopyToOp(array, index));
        }

        private static final class CopyToOp
                implements BiConsumer<String, String> {
            private String[] array;
            private int current_index, index;

            public CopyToOp(Object array, int index) {
                try {
                    this.array = (String[]) array;
                } catch (ClassCastException cce) {
                    throw new ArgumentException("The type of the source array was not a string array!", "array");
                }
                this.index = index;
                current_index = 0;
            }

            @Override
            public void accept(String key, String value) {
                array[current_index++] = key;
            }
        }

        private static final class KeysEnumerator
                implements IEnumerator
        {
            private String current;
            private Enumeration<String> en;

            public KeysEnumerator(Enumeration<String> en) {
                this.en = en;
            }

            @Override
            public Object getCurrent() {
                return (current == null) ? current = en.nextElement() : current;
            }

            @Override
            public boolean MoveNext() {
                current = null;
                return en.hasMoreElements();
            }

            @Override
            public void Reset() {
                throw new InvalidOperationException("Not supported for this enumerator object");
            }
        }

        @Override
        public IEnumerator GetEnumerator() {
            return new KeysEnumerator(actual.keys());
        }
    }

    private record ValuesCollection(Hashtable<String, String> actual)
        implements ICollection
    {
        @Override
        public int getCount() {
            return actual.size();
        }

        @Override
        public boolean getIsSynchronized() {
            return true;
        }

        @Override
        public Object getSyncRoot() {
            return actual;
        }

        @Override
        public void CopyTo(Object array, int index)
                throws ArgumentException
        {
            ArgumentNullException.ThrowIfNull(array, "array");
            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index must be into the provided array bounds.");
            }
            actual.forEach(new CopyToOp(array , index));
        }

        private static final class ValuesEnumerator
            implements IEnumerator
        {
            private String current;
            private Iterator<String> en;

            public ValuesEnumerator(Collection<String> en) {
                this.en = en.iterator();
            }

            @Override
            public Object getCurrent() {
                return (current == null) ? current = en.next() : current;
            }

            @Override
            public boolean MoveNext() {
                current = null;
                return en.hasNext();
            }

            @Override
            public void Reset() {
                throw new InvalidOperationException("Not supported for this enumerator object");
            }
        }

        @Override
        public IEnumerator GetEnumerator() {
            return new ValuesEnumerator(actual.values());
        }
    }

    /**
     * Initializes a new instance of the {@link StringDictionary} class.
     */
    public StringDictionary() {
        contents = new Hashtable<>();
    }

    /**
     * Gets the number of key/value pairs in the {@link StringDictionary}.
     * @return The number of key/value pairs in the {@link StringDictionary}. <br />
     * Retrieving the value of this method is an O(1) operation.
     */
    public int GetCount() {
        return contents.size();
    }

    /**
     * Gets a value indicating whether access to the {@link StringDictionary} is synchronized (thread safe).
     * @return {@code true} if access to the {@link StringDictionary} is synchronized (thread safe); otherwise, {@code false}.
     */
    public boolean GetIsSynchronized() {
        return true;
    }

    /**
     * Gets the value associated with the specified key.
     * @param key The key whose value to get.
     * @return The value associated with the specified key. If the specified key is not found, it returns {@code null}.
     */
    @MaybeNull
    public String GetItem(String key) {
        ArgumentNullException.ThrowIfNull(key, "key");
        return contents.get(key.toLowerCase(Locale.ROOT));
    }

    /**
     * Sets the value associated with the specified key.
     * @param key The key whose value to set.
     * @param value The value associated with the specified key. If the specified key is not found, it creates a new entry with the specified key.
     */
    public void SetItem(String key, @MaybeNull String value) {
        ArgumentNullException.ThrowIfNull(key, "key");
        contents.put(key.toLowerCase(Locale.ROOT) , value);
    }

    /**
     * Gets a collection of keys in the {@link StringDictionary}.
     * @return An {@link ICollection} that provides the keys in the {@link StringDictionary}.
     */
    public ICollection GetKeys() {
        return new KeysCollection(contents);
    }

    /**
     * Gets an object that can be used to synchronize access to the {@link StringDictionary}.
     * @return An {@link Object} that can be used to synchronize access to the {@link StringDictionary}.
     */
    public Object GetSyncRoot() {
        return contents;
    }

    /**
     * Gets a collection of values in the {@link StringDictionary}.
     * @return An {@link ICollection} that provides the values in the {@link StringDictionary}.
     */
    public ICollection GetValues() {
        return new ValuesCollection(contents);
    }

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

        if (contents.putIfAbsent(key.toLowerCase(Locale.ROOT), value) != null) {
            throw new ArgumentException(String.format("The entry with name %s does already exist in the collection!" , key));
        }
    }

    /**
     * Removes all entries from the {@link StringDictionary}.
     */
    public void Clear() { contents.clear(); }

    /**
     * Determines if the {@link StringDictionary} contains a specific key.
     * @param key The key to locate in the {@link StringDictionary}.
     * @return {@code true} if the {@link StringDictionary} contains an entry with the specified key; otherwise, {@code false}.
     * @throws ArgumentNullException The key is {@code null}.
     */
    public boolean ContainsKey(String key)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key);

        return contents.containsKey(key.toLowerCase(Locale.ROOT));
    }

    /**
     * Determines if the {@link StringDictionary} contains a specific value.
     * @param value The value to locate in the {@link StringDictionary}. The value can be {@code null}.
     * @return {@code true} if the {@link StringDictionary} contains an element with the specified value; otherwise, {@code false}.
     */
    public boolean ContainsValue(@MaybeNull String value) {
        return contents.containsValue(value);
    }

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
        }
        contents.forEach(new CopyToOp(array , index));
    }

    private static final class CopyToOp
        implements BiConsumer<String , String>
    {
        private String[] array;
        private int current_index, index;

        public CopyToOp(Object array, int index)
        {
            try {
                this.array = (String[]) array;
            } catch (ClassCastException cce) {
                throw new ArgumentException("The type of the source array was not a string array!" , "array");
            }
            this.index = index;
            current_index = 0;
        }

        @Override
        public void accept(String key, String value) {
            array[current_index++] = value;
        }
    }

    /**
     * Returns an enumerator that iterates through the string dictionary.
     * @return An {@link IEnumerator} that iterates through the string dictionary.
     */
    public IEnumerator GetEnumerator() {
        return GetValues().GetEnumerator();
    }

    /**
     * Removes the entry with the specified key from the string dictionary.
     * @param key The key of the entry to remove.
     * @throws ArgumentNullException {@code key} is {@code null}.
     * @throws NotSupportedException The {@link StringDictionary} is read-only.
     */
    public void Remove(String key)
        throws ArgumentNullException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(key);

        contents.remove(key.toLowerCase(Locale.ROOT));
    }
}
