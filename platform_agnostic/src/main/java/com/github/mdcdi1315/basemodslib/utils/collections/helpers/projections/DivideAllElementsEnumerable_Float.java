package com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IFloatEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IFloatEnumerator;

public record DivideAllElementsEnumerable_Float(IFloatEnumerable enumerable, float value)
    implements IFloatEnumerable
{
    private record Enumerator(IFloatEnumerator enumerator, float div_value)
            implements IFloatEnumerator
    {
        @Override
        public Float getCurrent() { return enumerator.getCurrent() / div_value; }

        @Override
        public void Reset() throws InvalidOperationException { enumerator.Reset(); }

        @Override
        public float getUncastedCurrent() { return enumerator.getUncastedCurrent() / div_value; }

        @Override
        public boolean MoveNext() throws InvalidOperationException { return enumerator.MoveNext(); }

        @Override
        public void Dispose() { enumerator.Dispose(); }
    }

    @Override
    public IFloatEnumerator GetEnumerator() { return new Enumerator(enumerable.GetEnumerator(), value); }
}
