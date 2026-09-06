package com.github.mdcdi1315.basemodslib.utils.collections.comparers;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;
import com.github.mdcdi1315.basemodslib.utils.collections.HashCodeFunction;

/**
 * An easing class for validating the input arguments against {@code null}
 * before calling the actual equality implementation. <br />
 * It additionally implements the {@link HashCodeFunction} interface.
 * @param <T> The type of objects to compare.
 */
public abstract class AbstractObjectEqualityComparer<T>
    implements IEqualityComparer<T>, HashCodeFunction<T>
{
    /**
     * Computes the hash code of {@code obj}.
     * @param obj The object to compute its hash code.
     * @return The hash code value of {@code obj}.
     * @apiNote This defines the actual implementation of the {@link #GetHashCode(Object)} method,
     *          however this method executes only when {@code obj} is non-null.
     */
    protected abstract int GetHashCodeImpl(@DisallowNull T obj);

    /**
     * Determines whether {@code x} and {@code y} objects are equal to each other.
     * @param x The first object of type {@link T} to compare.
     * @param y The second object of type {@link T} to compare.
     * @return {@code true} if the specified objects are equal; otherwise, {@code false}.
     */
    protected abstract boolean EqualsImpl(@DisallowNull T x, @DisallowNull T y);

    @Override
    public final boolean Equals(@AllowNull T x, @AllowNull T y)
    {
        boolean y_is_null = y == null;
        return x == null ? y_is_null : ((!y_is_null) && EqualsImpl(x, y));
    }

    @Override
    public final int GetHashCode(T obj) { return (obj == null) ? 0 : GetHashCodeImpl(obj); }
}
