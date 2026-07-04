package com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IFloatEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IFloatEnumerator;

public record MinMaxNormalization_FloatToFloatEnumerable(IFloatEnumerable enumerable, float min, float max)
    implements IFloatEnumerable
{
    private record Enumerator(IFloatEnumerator enumerator, float min, float max)
            implements IFloatEnumerator
    {
        @Override
        public Float getCurrent() { return Extensions.ToNormalRange(enumerator.getCurrent(), min, max); }

        @Override
        public float getUncastedCurrent() { return Extensions.ToNormalRange(enumerator.getUncastedCurrent(), min, max); }

        @Override
        public void Reset() throws InvalidOperationException { enumerator.Reset(); }

        @Override
        public boolean MoveNext() throws InvalidOperationException { return enumerator.MoveNext(); }

        @Override
        public void Dispose() { enumerator.Dispose(); }
    }

    @Override
    public IFloatEnumerator GetEnumerator() { return new Enumerator(enumerable.GetEnumerator(), min, max); }
}
