package com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.*;

public record DivideAllElementsEnumerable_IntToDouble(IIntEnumerable enumerable, double value)
    implements IDoubleEnumerable
{
    private record Enumerator(IIntEnumerator enumerator, double div_value)
            implements IDoubleEnumerator
    {
        @Override
        public Double getCurrent() { return enumerator.getCurrent() / div_value; }

        @Override
        public void Reset() throws InvalidOperationException { enumerator.Reset(); }

        @Override
        public double getUncastedCurrent() { return enumerator.getUncastedCurrent() / div_value; }

        @Override
        public boolean MoveNext() throws InvalidOperationException { return enumerator.MoveNext(); }

        @Override
        public void Dispose() { enumerator.Dispose(); }
    }

    @Override
    public IDoubleEnumerator GetEnumerator() { return new Enumerator(enumerable.GetEnumerator(), value); }
}
