package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import java.util.Objects;

public final class JavaObjectEqualsEqualityComparer<T>
    implements IEqualityComparer<T>
{
    public JavaObjectEqualsEqualityComparer() { }

    @Override
    public boolean Equals(T x, T y) {
        return Objects.equals(x, y);
    }

    @Override
    public int GetHashCode(T obj) {
        return Objects.hashCode(obj);
    }
}
