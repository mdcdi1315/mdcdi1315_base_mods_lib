package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.ISupportsSlicing;
import com.github.mdcdi1315.basemodslib.utils.collections.IArrayBasedCollection;

import java.util.*;

public final class WrappedJavaListFromIList<T>
    extends WrappedJavaCollectionFromICollection<T, IList<T>>
    implements List<T>
{
    public WrappedJavaListFromIList(IList<T> list) { super(list); }

    private static final class ListIteratorImpl<T>
        implements ListIterator<T>
    {
        private int index;
        private final IList<T> list;

        public ListIteratorImpl(IList<T> list, int index)
        {
            this.list = list;
            this.index = index;
        }

        public ListIteratorImpl(IList<T> list) { this(list, -1); }

        @Override
        public boolean hasNext() { return this.index < list.getCount(); }

        @Override
        public T next() { return list.getItem(++index); }

        @Override
        public boolean hasPrevious() { return index > -1; }

        @Override
        public T previous() { return list.getItem(--index); }

        @Override
        public int nextIndex() { return index + 1; }

        @Override
        public int previousIndex() { return index - 1; }

        @Override
        public void remove() { list.RemoveAt(index); }

        @Override
        public void set(T t) { list.setItem(index, t); }

        @Override
        public void add(T t) { list.Insert(index, t); }
    }

    @Override
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    public boolean addAll(int index, @NotNull Collection<? extends T> c)
    {
        boolean changed = false;
        IList<T> list = GetCollection();
        if (list instanceof IArrayBasedCollection ac) { ac.EnsureCapacity(c.size()); changed = true; }
        if (c.size() > 0) { changed = true; }
        for (T t : c) { list.Insert(index++, t); }
        return changed;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c)
    {
        boolean changed = false;
        IList<T> list = GetCollection();
        for (int I = 0; I < list.getCount(); I++)
        {
            if (!c.contains(list.getItem(I))) {
                list.RemoveAt(I);
                I++;
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public T get(int index) { return GetCollection().getItem(index); }

    @Override
    public T set(int index, T element)
    {
        IList<T> list = GetCollection();
        T old = list.getItem(index);
        list.setItem(index, element);
        return old;
    }

    @Override
    public void add(int index, T element) { GetCollection().Insert(index, element); }

    @Override
    public T remove(int index)
    {
        IList<T> list = GetCollection();
        T old = list.getItem(index);
        list.RemoveAt(index);
        return old;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int indexOf(Object o) { return GetCollection().IndexOf((T)o); }

    @Override
    public int lastIndexOf(Object o)
    {
        IList<T> list = GetCollection();
        for (int I = list.getCount() - 1; I > -1; I--)
        {
            if (Objects.equals(o, list.getItem(I))) { return I; }
        }
        return -1;
    }

    @Override
    public @NotNull ListIterator<T> listIterator() { return new ListIteratorImpl<>(GetCollection()); }

    @Override
    public @NotNull ListIterator<T> listIterator(int index) { return new ListIteratorImpl<>(GetCollection(), index); }

    @Override
    public List<T> subList(int fromIndex, int toIndex)
    {
        IList<T> list = GetCollection();
        try {
            return new WrappedJavaListFromIList<>((IList<T>) ((ISupportsSlicing<T>)list).Slice(fromIndex, toIndex - fromIndex));
        } catch (ClassCastException e) {
            ArrayList<T> result = new ArrayList<>(toIndex - fromIndex);
            for (int I = fromIndex; I <= toIndex; I++) { result.add(list.getItem(I)); }
            return result;
        }
    }
}
