package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Provides an {@link IEnumerator} implementation that skips over the first <i>count</i> elements specified by the constructor.
 * @param <T> The type of elements to be enumerated.
 * @since 1.0.31
 */
@SuppressWarnings("resource")
public final class SkippingEnumerator<T>
    extends BaseWrappedEnumerator<T>
{
    private boolean not_skipped;
    private final int n_initial_skip;

    /**
     * Initializes a new instance of the {@link SkippingEnumerator} class.
     *
     * @param enumerator_to_wrap The {@link IEnumerator} instance to wrap.
     * @param count Number of initial elements to skip.
     * @throws ArgumentNullException {@code enumerator_to_wrap} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code count} is a negative value.
     */
    public SkippingEnumerator(IEnumerator<T> enumerator_to_wrap, int count)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        super(enumerator_to_wrap);
        if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count must not be a negative number");
        } else {
            not_skipped = true;
            n_initial_skip = count;
        }
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
        IEnumerator<T> wrapped = GetWrapped();

        if (not_skipped)
        {
            int I = 0;
            while (I < n_initial_skip)
            {
                if (!wrapped.MoveNext()) { return false; }
                I++;
            }
            not_skipped = false;
        }

        return wrapped.MoveNext();
    }
}
