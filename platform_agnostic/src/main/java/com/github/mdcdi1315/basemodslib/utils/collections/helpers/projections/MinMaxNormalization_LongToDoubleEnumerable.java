package com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.*;

public record MinMaxNormalization_LongToDoubleEnumerable(ILongEnumerable enumerable, long min, long max)
        implements IDoubleEnumerable
{
    private record Enumerator(ILongEnumerator enumerator, double min, double max)
            implements IDoubleEnumerator
    {
        @Override
        public Double getCurrent() { return Extensions.ToNormalRange(enumerator.getCurrent(), min, max); }

        @Override
        public double getUncastedCurrent() { return Extensions.ToNormalRange(enumerator.getUncastedCurrent(), min, max); }

        @Override
        public void Reset() throws InvalidOperationException { enumerator.Reset(); }

        @Override
        public boolean MoveNext() throws InvalidOperationException { return enumerator.MoveNext(); }

        @Override
        public void Dispose() { enumerator.Dispose(); }
    }

    @Override
    public IDoubleEnumerator GetEnumerator() { return new Enumerator(enumerable.GetEnumerator(), min, max); }
}

