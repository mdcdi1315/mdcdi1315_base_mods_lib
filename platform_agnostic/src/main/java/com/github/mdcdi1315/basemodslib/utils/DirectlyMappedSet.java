package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Array;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Provides a {@link Set} implementation that maps all it's elements to type {@link TR} type before returning them. <br />
 * This is extremely useful for gaining the advantage of the {@link Collection#addAll(Collection)} method, but not having the issue of 'how to map {@link TA} to {@link TR} and use this?'. <br />
 * Note also, that methods that return or put elements of type {@link TR} throw the {@link UnsupportedOperationException} class because there is not a way to map {@link TR} to {@link TA}.
 * @param <TA> The original type of the set.
 * @param <TR> The mapped type to be returned through various methods of this class.
 * @since 1.0.20
 */
public class DirectlyMappedSet<TA, TR>
    implements Set<TR>
{
    private final Set<TA> underlying;
    private final Func2<TA , TR> mapping_function;

    /**
     * Initializes a new instance of the {@link DirectlyMappedSet} class.
     * @param underlying The underlying set to map its elements.
     * @param mapper The mapping function to use that will map a single {@link TA} element to a {@link TR} element.
     * @throws ArgumentNullException {@code underlying} and/or {@code mapper} are {@code null}.
     */
    public DirectlyMappedSet(Set<TA> underlying, Func2<TA , TR> mapper)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mapper, "mapper");
        ArgumentNullException.ThrowIfNull(underlying, "underlying");
        this.underlying = underlying;
        this.mapping_function = mapper;
    }

    @Override
    public boolean removeIf(@NotNull Predicate<? super TR> filter)
    {
        Iterator<TA> i = underlying.iterator();
        boolean removed = false;
        while (i.hasNext()) {
            if (filter.test(mapping_function.function(i.next()))) { i.remove(); removed = true; }
        }
        return removed;
    }

    @Override
    public @NotNull Object[] toArray()
    {
        Set<TA> local_set = underlying;
        int I = 0;
        Object[] array = new Object[local_set.size()];
        for (TA ta : local_set) { array[I++] = ta; }
        return array;
    }

    @Override
    public @NotNull <T> T[] toArray(@NotNull T[] a)
    {
        Set<TA> local_set = underlying;
        int size = local_set.size(), I = 0;
        T[] final_array = (size > a.length) ? (T[]) Array.CreateInstance(a.getClass().getComponentType() , size) : a;
        // We need to explicitly cast from TR to T.
        for (TA e : local_set) { final_array[I++] = (T)mapping_function.function(e); }
        return final_array;
    }

    @Override
    public void clear() { underlying.clear(); }

    @Override
    public int size() { return underlying.size(); }

    @Override
    public boolean isEmpty() { return underlying.isEmpty(); }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) { return underlying.containsAll(c); }

    @Override
    public boolean add(TR tr) { throw new UnsupportedOperationException("This method is not supported."); }

    @Override
    public boolean remove(Object o) { throw new UnsupportedOperationException("This method is not supported."); }

    @Override
    public boolean contains(Object o) { throw new UnsupportedOperationException("This method is not supported."); }

    @Override
    public @NotNull Iterator<TR> iterator() { return new DirectlyMappedIterator<>(underlying.iterator(), mapping_function); }

    @Override
    public Spliterator<TR> spliterator() { return new DirectlyMappedSpliterator<>(underlying.spliterator(), mapping_function); }

    @Override
    public void forEach(Consumer<? super TR> action) { for (TA ta : underlying) { action.accept(mapping_function.function(ta)); } }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) { throw new UnsupportedOperationException("This method is not supported."); }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) { throw new UnsupportedOperationException("This method is not supported."); }

    @Override
    public boolean addAll(@NotNull Collection<? extends TR> c) { throw new UnsupportedOperationException("This method is not supported."); }
}
