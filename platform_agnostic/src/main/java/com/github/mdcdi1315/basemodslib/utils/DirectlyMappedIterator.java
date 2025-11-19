package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import java.util.Iterator;

/**
 * Like the {@link DirectlyMappedList} class, this provides an {@link Iterator} implementation that directly maps all its elements to the {@link TR} type.
 * @param <TA> The original type of the elements.
 * @param <TR> The mapped type that will be returned through the {@link #next()} method.
 */
public class DirectlyMappedIterator<TA , TR>
    implements Iterator<TR>
{
    private final Iterator<TA> underlying;
    private final Func2<TA , TR> mapping_function;

    /**
     * Creates a new instance of the {@link DirectlyMappedIterator} class.
     * @param underlying The iterator to map all the elements that will return as of type {@link TR}.
     * @param mapper The mapping function to use for mapping a single {@link TA} element to a {@link TR} element.
     */
    public DirectlyMappedIterator(Iterator<TA> underlying, Func2<TA , TR> mapper)
    {
        this.underlying = underlying;
        this.mapping_function = mapper;
    }

    @Override
    public boolean hasNext() { return underlying.hasNext(); }

    @Override
    public TR next() {
        TA element = underlying.next();
        if (element == null) {
            return null;
        } else {
            return mapping_function.function(element);
        }
    }

    // This is supported if the underlying iterator supports it.
    @Override
    public void remove() { underlying.remove(); }
}
