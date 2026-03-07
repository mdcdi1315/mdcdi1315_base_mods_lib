package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Array;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.util.*;

/**
 * Provides a {@link List} implementation that maps all it's elements to type {@link TR} type before returning them. <br />
 * This is extremely useful for gaining the advantage of the {@link Collection#addAll(Collection)} method, but not having the issue of 'how to map {@link TA} to {@link TR} and use this?'. <br />
 * Note also, that methods that return or put elements of type {@link TR} throw the {@link UnsupportedOperationException} class because there is not a way to map {@link TR} to {@link TA}.
 * @param <TA> The original type of the list.
 * @param <TR> The mapped type to be returned through various methods of this class.
 */
public class DirectlyMappedList<TA , TR>
    implements List<TR>
{
    private final List<TA> underlying;
    private final Func2<TA , TR> mapping_function;

    /**
     * Initializes a new instance of the {@link DirectlyMappedList} class.
     * @param underlying The underlying list to map its elements.
     * @param mapper The mapping function to use that will map a single {@link TA} element to a {@link TR} element.
     * @throws ArgumentNullException {@code underlying} and/or {@code mapper} are {@code null}.
     */
    public DirectlyMappedList(List<TA> underlying, Func2<TA , TR> mapper)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mapper, "mapper");
        ArgumentNullException.ThrowIfNull(underlying, "underlying");
        this.underlying = underlying;
        this.mapping_function = mapper;
    }

    @Override
    public void clear() { underlying.clear(); }

    @Override
    public int size() { return underlying.size(); }

    @Override
    public boolean isEmpty() { return underlying.isEmpty(); }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) { return underlying.removeAll(c); }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) { return underlying.retainAll(c); }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) { return underlying.containsAll(c); }

    @Override
    public @NotNull Object[] toArray() {
        List<TA> local_list = underlying;
        int size = local_list.size(), I = 0;
        Object[] objects = new Object[size];
        for (; I < size; I++) {
            // Map from TA to TR.
            objects[I] = mapping_function.function(local_list.get(I));
        }
        return objects;
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        List<TA> local_list = underlying;
        int size = local_list.size(), I = 0;
        T[] final_array = (size > a.length) ? (T[])Array.CreateInstance(a.getClass().getComponentType() , size) : a;
        // We need to explicitly cast from TR to T.
        for (; I < size; I++) {
            final_array[I] = (T)mapping_function.function(local_list.get(I));
        }
        return final_array;
    }

    @Override
    public TR get(int index) {
        TA element = underlying.get(index);
        if (element != null) {
            return mapping_function.function(element);
        } else {
            return null;
        }
    }

    @Override
    public TR remove(int index) {
        TA element = underlying.remove(index);
        if (element != null) {
            return mapping_function.function(element);
        } else {
            return null;
        }
    }

    @Override
    public boolean add(TR tr) { throw new UnsupportedOperationException("This operation is not supported."); }

    @Override
    public int indexOf(Object o) { throw new UnsupportedOperationException("This operation is not supported."); }

    @Override
    public boolean remove(Object o) { throw new UnsupportedOperationException("This operation is not supported."); }

    @Override
    public int lastIndexOf(Object o) { throw new UnsupportedOperationException("This operation is not supported."); }

    @Override
    public boolean contains(Object o) { throw new UnsupportedOperationException("This operation is not supported."); }

    @Override
    public TR set(int index, TR element) { throw new UnsupportedOperationException("This operation is not supported."); }

    @Override
    public void add(int index, TR element) { throw new UnsupportedOperationException("This operation is not supported."); }

    @Override
    public @NotNull Iterator<TR> iterator() { return new InternalIterator<>(underlying.listIterator(), mapping_function); }

    @Override
    public @NotNull ListIterator<TR> listIterator() { return new InternalIterator<>(underlying.listIterator() , mapping_function); }

    @Override
    public boolean addAll(@NotNull Collection<? extends TR> c) { throw new UnsupportedOperationException("This operation is not supported."); }

    @Override
    public @NotNull ListIterator<TR> listIterator(int index) { return new InternalIterator<>(underlying.listIterator(index) , mapping_function); }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends TR> c) { throw new UnsupportedOperationException("This operation is not supported."); }

    @Override
    public @NotNull List<TR> subList(int fromIndex, int toIndex) { return new DirectlyMappedList<>(underlying.subList(fromIndex , toIndex) , mapping_function); }

    private record InternalIterator<TA, TR>(ListIterator<TA> original, Func2<TA, TR> mapping_function)
            implements ListIterator<TR>
    {
        @Override
        public TR next() {
            TA element = original.next();
            return (element == null) ? null : mapping_function.function(element);
        }

        @Override
        public TR previous() {
            TA element = original.previous();
            return (element == null) ? null : mapping_function.function(element);
        }

        @Override
        public void remove() { original.remove(); }

        @Override
        public boolean hasNext() { return original.hasNext(); }

        @Override
        public int nextIndex() { return original.nextIndex(); }

        @Override
        public boolean hasPrevious() { return original.hasPrevious(); }

        @Override
        public int previousIndex() { return original.previousIndex(); }

        @Override
        public void set(TR tr) { throw new UnsupportedOperationException("This operation is not supported."); }

        @Override
        public void add(TR tr) { throw new UnsupportedOperationException("This operation is not supported."); }
    }
}
