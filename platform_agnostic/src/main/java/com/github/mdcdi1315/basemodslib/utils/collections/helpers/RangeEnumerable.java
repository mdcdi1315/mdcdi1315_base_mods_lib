package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerator;

public final class RangeEnumerable
        extends BaseEnumerable<Integer>
        implements IIntEnumerable
{
    private final int start, count;

    public RangeEnumerable(int start, int count)
    {
        this.start = start;
        this.count = count;
    }

    @Override
    public IIntEnumerator GetEnumerator() { return new Enumerator(start, count); }

    private static final class Enumerator
            extends BaseEnumerator<Integer>
            implements IIntEnumerator
    {
        private int index;
        private final int start, bound;

        public Enumerator(int start, int count)
        {
            this.bound = start + count;
            this.index = this.start = start - 1;
        }

        @Override
        public Integer getCurrent() { return index; }

        @Override
        public int getUncastedCurrent() { return index; }

        @Override
        protected void ResetImpl() throws InvalidOperationException { index = start; }

        @Override
        protected boolean MoveNextImpl() throws InvalidOperationException { return ++index < bound; }
    }
}