package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.*;

public final class ConcatenatingEnumerable<T>
        extends BaseEnumerable<T>
{
    private final IEnumerable<IEnumerable<? extends T>> enumerables;

    public ConcatenatingEnumerable(IEnumerable<IEnumerable<? extends T>> enumerables) { this.enumerables = enumerables; }

    @SuppressWarnings("unchecked")
    public ConcatenatingEnumerable(IEnumerable<T> first, IEnumerable<T> second) { this(new IEnumerable[]{ first, second }); }

    public ConcatenatingEnumerable(IEnumerable<? extends T>[] enumerables) { this.enumerables = new ArrayView<>(enumerables); }

    @Override
    public IEnumerator<T> GetEnumerator() { return new Enumerator<>(enumerables.GetEnumerator()); }

    private static final class Enumerator<T>
            extends BaseEnumerator<T>
    {
        private IEnumerator<? extends T> current;
        private final IEnumerator<IEnumerable<? extends T>> enumerator;

        public Enumerator(IEnumerator<IEnumerable<? extends T>> enumerator)
        {
            this.current = null;
            this.enumerator = enumerator;
        }

        private void DisposeWrappedEnumerator()
        {
            if (current != null)
            {
                current.Dispose();
                current = null;
            }
        }

        @Override
        public T getCurrent() { return current.getCurrent(); }

        @Override
        protected void ResetImpl()
        {
            DisposeWrappedEnumerator();
            enumerator.Reset();
        }

        @Override
        protected boolean MoveNextImpl()
        {
            while (current == null || !current.MoveNext())
            {
                if (enumerator.MoveNext()) {
                    current = enumerator.getCurrent().GetEnumerator();
                } else {
                    return false;
                }
            }
            return current != null;
        }

        @Override
        public void Dispose()
        {
            super.Dispose();
            try {
                enumerator.Dispose();
            } finally {
                DisposeWrappedEnumerator();
            }
        }
    }
}