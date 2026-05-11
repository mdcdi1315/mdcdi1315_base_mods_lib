package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.basemodslib.utils.collections.DisposableIterator;
import com.github.mdcdi1315.basemodslib.utils.collections.DisposableSpliterator;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public final class DisposableSpliteratorFromIterator<T>
    implements DisposableSpliterator<T>
{
    private final Spliterator<T> wrapping;
    private final DisposableIterator<T> iterator;

    public DisposableSpliteratorFromIterator(DisposableIterator<T> iterator)
    {
        this.iterator = iterator;
        wrapping = Spliterators.spliteratorUnknownSize(iterator, Spliterator.IMMUTABLE | Spliterator.ORDERED);
    }

    @Override
    public void close() { iterator.close(); }

    @Override
    public long estimateSize() { return wrapping.estimateSize(); }

    @Override
    public Spliterator<T> trySplit() { return wrapping.trySplit(); }

    @Override
    public int characteristics() { return wrapping.characteristics(); }

    @Override
    public long getExactSizeIfKnown() { return wrapping.getExactSizeIfKnown(); }

    @Override
    public Comparator<? super T> getComparator() { return wrapping.getComparator(); }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) { return wrapping.tryAdvance(action); }

    @Override
    public void forEachRemaining(Consumer<? super T> action) { wrapping.forEachRemaining(action); }

    @Override
    public boolean hasCharacteristics(int characteristics) { return wrapping.hasCharacteristics(characteristics); }
}
