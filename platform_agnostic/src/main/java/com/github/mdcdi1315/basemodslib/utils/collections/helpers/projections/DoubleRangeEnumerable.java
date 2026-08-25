package com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.collections.projections.IDoubleEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.BaseDoubleEnumerator;

public final class DoubleRangeEnumerable
        implements IDoubleEnumerable
{
    private final double min, max, step;

    public DoubleRangeEnumerable(double min, double max, double step)
    {
        this.min = min;
        this.max = max;
        this.step = step;
    }

    private static final class Enumerator
            extends BaseDoubleEnumerator
    {
        private double current;
        private final double min, max, step;

        public Enumerator(double min, double max, double step)
        {
            this.min = min;
            this.max = max;
            this.step = step;
            current = min - step;
        }

        @Override
        public double getUncastedCurrent() { return current; }

        @Override
        protected void ResetImpl()  throws InvalidOperationException { current = min - step; }

        @Override
        protected boolean MoveNextImpl()
                throws InvalidOperationException
        {
            double new_value = current + step;
            if (new_value < max) {
                current = new_value;
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public BaseDoubleEnumerator GetEnumerator() { return new Enumerator(min, max, step); }
}