package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.DisposableIterator;

import java.util.function.Consumer;

public record DisposableIteratorImplFromEnumerator<T>(IEnumerator<T> enumerator)
    implements DisposableIterator<T>
{
    @Override
    public void close() { enumerator.Dispose(); }

    @Override
    public T next() { return enumerator.getCurrent(); }

    @Override
    public boolean hasNext() { return enumerator.MoveNext(); }

    @Override
    public void forEachRemaining(Consumer<? super T> action)
    {
        while (enumerator.MoveNext())
        {
            action.accept(enumerator.getCurrent());
        }
    }
}
