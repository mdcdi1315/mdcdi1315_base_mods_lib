package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.SlicingEnumerator;

public final class SliceEnumerable<T>
        extends BaseEnumerable<T>
{
    private final int index, count;
    private final IEnumerable<T> enumerable;

    public SliceEnumerable(IEnumerable<T> enumerable, int index, int count)
    {
        this.enumerable = enumerable;
        this.index = index;
        this.count = count;
    }

    @Override
    public IEnumerator<T> GetEnumerator() {
        return new SlicingEnumerator<>(enumerable.GetEnumerator(), index, count);
    }
}