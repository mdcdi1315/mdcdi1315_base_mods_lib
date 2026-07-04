package com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.collections.projections.*;

public record CastToDoubleFromFloatEnumerable(IFloatEnumerable enumerable)
    implements IDoubleEnumerable
{
    private record Enumerator(IFloatEnumerator enumerator)
        implements IDoubleEnumerator
    {
        @Override
        public Double getCurrent() { return (double)enumerator.getCurrent(); }

        @Override
        public double getUncastedCurrent() { return enumerator.getUncastedCurrent(); }

        @Override
        public void Reset() throws InvalidOperationException { enumerator.Reset(); }

        @Override
        public boolean MoveNext() throws InvalidOperationException { return enumerator.MoveNext(); }

        @Override
        public void Dispose() { enumerator.Dispose(); }
    }

    @Override
    public IDoubleEnumerator GetEnumerator() { return new Enumerator(enumerable.GetEnumerator()); }
}
