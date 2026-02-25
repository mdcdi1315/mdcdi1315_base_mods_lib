package com.github.mdcdi1315.basemodslib.utils;

import java.util.Iterator;

/**
 * A rather simple record class providing empty iterators.
 * @param <T> The type of elements that would be enumerated, if this iterator was not empty.
 * @since 1.0.15
 */
public record EmptyIterator<T>()
    implements Iterator<T>, ISynchronized
{
    @Override
    public boolean hasNext() { return false; }

    @Override
    public T next() { return null; }
}
