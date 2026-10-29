package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IDictionary;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.KeyValuePair;

import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.utils.collections.MappingEnumerator;

import java.util.*;
import java.lang.reflect.Array;

@SuppressWarnings("NullableProblems")
public class WrappedJavaMapFromIDictionary<TKey, TValue>
    implements Map<TKey, TValue>
{
    private final IDictionary<TKey, TValue> dictionary;

    public WrappedJavaMapFromIDictionary(IDictionary<TKey, TValue> collection)
    {
        this.dictionary = collection;
    }

    @Override
    public void clear() { dictionary.Clear(); }

    @Override
    public int size() { return dictionary.getCount(); }

    @Override
    public boolean isEmpty() { return dictionary.getCount() == 0; }

    @Override
    public Set<Entry<TKey, TValue>> entrySet() { return new EntrySet<>(dictionary); }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) { return dictionary.ContainsKey((TKey) key); }

    @Override
    public Set<TKey> keySet() { return new WrappedJavaSetFromICollection<>(dictionary.getKeys()); }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(Object value) { return dictionary.getValues().Contains((TValue) value); }

    @Override
    public Collection<TValue> values() { return new WrappedJavaCollectionFromICollection<>(dictionary.getValues()); }

    @Override
    @SuppressWarnings("unchecked")
    public TValue get(Object key)
    {
        ByRefParameter<TValue> value = new ByRefParameter<>();
        dictionary.TryGetValue((TKey) key, value);
        return value.Value;
    }

    @Override
    public TValue put(TKey key, TValue value)
    {
        ByRefParameter<TValue> val = new ByRefParameter<>();
        dictionary.TryGetValue(key, val);
        try {
            dictionary.setItem(key, value);
            return val.Value;
        } catch (NotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public TValue remove(Object key)
    {
        try {
            ByRefParameter<TValue> val = new ByRefParameter<>();
            if (dictionary.TryGetValue((TKey) key, val)) {
                dictionary.Remove_Ordinal2((TKey) key);
            }
            return val.Value;
        } catch (NotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public void putAll(Map<? extends TKey, ? extends TValue> m)
    {
        for (Map.Entry<? extends TKey, ? extends TValue> entry : m.entrySet())
        {
            dictionary.Add(entry.getKey(), entry.getValue());
        }
    }

    private static final class EntrySet<TKey, TValue>
        implements Set<Entry<TKey, TValue>>
    {
        private final IDictionary<TKey, TValue> dictionary;

        public EntrySet(IDictionary<TKey, TValue> dictionary) { this.dictionary = dictionary; }

        private record Mapper<TKey, TValue>()
            implements Func2<KeyValuePair<TKey, TValue>, Map.Entry<TKey, TValue>>
        {
            @Override
            public Pair<TKey, TValue> function(KeyValuePair<TKey, TValue> input) { return new Pair<>(input.getKey(), input.getValue()); }
        }

        @Override
        public void clear() { dictionary.Clear(); }

        @Override
        public int size() { return dictionary.getCount(); }

        @Override
        public boolean isEmpty() { return dictionary.getCount() == 0; }

        @Override
        public boolean containsAll(Collection<?> c) { return CollectionHelpers.ContainsAll(dictionary, c); }

        @Override
        public boolean add(Entry<TKey, TValue> entry) { throw new UnsupportedOperationException("Entry set backed by a map"); }

        @Override
        public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException("Retain all cannot be supported"); }

        @Override
        public boolean addAll(Collection<? extends Entry<TKey, TValue>> c) { throw new UnsupportedOperationException("Entry set backed by a map"); }

        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o)
        {
            var mp = (Map.Entry<TKey, TValue>) o;
            return dictionary.Contains(new KeyValuePair<>(mp.getKey(), mp.getValue()));
        }

        @Override
        public Iterator<Entry<TKey, TValue>> iterator()
        {
            return new DisposableIteratorImplFromEnumerator<>(
                    new MappingEnumerator<>(dictionary.GetEnumerator(), new Mapper<>())
            );
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o)
        {
            try {
                var mp = (Map.Entry<TKey, TValue>) o;
                return dictionary.Remove(new KeyValuePair<>(mp.getKey(), mp.getValue()));
            } catch (NotSupportedException e) {
                throw new UnsupportedOperationException(e);
            }
        }

        @Override
        public boolean removeAll(Collection<?> c)
        {
            for (Object o : c)
            {
                if (!remove(o)) { return false; }
            }
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] input_array)
        {
            int count = dictionary.getCount();
            if (input_array.length < count)
            {
                input_array = (T[]) Array.newInstance(input_array.getClass().componentType(), count);
            }
            try (var enumerator = dictionary.GetEnumerator())
            {
                int I = 0;
                KeyValuePair<TKey, TValue> pair;
                while (I < input_array.length && enumerator.MoveNext())
                {
                    pair = enumerator.getCurrent();
                    input_array[I++] = (T) new Pair<>(pair.getKey(), pair.getValue());
                }
            }
            return input_array;
        }

        @Override
        public Object[] toArray()
        {
            Object[] result = new Object[dictionary.getCount()];
            try (var enumerator = dictionary.GetEnumerator())
            {
                int I = 0;
                KeyValuePair<TKey, TValue> pair;
                while (I < result.length && enumerator.MoveNext())
                {
                    pair = enumerator.getCurrent();
                    result[I++] = new Pair<>(pair.getKey(), pair.getValue());
                }
            }
            return result;
        }
    }
}
