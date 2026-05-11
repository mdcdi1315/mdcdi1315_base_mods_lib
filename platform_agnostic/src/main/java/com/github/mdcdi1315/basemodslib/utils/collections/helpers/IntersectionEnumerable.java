package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;
import com.github.mdcdi1315.basemodslib.utils.JavaObjectEqualsEqualityComparer;

public final class IntersectionEnumerable<T>
        extends BaseEnumerable<T>
{
    private final IEnumerable<T> first, second;
    private final IEqualityComparer<T> comparer;

    public IntersectionEnumerable(IEnumerable<T> first, IEnumerable<T> second, IEqualityComparer<T> comparer)
    {
        this.first = first;
        this.second = second;
        this.comparer = comparer == null ? new JavaObjectEqualsEqualityComparer<>() : comparer;
    }

    private static final class Enumerator<T>
            extends BaseEnumerator<T>
    {
        private T cached_current;
        private IEnumerator<T> first, second;
        private IEqualityComparer<T> comparer;

        public Enumerator(IEnumerator<T> first, IEnumerator<T> second, IEqualityComparer<T> comparer)
        {
            this.first = first;
            this.second = second;
            this.comparer = comparer;
        }

        @Override
        public T getCurrent() { return cached_current; }

        @Override
        protected void ResetImpl()
                throws InvalidOperationException
        {
            first.Reset();
            second.Reset();
        }

        @Override
        protected boolean MoveNextImpl()
                throws InvalidOperationException
        {
            while (first.MoveNext() && second.MoveNext())
            {
                cached_current = second.getCurrent();
                if (comparer.Equals(first.getCurrent(), cached_current)) { return true; }
            }
            cached_current = null;
            return false;
        }

        @Override
        public void Dispose()
        {
            try {
                super.Dispose();
                if (first != null) { first.Dispose(); }
            } finally {
                first = null;
                try {
                    if (second != null) { second.Dispose(); }
                } finally {
                    second = null;
                    comparer = null;
                    cached_current = null;
                }
            }
        }
    }

    @Override
    public IEnumerator<T> GetEnumerator() {
        return new Enumerator<>(first.GetEnumerator(), second.GetEnumerator(), comparer);
    }
}
