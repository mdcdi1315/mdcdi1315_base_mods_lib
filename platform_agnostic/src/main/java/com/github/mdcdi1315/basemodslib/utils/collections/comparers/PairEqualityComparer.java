package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.DotNetLayer.System.HashCode;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.Pair;

/**
 * Provides an {@link AbstractObjectEqualityComparer} implementation
 * for comparing {@link Pair}s of specified types.
 * @param <T1> The first type of the pair to compare.
 * @param <T2> The second type of the pair to compare.
 */
public final class PairEqualityComparer<T1, T2>
    extends AbstractObjectEqualityComparer<Pair<T1, T2>>
{
    private final IEqualityComparer<T1> comparer_1;
    private final IEqualityComparer<T2> comparer_2;

    /**
     * Initializes a new instance of the {@link PairComparer} class. <br />
     * It requires the individual equality comparers for both {@link T1} and {@link T2}.
     * @param comparer_1 The first equality comparer that compares {@link T1} instances.
     * @param comparer_2 The second equality comparer that compares {@link T2} instances.
     * @throws ArgumentNullException {@code comparer_1} and/or {@code comparer_2} are {@code null}.
     */
    public PairEqualityComparer(IEqualityComparer<T1> comparer_1, IEqualityComparer<T2> comparer_2)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.comparer_1 = comparer_1, "comparer_1");
        ArgumentNullException.ThrowIfNull(this.comparer_2 = comparer_2, "comparer_2");
    }

    @Override
    protected int GetHashCodeImpl(Pair<T1, T2> obj)
    {
        HashCode hc = new HashCode();
        hc.Add(obj.first(), comparer_1);
        hc.Add(obj.second(), comparer_2);
        return hc.ToHashCode();
    }

    @Override
    protected boolean EqualsImpl(Pair<T1, T2> x, Pair<T1, T2> y)
    {
        return
                comparer_1.Equals(x.first(), y.first()) &&
                comparer_2.Equals(x.second(), y.second());
    }
}
