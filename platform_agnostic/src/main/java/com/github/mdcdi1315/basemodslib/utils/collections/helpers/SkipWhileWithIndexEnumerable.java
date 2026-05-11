package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.function.BiPredicate;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseWrappedEnumerator;

public final class SkipWhileWithIndexEnumerable<T>
    extends BaseEnumerable<T>
{
    private final IEnumerable<T> enumerable;
    private final BiPredicate<T, Integer> predicate;

    public SkipWhileWithIndexEnumerable(IEnumerable<T> enumerable, BiPredicate<T, Integer> predicate)
    {
        this.predicate = predicate;
        this.enumerable = enumerable;
    }

    private static final class Enumerator<T>
            extends BaseWrappedEnumerator<T>
    {
        private boolean not_skipped;
        private BiPredicate<T, Integer>predicate;

        public Enumerator(IEnumerator<T> enumerator_to_wrap, BiPredicate<T, Integer> predicate)
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
        protected boolean MoveNextImpl()
        {
            IEnumerator<T> w = GetWrapped();

            if (not_skipped)
            {
                int index = 0;
                while (w.MoveNext() && predicate.predicate(w.getCurrent(), index)) { index++; }
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
    public IEnumerator<T> GetEnumerator() { return new Enumerator<>(enumerable.GetEnumerator(), predicate); }
}
