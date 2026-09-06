package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.basemodslib.utils.collections.HashCodeFunction;

import java.util.Objects;

public record DefaultHashFunction<T>()
    implements HashCodeFunction<T>
{
    @Override
    public int GetHashCode(T object) { return Objects.hashCode(object); }
}
