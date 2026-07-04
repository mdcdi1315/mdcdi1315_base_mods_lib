package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.ILongEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.ILongEnumerator;

public final class LongRangeEnumerable
        extends BaseEnumerable<Long>
        implements ILongEnumerable
{
    private final long start, count;

    public LongRangeEnumerable(long start, long count)
    {
        this.start = start;
        this.count = count;
    }

    @Override
    public ILongEnumerator GetEnumerator() { return new Enumerator(start, count); }

    private static final class Enumerator
        extends BaseEnumerator<Long>
        implements ILongEnumerator
    {
        private long index;
        private final long start, bound;

        public Enumerator(long start, long count)
        {
            this.bound = start + count;
            this.index = this.start = start - 1;
        }

        @Override
        public Long getCurrent() { return index; }

        @Override
        public long getUncastedCurrent() { return index; }

        @Override
        protected void ResetImpl() throws InvalidOperationException { index = start; }

        @Override
        protected boolean MoveNextImpl() throws InvalidOperationException { return ++index < bound; }
    }
}
