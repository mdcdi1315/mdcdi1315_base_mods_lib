package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.DotNetLayer.System.IComparable;

/**
 * Comparer implementation depending on the {@link IComparable} implementation of type {@link T}.
 * @param <T> The type of the object that is comparable.
 */
public final class ComparableComparer<T extends IComparable<T>>
    extends AbstractObjectComparer<T>
{
    @Override
    protected int CompareImpl(T x, T y) { return x.CompareTo(y); }
}
