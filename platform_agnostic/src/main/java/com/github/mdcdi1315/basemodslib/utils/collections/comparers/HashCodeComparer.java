package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

/**
 * Provides an {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer} implementation
 * that computes the comparison of two objects by their hash codes.
 * @param <T> The type of objects to compare.
 */
public final class HashCodeComparer<T>
    extends AbstractObjectComparer<T>
{
    @Override
    protected int CompareImpl(T x, T y) { return x.hashCode() - y.hashCode(); }
}
