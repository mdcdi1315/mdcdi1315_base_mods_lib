package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Converter;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.MappingEnumerator;

public final class MapToMethodEnumerable<TS, TR>
        extends BaseEnumerable<TR>
{
    private final Converter<TS, TR> converter;
    private final IEnumerable<TS> enumerable;

    public MapToMethodEnumerable(IEnumerable<TS> enumerable, Converter<TS, TR> converter)
    {
        this.converter = converter;
        this.enumerable = enumerable;
    }

    @Override
    public IEnumerator<TR> GetEnumerator() {
        return new MappingEnumerator<>(enumerable.GetEnumerator(), converter);
    }
}