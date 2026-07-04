package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.MultipleEnumeratorsConcatenatingEnumerator;

public final class MultipleConcatenatingEnumerablesEnumerable<T>
    extends BaseEnumerable<T>
{
    private final IEnumerable<? extends T>[] enumerables;

    @SafeVarargs
    public MultipleConcatenatingEnumerablesEnumerable(IEnumerable<? extends T>... enumerables)
    {
        ArgumentNullException.ThrowIfNull(enumerables, "enumerables");
        this.enumerables = enumerables;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes", "resource"})
    public IEnumerator<T> GetEnumerator()
    {
        IEnumerator[] enumerators = new IEnumerator[this.enumerables.length];
        for (int i = 0; i < enumerators.length; i++)
        {
            enumerators[i] = this.enumerables[i].GetEnumerator();
        }
        return new MultipleEnumeratorsConcatenatingEnumerator<T>(enumerators);
    }
}
