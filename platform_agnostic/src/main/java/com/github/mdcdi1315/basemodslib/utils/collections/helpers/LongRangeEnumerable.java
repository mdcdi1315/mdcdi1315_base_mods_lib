package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.*;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.collections.*;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.ILongEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.BaseLongEnumerator;

public class LongRangeEnumerable
        extends BaseEnumerable<Long>
        implements ILongEnumerable,
        ISupportsCloning<Long>
{
    private final long start, count;

    public LongRangeEnumerable(long start, long count)
    {
        this.start = start;
        this.count = count;
    }

    private static final class Empty
        extends LongRangeEnumerable
        implements IEmptyEnumerable
    { public Empty() { super(0, 0); } }

    @Override
    public int GetCount() { return (int)Extensions.Min(count, Integer.MAX_VALUE); }

    @Override
    public Long GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else if (index >= GetCount()) {
            throw new ArgumentOutOfRangeException("index", "Index is out of bounds of the current collection.");
        } else {
            return start + index;
        }
    }

    private static final class Enumerator
        extends BaseLongEnumerator
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

    @Override
    public BaseLongEnumerator GetEnumerator() { return new Enumerator(start, count); }

    @Override
    public LongRangeEnumerable Slice(int count) throws ArgumentException { return Slice(0, count); }

    @Override
    public LongRangeEnumerable Slice(int index, int count)
            throws ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (count == 0 || this.count == 0L) {
            return new Empty();
        } else {
            // Index is selected by doing this.start + index, and
            // the count is deduced by the min of count and this.count.
            return new LongRangeEnumerable(this.start + index, Extensions.Min(this.count, count));
        }
    }

    @Override
    public String toString() { return String.format("[%d, %d]", start, start + count); }
}
