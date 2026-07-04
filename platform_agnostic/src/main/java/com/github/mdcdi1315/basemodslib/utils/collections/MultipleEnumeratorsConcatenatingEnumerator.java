package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

/**
 * Provides a newer implementation of the {@link ConcatenatingEnumerator} class, but this one has the following differences:
 * <ul>
 *     <li>This class supports any number of enumerators.</li>
 *     <li>Enumerator implementations that define a more extended type than the defined type {@link T} can be also specified.</li>
 * </ul>
 * If, however, all of these improvements are not needed by your code, you should still use the
 * original {@link ConcatenatingEnumerator} implementation.
 * @param <T> The type of the elements to be returned by this enumerator implementation.
 * @since 1.0.35
 */
public final class MultipleEnumeratorsConcatenatingEnumerator<T>
    extends BaseEnumerator<T>
{
    private int index;
    private IEnumerator<? extends T>[] enumerators;

    /**
     * Initializes a new instance of the {@link MultipleEnumeratorsConcatenatingEnumerator},
     * specifying the enumerator objects to use.
     * @param enumerators The enumerator objects to concatenate.
     * @throws ArgumentNullException {@code enumerators} is {@code null}.
     */
    @SafeVarargs
    public MultipleEnumeratorsConcatenatingEnumerator(IEnumerator<? extends T>... enumerators)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerators, "enumerators");
        index = 0;
        this.enumerators = enumerators;
    }

    @Pure
    @Override
    public T getCurrent() { return enumerators[index].getCurrent(); }

    @Pure
    @Override
    protected void ResetImpl() { index = 0; }

    @Pure
    @Override
    protected boolean MoveNextImpl()
    {
        while (index < enumerators.length)
        {
            if (enumerators[index].MoveNext()) { return true; }
            index++;
        }

        return false;
    }

    @Override
    public void Dispose()
    {
        synchronized (this)
        {
            super.Dispose();
            index = 0;
            if (enumerators != null)
            {
                for (int I = 0; I < enumerators.length; I++)
                {
                    enumerators[I].Dispose();
                    enumerators[I] = null;
                }
                enumerators = null;
            }
        }
    }
}
