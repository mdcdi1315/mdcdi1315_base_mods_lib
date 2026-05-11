package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.SkippingEnumerator;

public final class SkipEnumerable<T>
        extends BaseEnumerable<T>
{
    private final int n_skip;
    private final IEnumerable<T> enumerable;

    public SkipEnumerable(IEnumerable<T> enumerable, int count) { this.enumerable = enumerable; n_skip = count; }

    @Override
    public IEnumerator<T> GetEnumerator() {
        return new SkippingEnumerator<>(enumerable.GetEnumerator(), n_skip);
    }
}