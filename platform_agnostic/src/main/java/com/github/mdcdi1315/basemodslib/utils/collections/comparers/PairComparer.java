package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer;

import com.github.mdcdi1315.basemodslib.utils.Pair;

/**
 * Provides an {@link AbstractObjectComparer} implementation
 * for comparing {@link Pair}s of specified types.
 * @param <T1> The first type of the pair to compare.
 * @param <T2> The second type of the pair to compare.
 */
public final class PairComparer<T1, T2>
    extends AbstractObjectComparer<Pair<T1, T2>>
{
    private final IComparer<T1> comparer_1;
    private final IComparer<T2> comparer_2;

    /**
     * Initializes a new instance of the {@link PairComparer} class. <br />
     * It requires the individual comparers for both {@link T1} and {@link T2}.
     * @param comparer_1 The first comparer that compares {@link T1} instances.
     * @param comparer_2 The second comparer that compares {@link T2} instances.
     * @throws ArgumentNullException {@code comparer_1} and/or {@code comparer_2} are {@code null}.
     */
    public PairComparer(IComparer<T1> comparer_1, IComparer<T2> comparer_2)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.comparer_1 = comparer_1, "comparer_1");
        ArgumentNullException.ThrowIfNull(this.comparer_2 = comparer_2, "comparer_2");
    }

    @Override
    protected int CompareImpl(Pair<T1, T2> x, Pair<T1, T2> y)
    {
        return IntComparer.INSTANCE.Compare(
                comparer_1.Compare(x.first(), y.first()),
                comparer_2.Compare(x.second(), y.second())
        );
    }
}
