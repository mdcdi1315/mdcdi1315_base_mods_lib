package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.ConcatenatingEnumerator;

public final class ConcatenatingEnumerable<T>
        extends BaseEnumerable<T>
{
    private final IEnumerable<T> first, second;

    public ConcatenatingEnumerable(IEnumerable<T> first, IEnumerable<T> second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return new ConcatenatingEnumerator<>(first.GetEnumerator(), second.GetEnumerator()); }
}