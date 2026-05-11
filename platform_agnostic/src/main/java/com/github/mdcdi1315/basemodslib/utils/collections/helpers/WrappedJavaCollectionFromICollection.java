package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.basemodslib.utils.collections.DisposableIterator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.util.Iterator;
import java.util.Collection;

public class WrappedJavaCollectionFromICollection<T, TC extends ICollection<T>>
    implements Collection<T>
{
    private final TC collection;

    public WrappedJavaCollectionFromICollection(@NotNull TC collection) { this.collection = collection; }

    @NotNull
    protected final TC GetCollection() { return collection; }

    @Override
    public int size() { return collection.getCount(); }

    @Override
    public boolean isEmpty() { return collection.getCount() == 0; }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) { return collection.Contains((T)o); }

    @Override
    public Iterator<T> iterator() { return DisposableIterator.FromEnumerator(collection.GetEnumerator()); }

    @Override
    public Object[] toArray() { return CollectionBridgingHelpers.ToArray(collection, ICollection::getCount); }

    @Override
    @SuppressWarnings("unchecked")
    public <T1> T1[] toArray(T1[] a) { return CollectionBridgingHelpers.ToArray(collection, ICollection::getCount, a); }

    @Override
    public boolean add(T t)
    {
        collection.Add(t);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) { return collection.Remove((T)o); }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsAll(Collection<?> c)
    {
        for (Object o : c) {
            if (!collection.Contains((T)o)) { return false; }
        }
        return true;
    }

    @Override
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    public boolean addAll(Collection<? extends T> c) { return CollectionBridgingHelpers.AddAll(collection, c, collection::Add); }

    @Override
    @SuppressWarnings("unchecked")
    public boolean removeAll(Collection<?> c) {
        boolean at_least_one = false;
        for (Object o : c) {
            if (collection.Remove((T)o)) { at_least_one = true; }
        }
        return at_least_one;
    }

    @Override
    public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException("Retain all cannot be supported"); }

    @Override
    public void clear() { collection.Clear(); }
}
