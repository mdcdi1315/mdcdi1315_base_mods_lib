package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Provides an {@link Iterable} that maps it's elements from the type {@link T} to {@link TR} through a mapping function.
 * @param <T> The type of the source object to be converted to {@link TR}.
 * @param <TR> The type of the object that is converted from {@link T}.
 * @since 1.0.18
 */
public class DirectlyMappedIterable<T, TR>
    implements Iterable<TR>
{
    private final Func2<T, TR> mapper;
    private final Iterable<T> iterable;

    /**
     * Initializes a new instance of the {@link DirectlyMappedIterable} class.
     * @param iterable The source iterable to adapt.
     * @param mapper The function that maps {@link T} objects to {@link TR} objects.
     * @throws ArgumentNullException {@code iterable} and/or {@code mapper} are {@code null}.
     */
    public DirectlyMappedIterable(Iterable<T> iterable, Func2<T, TR> mapper)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.mapper = mapper, "mapper");
        ArgumentNullException.ThrowIfNull(this.iterable = iterable, "iterable");
    }

    @Override
    public Iterator<TR> iterator() { return new DirectlyMappedIterator<>(this.iterable.iterator(), this.mapper); }

    @Override
    public void forEach(Consumer<? super TR> action)
    {
        Iterator<T> iterator = this.iterable.iterator();
        while (iterator.hasNext()) {
            action.accept(mapper.function(iterator.next()));
        }
    }

    /**
     * Gets the function that maps objects of {@link T} to type {@link TR}.
     * @return The mapping function.
     */
    @NotNull
    public Func2<T, TR> GetMapper() { return mapper; }
}
