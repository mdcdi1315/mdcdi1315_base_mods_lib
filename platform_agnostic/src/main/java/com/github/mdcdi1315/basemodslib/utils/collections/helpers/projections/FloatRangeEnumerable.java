package com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.collections.projections.IFloatEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.BaseFloatEnumerator;

public final class FloatRangeEnumerable
    implements IFloatEnumerable
{
    private final float min, max, step;

    public FloatRangeEnumerable(float min, float max, float step)
    {
        this.min = min;
        this.max = max;
        this.step = step;
    }

    private static final class Enumerator
        extends BaseFloatEnumerator
    {
        private float current;
        private final float min, max, step;

        public Enumerator(float min, float max, float step)
        {
            this.min = min;
            this.max = max;
            this.step = step;
            current = min - step;
        }

        @Override
        public float getUncastedCurrent() { return current; }

        @Override
        protected void ResetImpl() throws InvalidOperationException { current = min - step; }

        @Override
        protected boolean MoveNextImpl()
                throws InvalidOperationException
        {
            float new_value = current + step;
            if (new_value < max) {
                current = new_value;
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public BaseFloatEnumerator GetEnumerator() { return new Enumerator(min, max, step); }
}
