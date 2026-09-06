package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.IEmptyEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.ISupportsCloning;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.BaseIntEnumerator;

public class RangeEnumerable
        extends BaseEnumerable<Integer>
        implements IIntEnumerable,
        ISupportsCloning<Integer>
{
    private final int start, count;

    public RangeEnumerable(int start, int count)
    {
        this.start = start;
        this.count = count;
    }

    @Override
    public int GetCount() { return count; }

    @Override
    public Integer GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is out of bounds of the current collection.");
        } else {
            return start + index;
        }
    }

    private static final class Enumerator
            extends BaseIntEnumerator
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

    private static final class Empty
        extends RangeEnumerable
        implements IEmptyEnumerable
    {
        public Empty() { super(0, 0); }
    }

    @Override
    public BaseIntEnumerator GetEnumerator() { return new Enumerator(start, count); }

    @Override
    public RangeEnumerable Slice(int count) throws ArgumentException { return Slice(0, count); }

    @Override
    public RangeEnumerable Slice(int index, int count)
            throws ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (count == 0 || this.count == 0) {
            return new Empty();
        } else {
            // Index is selected by doing this.start + index, and
            // the count is deduced by the min of count and this.count.
            return new RangeEnumerable(this.start + index, Extensions.Min(this.count, count));
        }
    }

    @Override
    public RangeEnumerable Clone() { return new RangeEnumerable(this.start, this.count); }

    @Override
    public String toString() { return String.format("[%d, %d]", start, start + count); }
}