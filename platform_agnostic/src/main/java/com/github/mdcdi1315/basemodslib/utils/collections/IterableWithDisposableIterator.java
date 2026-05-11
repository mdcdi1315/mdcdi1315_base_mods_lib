package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.helpers.DisposableSpliteratorFromIterator;

/**
 * Provides an interface extending {@link Iterable} for exposing the disposable iterator and spliterator interfaces to user code.
 * @param <T> The type of elements returned by the iterator
 * @since 1.0.31
 */
public interface IterableWithDisposableIterator<T>
    extends Iterable<T>
{
    /**
     * {@inheritDoc}
     */
    @NotNull
    DisposableIterator<T> iterator();

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default DisposableSpliterator<T> spliterator() { return new DisposableSpliteratorFromIterator<>(iterator()); }
}
