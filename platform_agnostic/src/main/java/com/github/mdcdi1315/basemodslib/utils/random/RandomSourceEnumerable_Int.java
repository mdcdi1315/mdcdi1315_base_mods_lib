package com.github.mdcdi1315.basemodslib.utils.random;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.ICloneableEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.ICountableCollection;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.BaseIntEnumerator;

record RandomSourceEnumerable_Int(IRandomSource random, int count)
    implements IIntEnumerable, ICountableCollection<Integer>, ICloneableEnumerable<Integer>
{
    private static final class Enumerator
        extends BaseIntEnumerator
    {
        private final int count;
        private int index, current;
        private final IRandomSource random;

        public Enumerator(IRandomSource random, int count)
        {
            this.random = random;
            this.index = -1;
            this.current = 0;
            this.count = count;
        }

        @Pure
        @NotNull
        @Override
        public Integer getCurrent() { return current; }

        @Pure
        @Override
        public int getUncastedCurrent() { return current; }

        @Override
        protected void ResetImpl() { index = -1; }

        @Override
        protected boolean MoveNextImpl()
        {
            boolean value = ++index < count;
            if (value) {
                current = RandomUtils.NextInt(random);
            }
            return value;
        }
    }

    @Pure
    @Override
    public int GetCount() { return count; }

    @Pure
    @NotNull
    @Override
    public IIntEnumerator GetEnumerator() { return new Enumerator(random, count); }

    @Pure
    @NotNull
    @Override
    public RandomSourceEnumerable_Int Clone() { return new RandomSourceEnumerable_Int(random, count); }
}
