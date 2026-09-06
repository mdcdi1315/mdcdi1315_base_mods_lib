package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

/**
 * Comparer implementation depending on the {@link Comparable} implementation of type {@link T}.
 * @param <T> The type of the object that is comparable.
 */
public final class JavaComparableComparer<T extends Comparable<T>>
    extends AbstractObjectComparer<T>
{
    @Override
    protected int CompareImpl(T x, T y) { return x.compareTo(y); }
}
