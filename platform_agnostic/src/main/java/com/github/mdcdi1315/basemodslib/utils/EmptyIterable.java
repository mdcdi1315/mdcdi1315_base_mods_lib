package com.github.mdcdi1315.basemodslib.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Like the {@link EmptyEnumerable} class, this class does provide an empty iterable collection.
 * @param <T> The type of elements that would be enumerated, if this iterable was not empty.
 * @since 1.0.15
 */
public record EmptyIterable<T>()
    implements Iterable<T>
{
    @Override
    public @NotNull Iterator<T> iterator() { return new EmptyIterator<>(); }
}
