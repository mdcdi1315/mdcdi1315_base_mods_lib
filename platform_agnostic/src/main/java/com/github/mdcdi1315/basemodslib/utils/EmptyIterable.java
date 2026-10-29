package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.util.Iterator;

/**
 * Like the {@link EmptyEnumerable} class, this class does provide an empty iterable collection.
 * @param <T> The type of elements that would be enumerated, if this iterable was not empty.
 * @since 1.0.15
 */
public record EmptyIterable<T>()
    implements Iterable<T>, ISynchronized
{
    @NotNull
    @Override
    @SuppressWarnings("NullableProblems")
    public Iterator<T> iterator() { return new EmptyIterator<>(); }
}
