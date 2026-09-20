package com.github.mdcdi1315.basemodslib.utils.random;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;

import java.util.Optional;

record TraversableCollectionRandomLookup<T>(IRandomSource random, ITraversableCollection<T> collection)
    implements IRandomLookup<T>
{
    @Pure
    @NotNull
    @Override
    public IRandomSource GetRandomSource() { return random; }

    @Pure
    @NotNull
    @Override
    public IEnumerable<T> GetCollection() { return collection; }

    @NotNull
    @Override
    public Optional<T> GetRandomElement()
    {
        int count = collection.GetCount();
        return (count == 0) ? Optional.empty() :
                Optional.ofNullable(collection.GetItem(RandomUtils.NextIntInRangeExclusive(random, 0, count)));
    }
}
