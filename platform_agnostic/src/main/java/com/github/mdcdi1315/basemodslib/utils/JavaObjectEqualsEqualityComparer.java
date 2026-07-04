package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import java.util.Objects;

/**
 * {@link IEqualityComparer} implementation for Java objects in general.
 * @param <T> The type of the Java object to be compared.
 */
public record JavaObjectEqualsEqualityComparer<T>()
    implements IEqualityComparer<T>, ISynchronized
{
    @Override
    public boolean Equals(T x, T y) { return Objects.equals(x, y); }

    @Override
    public int GetHashCode(T obj) { return Objects.hashCode(obj); }
}
