package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Special {@link IEnumerable} implementation for returning empty {@link IEnumerable} instances. <br />
 * (That is, instances of this do not return any collection elements.)
 * @param <T> The type of the elements to return.
 */
public record EmptyEnumerable<T>()
    implements IEnumerable<T>, ISynchronized
{
    private record EmptyEnumerator<T>()
        implements IEnumerator<T>, ISynchronized
    {
        @Override
        public T getCurrent() { return null; }

        @Override
        public boolean MoveNext() { return false; }

        @Override
        public void Reset() {}

        @Override
        public void Dispose() {}
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return new EmptyEnumerator<>(); }

    @Override
    public boolean equals(Object obj) { return obj instanceof EmptyEnumerable; }
}
