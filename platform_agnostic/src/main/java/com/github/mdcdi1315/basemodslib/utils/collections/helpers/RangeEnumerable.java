package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

public final class RangeEnumerable
        extends BaseEnumerable<Integer>
{
    private final int start, count;

    public RangeEnumerable(int start, int count)
    {
        this.start = start;
        this.count = count;
    }

    @Override
    public IEnumerator<Integer> GetEnumerator() { return new Enumerator(start, count); }

    private static final class Enumerator
            extends BaseEnumerator<Integer>
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
        protected void ResetImpl() throws InvalidOperationException { index = start; }

        @Override
        protected boolean MoveNextImpl() throws InvalidOperationException { return ++index < bound; }
    }
}