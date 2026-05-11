package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

public final class OfTypeEnumerable<T>
        extends BaseEnumerable<T>
{
    private final Class<T> type;
    private final com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerable enumerable;

    public OfTypeEnumerable(com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerable enumerable, Class<T> type)
    {
        this.type = type;
        this.enumerable = enumerable;
    }

    private static final class Enumerator<T>
            extends BaseEnumerator<T>
    {
        private T cached_current;
        private final Class<T> type;
        private final com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerator enumerator;

        public Enumerator(com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerator enumerator_to_wrap, Class<T> type)
                throws ArgumentNullException
        {
            this.type = type;
            this.enumerator = enumerator_to_wrap;
        }

        @Override
        public T getCurrent() { return cached_current; }

        @Override
        protected void ResetImpl() { enumerator.Reset(); }

        @Override
        @SuppressWarnings("unchecked")
        protected boolean MoveNextImpl()
        {
            Object inst;
            while (enumerator.MoveNext())
            {
                if (type.isInstance(inst = enumerator.getCurrent()))
                {
                    cached_current = (T)inst;
                    return true;
                }
            }
            cached_current = null;
            return false;
        }
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return new Enumerator<>(enumerable.GetEnumerator(), type); }
}