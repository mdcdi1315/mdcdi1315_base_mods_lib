package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import java.util.Objects;

public final class JavaObjectEqualsEqualityComparer<T>
    implements IEqualityComparer<T>
{
    public JavaObjectEqualsEqualityComparer() { }

    @Override
    public boolean Equals(T x, T y)
    {
        if (x == null) {
            // y == null : Two null objects are always equal and interchangeable
            return y == null;
        } else {
            return x.equals(y);
        }
    }

    @Override
    public int GetHashCode(T obj) {
        return Objects.hashCode(obj);
    }
}
