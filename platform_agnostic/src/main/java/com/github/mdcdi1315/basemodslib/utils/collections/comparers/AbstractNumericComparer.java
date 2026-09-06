package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.HashCodeFunction;

import java.util.Objects;

/**
 * Provides a {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer} and
 * {@link IEqualityComparer} base class for {@link Number} values.
 * @param <T> The {@link Number} type.
 */
public abstract class AbstractNumericComparer<T extends Number>
    extends AbstractObjectComparer<T>
    implements IEqualityComparer<T>, HashCodeFunction<T>
{
    /**
     * {@inheritDoc}
     * @implNote The default implementation of this
     * method forwards to the {@link AbstractObjectComparer#Compare(Object, Object)}
     * method, and it tests the return value against zero.
     */
    @Override
    public final boolean Equals(@AllowNull T x, @AllowNull T y)
    {
        boolean x_is_null = x == null;
        boolean y_is_null = y == null;
        return x_is_null ? y_is_null : ((!y_is_null) && EqualsImpl(x, y));
    }

    /**
     * Provides the actual definition that determines whether
     * the specified numeric objects are equal.
     * @param x The first object of type {@link T} to compare.
     * @param y The second object of type {@link T} to compare.
     * @return {@code true} if the specified objects are equal; otherwise, {@code false}.
     * @implNote The default implementation of this method calls the
     * {@link AbstractObjectComparer#CompareImpl(Object, Object)}
     * and performs a check against zero, which is the return value.
     */
    protected boolean EqualsImpl(@DisallowNull T x, @DisallowNull T y) { return CompareImpl(x, y) == 0; }

    @Pure
    @Override
    public int GetHashCode(T obj) { return Objects.hashCode(obj); }
}
