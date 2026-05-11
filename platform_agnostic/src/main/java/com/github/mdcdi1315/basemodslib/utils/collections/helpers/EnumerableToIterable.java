package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.DisposableIterator;
import com.github.mdcdi1315.basemodslib.utils.collections.DisposableSpliterator;
import com.github.mdcdi1315.basemodslib.utils.collections.IterableWithDisposableIterator;

import java.util.function.Consumer;

public record EnumerableToIterable<T>(IEnumerable<T> enumerable)
            implements IterableWithDisposableIterator<T>
{
    @Override
    public void forEach(Consumer<? super T> action)
    {
        IEnumerator<T> enumerator = enumerable.GetEnumerator();
        try {
            while (enumerator.MoveNext()) {
                action.accept(enumerator.getCurrent());
            }
        } finally {
            enumerator.Dispose();
        }
    }

    @NotNull
    @Override
    public DisposableIterator<T> iterator() { return DisposableIterator.FromEnumerator(enumerable.GetEnumerator()); }

    @NotNull
    @Override
    public DisposableSpliterator<T> spliterator() { return DisposableSpliterator.FromEnumerator(enumerable.GetEnumerator()); }
}