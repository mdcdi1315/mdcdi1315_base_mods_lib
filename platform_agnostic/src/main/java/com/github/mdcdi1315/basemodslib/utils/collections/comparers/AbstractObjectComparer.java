package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

/**
 * An easing class for validating the input arguments against {@code null}
 * before calling the actual comparison implementation.
 * @param <T> The type of objects to compare.
 */
public abstract class AbstractObjectComparer<T>
    implements IComparer<T>
{
    /**
     * Provides the implementation of the comparison operation. <br />
     * Implementers provide an implementation of this method to define
     * the actual comparison result when both arguments are non-{@code null}.
     * @param x The first object to compare.
     * @param y The second object to compare.
     * @return A signed integer that indicates the relative values of {@code x} and {@code y}.
     * @see IComparer#Compare(Object, Object)
     */
    protected abstract int CompareImpl(@DisallowNull T x, @DisallowNull T y);

    /**
     * {@inheritDoc}
     */
    public final int Compare(@AllowNull T x, @AllowNull T y)
    {
        boolean y_is_null = y == null;
        return (x == null) ? (y_is_null ? 0 : -1) : (y_is_null ? 1 : CompareImpl(x, y));
    }
}
