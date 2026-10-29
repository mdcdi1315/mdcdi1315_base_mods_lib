package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer;

/**
 * An {@link IComparer} implementation that is backed
 * by a {@link java.util.Comparator} instance.
 * @param <T> The type of the objects to compare.
 */
public final class JavaComparatorComparer<T>
    implements IComparer<T>
{
    private final java.util.Comparator<T> comparator;

    public JavaComparatorComparer(java.util.Comparator<T> comparator)
    {
        ArgumentNullException.ThrowIfNull(comparator, "comparator");
        this.comparator = comparator;
    }

    @Override
    public int Compare(T x, T y) { return comparator.compare(x, y); }
}
