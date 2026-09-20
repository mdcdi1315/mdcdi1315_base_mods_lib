package com.github.mdcdi1315.basemodslib.utils.random;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IReadOnlyList;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

import java.util.Optional;

record IReadOnlyListRandomLookup<T>(IRandomSource random, IReadOnlyList<T> list)
    implements IRandomLookup<T>
{
    @Pure
    @NotNull
    @Override
    public IEnumerable<T> GetCollection() { return list; }

    @Pure
    @NotNull
    @Override
    public IRandomSource GetRandomSource() { return random; }

    @NotNull
    @Override
    public Optional<T> GetRandomElement()
    {
        int count = list.getCount();
        return (count == 0) ? Optional.empty() :
                Optional.ofNullable(list.getItem(RandomUtils.NextIntInRangeExclusive(random, 0, count)));
    }
}
