package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Special {@link IEnumerator} implementation for returning empty {@link IEnumerator} instances. <br />
 * (That is, instances of this do not return any elements.)
 * @param <T> The type of the elements that would be returned, if the enumerator had any elements within.
 * @since 1.0.26
 */
public record EmptyEnumerator<T>()
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