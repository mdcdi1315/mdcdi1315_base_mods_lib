package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.MappingEnumerator;
import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;

public final class ConvertAllEnumerable<TI, TO>
    extends BaseEnumerable<TO>
{
    private final Converter<TI, TO> converter;
    private final BaseEnumerable<TI> enumerable;

    public ConvertAllEnumerable(BaseEnumerable<TI> enumerable, Converter<TI, TO> converter)
    {
        this.converter = converter;
        this.enumerable = enumerable;
    }

    @Override
    public IEnumerator<TO> GetEnumerator() { return new MappingEnumerator<>(enumerable.GetEnumerator(), converter); }

    @Override
    public <TG> BaseEnumerable<TG> ConvertAll(Converter<TO, TG> converter, IEqualityComparer<TG> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");
        return enumerable.ConvertAll(
                FunctionManipulations.ThenMap(this.converter, converter),
                comparer
        );
    }

    @Override
    public <TG> BaseEnumerable<TG> ConvertAll(Converter<TO, TG> converter)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");
        return enumerable.ConvertAll(
                FunctionManipulations.ThenMap(this.converter, converter)
        );
    }

    @Override
    public ConvertAllEnumerable<TI, TO> Slice(int index, int count)
            throws ArgumentException, ArgumentOutOfRangeException
    {
        return new ConvertAllEnumerable<>(enumerable.Slice(index, count), converter);
    }

    @Override
    public ConvertAllEnumerable<TI, TO> Slice(int count)
            throws ArgumentException
    {
        return new ConvertAllEnumerable<>(enumerable.Slice(count), converter);
    }
}
