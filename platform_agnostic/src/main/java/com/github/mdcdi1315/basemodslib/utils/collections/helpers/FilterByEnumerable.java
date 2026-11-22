package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;
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

    @Override
    public BaseEnumerable<T> FilterBy(Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        var p = FunctionManipulations.Combine(this.predicate, predicate);
        if (FunctionManipulations.IsAlwaysTrue(p)) {
            return this;
        } else if (FunctionManipulations.IsAlwaysFalse(p)) {
            return new EmptyBaseEnumerable<>();
        } else {
            return new FilterByEnumerable<>(enumerable, p);
        }
    }
}
