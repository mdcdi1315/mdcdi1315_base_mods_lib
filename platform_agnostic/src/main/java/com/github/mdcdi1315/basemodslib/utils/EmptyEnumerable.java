package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.IEmptyEnumerable;

/**
 * Special {@link IEnumerable} implementation for returning empty {@link IEnumerable} instances. <br />
 * (That is, instances of this do not return any collection elements.)
 * @param <T> The type of the elements that would be returned, if the enumerator had any elements within.
 */
public record EmptyEnumerable<T>()
    implements IEnumerable<T>, IEmptyEnumerable, ISynchronized
{
    @Override
    public IEnumerator<T> GetEnumerator() { return new EmptyEnumerator<>(); }

    @Override
    public boolean equals(Object obj) { return obj instanceof EmptyEnumerable; }
}
