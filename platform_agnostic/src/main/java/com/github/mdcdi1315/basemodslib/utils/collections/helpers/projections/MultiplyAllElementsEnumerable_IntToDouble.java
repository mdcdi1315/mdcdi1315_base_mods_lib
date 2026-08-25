package com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IDoubleEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IDoubleEnumerator;

public record MultiplyAllElementsEnumerable_IntToDouble(IIntEnumerable enumerable, double value)
    implements IDoubleEnumerable
{
    private record Enumerator(IIntEnumerator enumerator, double value)
        implements IDoubleEnumerator
    {
        @Override
        public Double getCurrent() { return enumerator.getCurrent() * value; }

        @Override
        public void Reset() throws InvalidOperationException { enumerator.Reset(); }

        @Override
        public double getUncastedCurrent() { return enumerator.getUncastedCurrent() * value; }

        @Override
        public boolean MoveNext() throws InvalidOperationException { return enumerator.MoveNext(); }

        @Override
        public void Dispose() { enumerator.Dispose(); }
    }

    @Override
    public IDoubleEnumerator GetEnumerator() { return new Enumerator(enumerable.GetEnumerator(), value); }
}
