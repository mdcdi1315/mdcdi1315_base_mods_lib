package com.github.mdcdi1315.DotNetLayer.System.Collections.ObjectModel;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.OverloadedMethod;
import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.*;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNullWhen;

/**
 * Provides the abstract base class for a collection whose keys are embedded in the values.
 * @param <TKey> The type of keys in the collection.
 * @param <TItem> The type of items in the collection.
 */
public abstract class KeyedCollection<TKey, TItem>
        extends Collection<TItem>
{
    private static final int DefaultThreshold = 0;

    private final IEqualityComparer<? super TKey> comparer; // Do not rename (binary serialization)
    @AllowNull
    private Dictionary<TKey, TItem> dict; // Do not rename (binary serialization)
    private int keyCount; // Do not rename (binary serialization)
    private final int threshold; // Do not rename (binary serialization)

    /**
     * Initializes a new instance of the {@link KeyedCollection} class that uses the default equality comparer.
     */
    protected KeyedCollection() { this(null, DefaultThreshold); }

    /**
     * Initializes a new instance of the {@link KeyedCollection} class that uses the specified equality comparer.
     * @param comparer The implementation of the {@link IEqualityComparer} generic interface to use when comparing keys, or {@code null} to use the default equality comparer for the type of the key, obtained from {@link EqualityComparer#GetDefault()}.
     */
    protected KeyedCollection(@AllowNull IEqualityComparer<? super TKey> comparer) { this(comparer, DefaultThreshold); }

    /**
     * Initializes a new instance of the {@link KeyedCollection} class that uses the specified equality comparer and creates a lookup dictionary when the specified threshold is exceeded.
     * @param comparer The implementation of the {@link IEqualityComparer} generic interface to use when comparing keys, or {@code null} to use the default equality comparer for the type of the key, obtained from {@link EqualityComparer#GetDefault()}.
     * @param dictionaryCreationThreshold The number of elements the collection can hold without creating a lookup dictionary (0 creates the lookup dictionary when the first item is added), or -1 to specify that a lookup dictionary is never created.
     * @throws ArgumentOutOfRangeException {@code dictionaryCreationThreshold} is less than -1.
     */
    protected KeyedCollection(@AllowNull IEqualityComparer<? super TKey> comparer, int dictionaryCreationThreshold)
        throws ArgumentOutOfRangeException
    {
        // Be explicit about the use of List<T> so we can foreach over
        // Items internally without enumerator allocations.
        super(new List<>());
        if (dictionaryCreationThreshold < -1) {
            throw new ArgumentOutOfRangeException("dictionaryCreationThreshold", dictionaryCreationThreshold, "Invalid dictionary creation threshold.");
        } else {
            this.comparer = (comparer == null) ? EqualityComparer.GetDefault() : comparer;
            threshold = dictionaryCreationThreshold == -1 ? Integer.MAX_VALUE : dictionaryCreationThreshold;
        }
    }

    /**
     * When implemented in a derived class, extracts the key from the specified element.
     * @param item The element from which to extract the key.
     * @return The key for the specified element.
     */
    protected abstract TKey GetKeyForItem(TItem item);

    /**
     * Gets the lookup dictionary of the {@link KeyedCollection}.
     * @return The lookup dictionary of the {@link KeyedCollection}, if it exists; otherwise, {@code null}.
     */
    @MaybeNull
    protected final IDictionary<TKey, TItem> GetDictionary() { return dict; }

    /**
     * Changes the key associated with the specified element in the lookup dictionary.
     * @param item The element to change the key of.
     * @param newKey The new key for {@code item}.
     */
    protected void ChangeItemKey(TItem item, TKey newKey)
    {
        if (!ContainsItem(item))
        {
            throw new ArgumentException("Item does not exist.", "item");
        }

        TKey oldKey = GetKeyForItem(item);
        if (!comparer.Equals(oldKey, newKey))
        {
            if (newKey != null)
            {
                AddKey(newKey, item);
            }
            if (oldKey != null)
            {
                RemoveKey(oldKey);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void ClearItems()
    {
        super.ClearItems();
        if (dict != null) { dict.Clear(); }
        keyCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void InsertItem(int index, TItem item)
    {
        TKey key = GetKeyForItem(item);
        if (key != null)
        {
            AddKey(key, item);
        }

        super.InsertItem(index, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void RemoveItem(int index)
    {
        TKey key = GetKeyForItem(GetItems().getItem(index));
        if (key != null)
        {
            RemoveKey(key);
        }

        super.RemoveItem(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void SetItem(int index, TItem item)
    {
        TKey newKey = GetKeyForItem(item);
        TKey oldKey = GetKeyForItem(GetItems().getItem(index));

        if (comparer.Equals(oldKey, newKey))
        {
            if (newKey != null && dict != null)
            {
                dict.setItem(newKey, item);
            }
        }
        else
        {
            if (newKey != null)
            {
                AddKey(newKey, item);
            }

            if (oldKey != null)
            {
                RemoveKey(oldKey);
            }
        }

        super.SetItem(index, item);
    }

    private void AddKey(TKey key, TItem item)
    {
        if (dict != null)
        {
            dict.Add(key, item);
        }
        else if (keyCount == threshold)
        {
            CreateDictionary();
            dict.Add(key, item);
        }
        else
        {
            if (Contains_Ordinal2(key))
            {
                throw new ArgumentException("Attempted to add again the same key.\nKey: " + key, "key");
            }

            keyCount++;
        }
    }

    private boolean ContainsItem(TItem item)
    {
        TKey key;
        if ((dict == null) || ((key = GetKeyForItem(item)) == null))
        {
            return GetItems().Contains(item);
        }

        ByRefParameter<TItem> itemInDict = new ByRefParameter<>();
        if (dict.TryGetValue(key, itemInDict))
        {
            return EqualityComparer.GetDefault().Equals(itemInDict.Value, item);
        }

        return false;
    }

    private void CreateDictionary()
    {
        dict = new Dictionary<>(0, comparer);
        try (var en = GetItems().GetEnumerator())
        {
            while (en.MoveNext())
            {
                var item = en.getCurrent();
                TKey key = GetKeyForItem(item);
                if (key != null)
                {
                    dict.Add(key, item);
                }
            }
        }
        // We know that we can clear and minimize the mem usage of the underlying list.
        GetItems().Clear();
        ((List<TItem>)GetItems()).TrimExcess();
    }

    private void RemoveKey(TKey key)
    {
        // Debug.Assert(key != null, "key shouldn't be null!");
        if (dict != null)
        {
            dict.Remove_Ordinal2(key);
        }
        else
        {
            keyCount--;
        }
    }

    /**
     * Gets the generic equality comparer that is used to determine equality of keys in the collection.
     * @return The implementation of the {@link IEqualityComparer} generic interface that is used to determine equality of keys in the collection.
     */
    @NotNull
    public final IEqualityComparer<? super TKey> GetComparer() { return comparer; }

    /**
     * Determines whether the collection contains an element with the specified key.
     * @param key The key to locate in the {@link KeyedCollection}.
     * @return {@code true} if the {@link KeyedCollection} contains an element with the specified key; otherwise, {@code false}.
     * @throws ArgumentNullException {@code key} is {@code null}.
     */
    @OverloadedMethod("Contains")
    public final boolean Contains_Ordinal2(TKey key)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        if (dict != null)
        {
            return dict.ContainsKey(key);
        }

        try (var en = GetItems().GetEnumerator())
        {
            while (en.MoveNext())
            {
                if (comparer.Equals(GetKeyForItem(en.getCurrent()), key))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Removes the element with the specified key from the {@link KeyedCollection}.
     * @param key The key of the element to remove.
     * @return {@code true} if the element is successfully removed; otherwise, {@code false}. This method also returns {@code false} if {@code key} is not found in the {@link KeyedCollection}.
     * @throws ArgumentNullException {@code key} is {@code null}.
     */
    @OverloadedMethod("Remove")
    public final boolean Remove_Ordinal2(TKey key)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        if (dict != null)
        {
            ByRefParameter<TItem> item = new ByRefParameter<>();
            return dict.TryGetValue(key, item) && Remove(item.Value);
        }

        int count = GetItems().getCount();
        for (int i = 0; i < count; i++)
        {
            if (comparer.Equals(GetKeyForItem(GetItems().getItem(i)), key))
            {
                RemoveItem(i);
                return true;
            }
        }

        return false;
    }

    /**
     * Tries to get an item from the collection using the specified key.
     * @param key The key of the item to search in the collection.
     * @param item When this method returns {@code true}, the item from the collection that matches the provided key; when this method returns {@code false}, the default value for the type of the collection.
     * @return {@code true} if an item for the specified key was found in the collection; otherwise, {@code false}.
     * @throws ArgumentNullException {@code key} is {@code null}.
     */
    public final boolean TryGetValue(TKey key, @DotNetByRefParameter(ByRefParameterType.OUT) @MaybeNullWhen(ReturnValue = false) ByRefParameter<TItem> item)
            throws ArgumentNullException
    {
        ByRefParameter.AssertEOut(item);
        ArgumentNullException.ThrowIfNull(key, "key");

        if (dict != null)
        {
            return dict.TryGetValue(key, item);
        }

        try (var en = GetItems().GetEnumerator())
        {
            TItem itemInItems;
            while (en.MoveNext())
            {
                itemInItems = en.getCurrent();
                TKey keyInItems = GetKeyForItem(itemInItems);
                if (keyInItems != null && comparer.Equals(key, keyInItems))
                {
                    item.Value = itemInItems;
                    return true;
                }
            }
        }

        item.Value = null;
        return false;
    }

    /**
     * Gets the element with the specified key.
     * @param key The key of the element to get.
     * @return The element with the specified key. If an element with the specified key is not found, an exception is thrown.
     * @throws ArgumentNullException {@code key} is {@code null}.
     * @throws KeyNotFoundException An element with the specified key does not exist in the collection.
     */
    @MaybeNull
    public final TItem GetItem(TKey key)
            throws ArgumentNullException, KeyNotFoundException
    {
        ByRefParameter<TItem> item = new ByRefParameter<>();
        if (TryGetValue(key, item))
        {
            return item.Value;
        }

        throw new KeyNotFoundException("Key not found: " + key);
    }
}
