package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Tuple2;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

public final class IndexEnumerable<T>
        extends BaseEnumerable<Tuple2<T, Integer>>
{
    private final IEnumerable<T> enumerable;

    public IndexEnumerable(IEnumerable<T> enumerable) { this.enumerable = enumerable; }

    private static final class Enumerator<T>
            extends BaseEnumerator<Tuple2<T, Integer>>
    {
        private int index;
        private final IEnumerator<T> enumerator;

        public Enumerator(IEnumerator<T> enumerator) { this.enumerator = enumerator; index = -1; }

        @Override
        public Tuple2<T, Integer> getCurrent() { return new Tuple2<>(enumerator.getCurrent(), index); }

        @Override
        protected void ResetImpl() throws InvalidOperationException { enumerator.Reset(); index = -1; }

        @Override
        protected boolean MoveNextImpl()
                throws InvalidOperationException
        {
            if (enumerator.MoveNext()) {
                index++;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void Dispose()
        {
            super.Dispose();
            enumerator.Dispose();
        }
    }

    @Override
    public IEnumerator<Tuple2<T, Integer>> GetEnumerator() { return new Enumerator<>(enumerable.GetEnumerator()); }
}