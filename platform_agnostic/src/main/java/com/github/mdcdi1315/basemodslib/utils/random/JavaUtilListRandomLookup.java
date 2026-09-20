package com.github.mdcdi1315.basemodslib.utils.random;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;

import java.util.Optional;

record JavaUtilListRandomLookup<T>(IRandomSource random, java.util.List<T> list)
        implements IRandomLookup<T>
{
    @NotNull
    @Override
    public IEnumerable<T> GetCollection() { return CollectionManipulations.AsList(list); }

    @NotNull
    @Override
    public Optional<T> GetRandomElement()
    {
        int count = list.size();
        return (count == 0) ? Optional.empty() :
                Optional.ofNullable(list.get(RandomUtils.NextIntInRangeExclusive(random, 0, count)));
    }

    @Pure
    @NotNull
    @Override
    public IRandomSource GetRandomSource() { return random; }
}
