package com.github.mdcdi1315.basemodslib.utils.random.weighted;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.random.IRandomLookup;
import com.github.mdcdi1315.basemodslib.utils.random.IRandomSource;

import java.util.Optional;

/**
 * Provides the default {@link IRandomLookup} implementation for all the
 * weighted instances, honoring the weights of each element.
 * @param <T> The type of elements that are contained in the provided list.
 * @param <TC> The type of the collection. The collection must implement {@link IEnumerable} and {@link IWeightedList}.
 */
public final class WeightedRandomLookup<
    T extends IWeightedEntry,
    TC extends IEnumerable<T> & IWeightedList
>
    implements IRandomLookup<T>
{
    private final TC list;
    private final IRandomSource random;

    /**
     * Initializes a new instance of the {@link WeightedRandomLookup} class.
     * @param random The random source to use for getting random elements from the list.
     * @param list The weighted list object from which weighted random elements are picked.
     * @throws ArgumentNullException {@code random} and/or {@code list} are {@code null}.
     */
    public WeightedRandomLookup(IRandomSource random, TC list)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(list, "list");
        ArgumentNullException.ThrowIfNull(random, "random");
        this.list = list;
        this.random = random;
    }

    @Pure
    @NotNull
    @Override
    public TC GetCollection() { return list; }

    @Pure
    @NotNull
    @Override
    public IRandomSource GetRandomSource() { return random; }

    @NotNull
    @Override
    public Optional<T> GetRandomElement() { return WeightedRandomUtils.PickRandomElement(random, list, list.GetTotalWeight()); }
}
