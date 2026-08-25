package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.EmptyEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.IEmptyEnumerable;

public final class EmptyBaseEnumerable<T>
    extends BaseEnumerable<T>
    implements IEmptyEnumerable
{
    @Override
    public EmptyEnumerator<T> GetEnumerator() { return new EmptyEnumerator<>(); }

    @Override
    public EmptyBaseEnumerable<T> Slice(int count) throws ArgumentException { return this; }

    @Override
    public EmptyBaseEnumerable<T> Slice(int index, int count) throws ArgumentException { return this; }

    @Override
    public <TO> EmptyBaseEnumerable<TO> ConvertAll(Converter<T, TO> converter)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");
        return new EmptyBaseEnumerable<>();
    }

    @Override
    public <TO> EmptyBaseEnumerable<TO> ConvertAll(Converter<T, TO> converter, IEqualityComparer<TO> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");
        return new EmptyBaseEnumerable<>();
    }

    @Override
    public EmptyBaseEnumerable<T> FilterBy(Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        return this;
    }
}
