package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;

public final class IterableToEnumerable<T>
    extends BaseEnumerable<T>
{
    private final Iterable<T> iterable;

    public IterableToEnumerable(@NotNull Iterable<T> iterable) { this.iterable = iterable; }

    @Override
    public IEnumerator<T> GetEnumerator()
    {
        return new FromIteratorEnumerator<>(iterable.iterator());
    }
}
