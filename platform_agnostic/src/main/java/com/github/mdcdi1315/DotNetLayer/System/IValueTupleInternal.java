package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Collections.IEqualityComparer;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.ITuple;

/**
 * Helper so we can call some tuple methods recursively without knowing the underlying types.
 */
interface IValueTupleInternal
        extends ITuple
{
    int GetHashCode(IEqualityComparer comparer);

    String ToStringEnd();

    @Override
    default void setItem(Integer integer, Object o) { }
}
