package com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IShortEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IShortEnumerator;

public record CastToIntFromShortEnumerable(IShortEnumerable enumerable)
    implements IIntEnumerable
{
    private record Enumerator(IShortEnumerator enumerator)
        implements IIntEnumerator
    {
        @Override
        public Integer getCurrent() { return enumerator.getCurrent().intValue(); }

        @Override
        public int getUncastedCurrent() { return enumerator.getUncastedCurrent(); }

        @Override
        public void Reset() throws InvalidOperationException { enumerator.Reset(); }

        @Override
        public boolean MoveNext() throws InvalidOperationException { return enumerator.MoveNext(); }

        @Override
        public void Dispose() { enumerator.Dispose(); }
    }

    @Override
    public IIntEnumerator GetEnumerator() { return new Enumerator(enumerable.GetEnumerator()); }
}
