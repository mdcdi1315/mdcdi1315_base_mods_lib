package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.FilteringEnumerator;

public final class FilterByEnumerable<T>
        extends BaseEnumerable<T>
{
    private final Predicate<T> predicate;
    private final IEnumerable<T> enumerable;

    public FilterByEnumerable(IEnumerable<T> enumerable, Predicate<T> predicate)
    {
        this.predicate = predicate;
        this.enumerable = enumerable;
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return new FilteringEnumerator<>(enumerable.GetEnumerator(), predicate); }
}
