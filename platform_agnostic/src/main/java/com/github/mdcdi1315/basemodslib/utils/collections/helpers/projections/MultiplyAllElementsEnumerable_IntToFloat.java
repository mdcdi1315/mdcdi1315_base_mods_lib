package com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IFloatEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IFloatEnumerator;

public record MultiplyAllElementsEnumerable_IntToFloat(IIntEnumerable enumerable, float value)
    implements IFloatEnumerable
{
    private record Enumerator(IIntEnumerator enumerator, float value)
        implements IFloatEnumerator
    {
        @Override
        public Float getCurrent() { return enumerator.getCurrent() * value; }

        @Override
        public void Reset() throws InvalidOperationException { enumerator.Reset(); }

        @Override
        public float getUncastedCurrent() { return enumerator.getUncastedCurrent() * value; }

        @Override
        public boolean MoveNext() throws InvalidOperationException { return enumerator.MoveNext(); }

        @Override
        public void Dispose() { enumerator.Dispose(); }
    }

    @Override
    public IFloatEnumerator GetEnumerator() { return new Enumerator(enumerable.GetEnumerator(), value); }
}
