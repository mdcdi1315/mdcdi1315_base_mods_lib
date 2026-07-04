package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseWrappedEnumerator;

public final class SkipWhileSimpleEnumerable<T>
    extends BaseEnumerable<T>
{
    private final Predicate<T> predicate;
    private final IEnumerable<T> enumerable;

    public SkipWhileSimpleEnumerable(IEnumerable<T> enumerable, Predicate<T> predicate)
    {
        this.predicate = predicate;
        this.enumerable = enumerable;
    }

    @SuppressWarnings("resource")
    private static final class Enumerator<T>
        extends BaseWrappedEnumerator<T>
    {
        private boolean not_skipped;
        private Predicate<T> predicate;

        public Enumerator(IEnumerator<T> enumerator_to_wrap, Predicate<T> predicate)
        {
            super(enumerator_to_wrap);
            this.predicate = predicate;
            not_skipped = true;
        }

        @Override
        public T getCurrent() { return GetWrapped().getCurrent(); }

        @Override
        protected void ResetImpl()
        {
            GetWrapped().Reset();
            not_skipped = true;
        }

        @Override
        @SuppressWarnings("StatementWithEmptyBody")
        protected boolean MoveNextImpl()
        {
            IEnumerator<T> w = GetWrapped();

            if (not_skipped)
            {
                while (w.MoveNext() && predicate.predicate(w.getCurrent()));
                not_skipped = false;
            }

            return w.MoveNext();
        }

        @Override
        protected void DisposeAdditional()
        {
            predicate = null;
            not_skipped = false;
        }
    }

    @Override
    public IEnumerator<T> GetEnumerator() {
        return new Enumerator<>(enumerable.GetEnumerator(), predicate);
    }
}
