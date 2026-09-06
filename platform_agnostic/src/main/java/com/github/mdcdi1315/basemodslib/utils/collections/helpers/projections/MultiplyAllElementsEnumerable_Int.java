package com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerator;

public record MultiplyAllElementsEnumerable_Int(IIntEnumerable enumerable, int value)
    implements IIntEnumerable
{
    private record Enumerator(IIntEnumerator enumerator, int value)
        implements IIntEnumerator
    {
        @Override
        public Integer getCurrent() { return enumerator.getCurrent() * value; }

        @Override
        public void Reset() throws InvalidOperationException { enumerator.Reset(); }

        @Override
        public int getUncastedCurrent() { return enumerator.getUncastedCurrent() * value; }

        @Override
        public boolean MoveNext() throws InvalidOperationException { return enumerator.MoveNext(); }

        @Override
        public void Dispose() { enumerator.Dispose(); }
    }

    @Override
    public IIntEnumerator GetEnumerator() { return new Enumerator(enumerable.GetEnumerator(), value); }
}
