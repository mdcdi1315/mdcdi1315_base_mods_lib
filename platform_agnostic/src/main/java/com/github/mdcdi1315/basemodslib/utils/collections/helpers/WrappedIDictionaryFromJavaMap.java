package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.utils.collections.MappingEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;

import java.util.Map;

public final class WrappedIDictionaryFromJavaMap<TKey, TValue>
    implements IDictionary<TKey, TValue>
{
    private final Map<TKey, TValue> map;

    public WrappedIDictionaryFromJavaMap(@DisallowNull Map<TKey, TValue> collection) { this.map = collection; }

    @Override
    public int getCount() { return map.size(); }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    public ICollection<TKey> getKeys() { return new WrappedICollectionFromJavaCollection<>(map.keySet()); }

    @Override
    public ICollection<TValue> getValues() { return new WrappedICollectionFromJavaCollection<>(map.values()); }

    @Override
    public boolean Remove(KeyValuePair<TKey, TValue> item)
    {
        KeyValuePair.ValidateNonNullStructure(item);
        return map.remove(item.getKey(), item.getValue());
    }

    @Override
    public void Add(KeyValuePair<TKey, TValue> item)
    {
        KeyValuePair.ValidateNonNullStructure(item);
        Add(item.getKey(), item.getValue());
    }

    @Override
    public boolean Contains(KeyValuePair<TKey, TValue> item)
    {
        KeyValuePair.ValidateNonNullStructure(item);
        return ContainsKey(item.getKey());
    }

    @Override
    public TValue getItem(TKey key)
            throws ArgumentNullException, KeyNotFoundException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            throw new KeyNotFoundException("The specified key does not exist in the dictionary. \nKey: " + key);
        }
    }

    @Override
    public void setItem(TKey key, TValue value)
            throws ArgumentNullException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        try {
            map.put(key, value);
        } catch (UnsupportedOperationException e) {
            throw new NotSupportedException(e.getMessage());
        }
    }

    @Override
    public void Add(TKey key, TValue value)
            throws ArgumentNullException, ArgumentException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        if (map.containsKey(key)) {
            throw new ArgumentException("An element with the same key is already existing in the dictionary.");
        } else {
            try {
                map.put(key, value);
            } catch (UnsupportedOperationException e) {
                throw new NotSupportedException(e.getMessage());
            }
        }
    }

    @Override
    public boolean ContainsKey(TKey key)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        return map.containsKey(key);
    }

    @Override
    public boolean Remove_Ordinal2(TKey key)
            throws ArgumentNullException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        if (map.containsKey(key)) {
            map.remove(key);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean TryGetValue(TKey key, ByRefParameter<TValue> value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        ByRefParameter.AssertEOut(value);
        if (map.containsKey(key)) {
            value.Value = map.get(key);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void Clear()
    {
        try {
            map.clear();
        } catch (UnsupportedOperationException e) {
            throw new NotSupportedException(e.getMessage());
        }
    }

    @Override
    public void CopyTo(KeyValuePair<TKey, TValue>[] array, int arrayIndex)
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        CollectionHelpers.CheckCopyToArguments(arrayIndex, array.length, map.size());
        int index = arrayIndex;
        for (var kvp : map.entrySet())
        {
            array[index++] = new KeyValuePair<>(
                    kvp.getKey(),
                    kvp.getValue()
            );
        }
    }

    @Override
    public IEnumerator<KeyValuePair<TKey, TValue>> GetEnumerator()
    {
        return new MappingEnumerator<>(
                new FromIteratorEnumerator<>(map.entrySet().iterator()),
                (Func2<Map.Entry<TKey,TValue>, KeyValuePair<TKey,TValue>>) CollectionManipulations::AsKeyValuePair
        );
    }
}
