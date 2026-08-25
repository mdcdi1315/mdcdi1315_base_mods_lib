package com.github.mdcdi1315.basemodslib.utils.collections.helpers.projections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseWrappedEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerator;

public record DivideAllElementsEnumerable_Int(IIntEnumerable enumerable, int value)
    implements IIntEnumerable
{
    @SuppressWarnings("resource")
    private static final class Enumerator
        extends BaseWrappedEnumerator<Integer>
        implements IIntEnumerator
    {
        private final int div_value;

        public Enumerator(IIntEnumerator enumerator_to_wrap, int v) throws ArgumentNullException { super(enumerator_to_wrap); div_value = v; }

        @Override
        protected void ResetImpl() { GetWrapped().Reset(); }

        @Override
        protected boolean MoveNextImpl() { return GetWrapped().MoveNext(); }

        @Override
        public Integer getCurrent() { return GetWrapped().getCurrent() / div_value; }

        @Override
        public int getUncastedCurrent() { return ((IIntEnumerator)GetWrapped()).getUncastedCurrent() / div_value; }
    }

    @Override
    public IIntEnumerator GetEnumerator() { return new Enumerator(enumerable.GetEnumerator(), value); }
}
