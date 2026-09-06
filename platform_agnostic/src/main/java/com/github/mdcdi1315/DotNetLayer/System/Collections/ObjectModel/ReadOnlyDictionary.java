package com.github.mdcdi1315.DotNetLayer.System.Collections.ObjectModel;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Represents a read-only, generic collection of key/value pairs.
 * @param <TKey> The type of keys in the dictionary.
 * @param <TValue> The type of values in the dictionary.
 */
public class ReadOnlyDictionary<TKey, TValue>
    implements IDictionary<TKey, TValue>,
        IReadOnlyDictionary<TKey, TValue>
{
    private final IDictionary<TKey, TValue> dictionary;

    @AllowNull
    private KeyCollection<TKey> keys;
    @AllowNull
    private ValueCollection<TValue> values;

    /**
     * Initializes a new instance of the {@link ReadOnlyDictionary} class that is a wrapper around the specified dictionary.
     * @param dictionary The dictionary to wrap.
     * @throws ArgumentNullException {@code dictionary} is {@code null}.
     */
    public ReadOnlyDictionary(IDictionary<TKey, TValue> dictionary)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(dictionary, "dictionary");
        this.dictionary = dictionary;
    }

    @NotNull
    protected final IDictionary<TKey, TValue> GetDictionary() { return dictionary; }

    @Override
    public KeyCollection<TKey> getKeys()
    {
        if (keys == null) {
            keys = new KeyCollection<>(dictionary.getKeys());
        }
        return keys;
    }

    @Override
    public ValueCollection<TValue> getValues()
    {
        if (values == null) {
            values = new ValueCollection<>(dictionary.getValues());
        }
        return values;
    }

    @Override
    public boolean TryGetValue(TKey key, ByRefParameter<TValue> value)
            throws ArgumentNullException
    {
        ByRefParameter.AssertEOut(value);
        return dictionary.TryGetValue(key, value);
    }

    @Override
    public boolean getIsReadOnly() { return true; }

    @Override
    public int getCount() { return dictionary.getCount(); }

    @Override
    public KeyCollection<TKey> GetKeys() { return getKeys(); }

    @Override
    public ValueCollection<TValue> GetValues() { return getValues(); }

    @Override
    public void Clear() { throw new NotSupportedException("Read-only dictionary"); }

    @Override
    public boolean Contains(KeyValuePair<TKey, TValue> item) { return dictionary.Contains(item); }

    @Override
    public IEnumerator<KeyValuePair<TKey, TValue>> GetEnumerator() { return dictionary.GetEnumerator(); }

    @Override
    public boolean ContainsKey(TKey key) throws ArgumentNullException { return dictionary.ContainsKey(key); }

    @Override
    public TValue GetItem(TKey key) throws ArgumentNullException, KeyNotFoundException { return getItem(key); }

    @Override
    public void Add(KeyValuePair<TKey, TValue> item) { throw new NotSupportedException("Read-only dictionary"); }

    @Override
    public void CopyTo(KeyValuePair<TKey, TValue>[] array, int arrayIndex) { dictionary.CopyTo(array, arrayIndex); }

    @Override
    public boolean Remove(KeyValuePair<TKey, TValue> item) { throw new NotSupportedException("Read-only dictionary"); }

    @Override
    public TValue getItem(TKey key) throws ArgumentNullException, KeyNotFoundException { return dictionary.getItem(key); }

    @Override
    public boolean Remove_Ordinal2(TKey key) throws ArgumentNullException, NotSupportedException { throw new NotSupportedException("Read-only dictionary"); }

    @Override
    public void setItem(TKey key, TValue value) throws ArgumentNullException, NotSupportedException { throw new NotSupportedException("Read-only dictionary"); }

    @Override
    public void Add(TKey key, TValue value) throws ArgumentNullException, ArgumentException, NotSupportedException { throw new NotSupportedException("Read-only dictionary"); }

    public static final class KeyCollection<TKey>
            implements ICollection<TKey>, com.github.mdcdi1315.DotNetLayer.System.Collections.ICollection, IReadOnlyCollection<TKey>
    {
        private final ICollection<TKey> _collection;

        public KeyCollection(ICollection<TKey> collection)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(collection, "collection");
            _collection = collection;
        }

        @Override
        public boolean getIsReadOnly() { return true; }

        @Override
        public Object getSyncRoot() { return _collection; }

        @Override
        public boolean getIsSynchronized() { return false; }

        @Override
        public int getCount() { return _collection.getCount(); }

        @Override
        public boolean Contains(TKey item) { return _collection.Contains(item); }

        @Override
        public void Clear() { throw new NotSupportedException("Read-only collection."); }

        @Override
        public void Add(TKey item) { throw new NotSupportedException("Read-only collection."); }

        @Override
        public IEnumerator<TKey> GetEnumerator() { return _collection.GetEnumerator(); }

        @Override
        public boolean Remove(TKey item) { throw new NotSupportedException("Read-only collection."); }

        @Override
        public void CopyTo(Object array, int index)
                throws ArgumentException
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            int length = java.lang.reflect.Array.getLength(array);

            if (index < 0 || index > length)
            {
                // ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
                throw new ArgumentOutOfRangeException("index", index, "Index out of bounds.");
            }

            if (length - index < _collection.getCount())
            {
                // ThrowHelper.ThrowArgumentException(ExceptionResource.Arg_ArrayPlusOffTooSmall);
                throw new ArgumentException("The specified array length, the selected index and the collection size is too small to fit all the elements in array.");
            }

            try (var en = _collection.GetEnumerator())
            {
                while (en.MoveNext())
                {
                    java.lang.reflect.Array.set(array, index++, en.getCurrent());
                }
            }
        }

        @Override
        public void CopyTo(TKey[] array, int arrayIndex)
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            if (arrayIndex < 0 || arrayIndex > array.length)
            {
                // ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
                throw new ArgumentOutOfRangeException("index", arrayIndex, "Index out of bounds.");
            }

            if (array.length - arrayIndex < _collection.getCount())
            {
                // ThrowHelper.ThrowArgumentException(ExceptionResource.Arg_ArrayPlusOffTooSmall);
                throw new ArgumentException("The specified array length, the selected index and the collection size is too small to fit all the elements in array.");
            }

            try (var en = _collection.GetEnumerator())
            {
                while (en.MoveNext())
                {
                    array[arrayIndex++] = en.getCurrent();
                }
            }
        }
    }

    public static final class ValueCollection<TValue>
            implements ICollection<TValue>, com.github.mdcdi1315.DotNetLayer.System.Collections.ICollection, IReadOnlyCollection<TValue>
    {
        private final ICollection<TValue> _dictionary;

        public ValueCollection(ICollection<TValue> dictionary)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(dictionary, "dictionary");
            _dictionary = dictionary;
        }

        @Override
        public boolean getIsReadOnly() { return true; }

        @Override
        public Object getSyncRoot() { return _dictionary; }

        @Override
        public boolean getIsSynchronized() { return false; }

        @Override
        public int getCount() { return _dictionary.getCount(); }

        @Override
        public boolean Contains(TValue item) { return _dictionary.Contains(item); }

        @Override
        public void Clear() { throw new NotSupportedException("Read-only collection."); }

        @Override
        public IEnumerator<TValue> GetEnumerator() { return _dictionary.GetEnumerator(); }

        @Override
        public void Add(TValue item) { throw new NotSupportedException("Read-only collection."); }

        @Override
        public boolean Remove(TValue item) { throw new NotSupportedException("Read-only collection."); }

        @Override
        public void CopyTo(Object array, int index)
                throws ArgumentException
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            int length = java.lang.reflect.Array.getLength(array);

            if (index < 0 || index > length)
            {
                // ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
                throw new ArgumentOutOfRangeException("index", index, "Index out of bounds.");
            }

            if (length - index < _dictionary.getCount())
            {
                // ThrowHelper.ThrowArgumentException(ExceptionResource.Arg_ArrayPlusOffTooSmall);
                throw new ArgumentException("The specified array length, the selected index and the collection size is too small to fit all the elements in array.");
            }

            try (var en = _dictionary.GetEnumerator())
            {
                while (en.MoveNext())
                {
                    java.lang.reflect.Array.set(array, index++, en.getCurrent());
                }
            }
        }

        @Override
        public void CopyTo(TValue[] array, int arrayIndex)
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            int length = array.length;

            if (arrayIndex < 0 || arrayIndex > length)
            {
                // ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
                throw new ArgumentOutOfRangeException("index", arrayIndex, "Index out of bounds.");
            }

            if (length - arrayIndex < _dictionary.getCount())
            {
                // ThrowHelper.ThrowArgumentException(ExceptionResource.Arg_ArrayPlusOffTooSmall);
                throw new ArgumentException("The specified array length, the selected index and the collection size is too small to fit all the elements in array.");
            }

            try (var en = _dictionary.GetEnumerator())
            {
                while (en.MoveNext())
                {
                    array[arrayIndex++] = en.getCurrent();
                }
            }
        }
    }
}
