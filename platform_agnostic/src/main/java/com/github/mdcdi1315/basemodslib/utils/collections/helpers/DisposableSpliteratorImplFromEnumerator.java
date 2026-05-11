package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.DisposableSpliterator;

import java.util.Spliterator;
import java.util.function.Consumer;

public record DisposableSpliteratorImplFromEnumerator<T>(IEnumerator<T> enumerator)
    implements DisposableSpliterator<T>
{
    @Override
    public void close() { enumerator.Dispose(); }

    @Override
    public boolean tryAdvance(Consumer<? super T> action)
    {
        if (enumerator.MoveNext()) {
            action.accept(enumerator.getCurrent());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public long estimateSize() { return 0; }

    @Override
    public Spliterator<T> trySplit() { return null; }

    @Override
    public int characteristics() { return ORDERED | IMMUTABLE; }
}
