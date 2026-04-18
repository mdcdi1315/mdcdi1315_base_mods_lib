package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Provides an enumerator implementation that wraps the specified {@link IEnumerator} instances
 * and returns a single {@link IEnumerator} for them that returns their elements in a sequential manner.
 * @param <T> The type of the elements of the input enumerators.
 * @since 1.0.26
 */
public final class ConcatenatingEnumerator<T>
    extends BaseEnumerator<T>
{
    private static final byte FLAG_NONE = 0, FLAG_ITERATED_FIRST = 1 << 0, FLAG_TERMINATED = 1 << 1;

    private byte flags;
    private T cached_current;
    private IEnumerator<T> enumerator_1;
    private IEnumerator<T> enumerator_2;

    /**
     * Initializes a new instance of the {@link ConcatenatingEnumerator} class.
     * @param enumerator_1 The first enumerator of the concatenation result.
     * @param enumerator_2 The second enumerator of the concatenation result.
     * @throws ArgumentNullException {@code enumerator_1} and/or {@code enumerator_2} are {@code null}.
     */
    public ConcatenatingEnumerator(IEnumerator<T> enumerator_1, IEnumerator<T> enumerator_2)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.enumerator_1 = enumerator_1, "enumerator_1");
        ArgumentNullException.ThrowIfNull(this.enumerator_2 = enumerator_2, "enumerator_2");
        flags = FLAG_NONE;
    }

    /**
     * Initializes a new instance of the {@link ConcatenatingEnumerator} class from three input enumerators.
     * @param enumerator_1 The first enumerator of the concatenation result.
     * @param enumerator_2 The second enumerator of the concatenation result.
     * @param enumerator_3 The third enumerator of the concatenation result.
     * @throws ArgumentNullException {@code enumerator_1} and/or {@code enumerator_2} and/or {@code enumerator_3} are {@code null}.
     */
    public ConcatenatingEnumerator(IEnumerator<T> enumerator_1, IEnumerator<T> enumerator_2, IEnumerator<T> enumerator_3) throws ArgumentNullException { this(new ConcatenatingEnumerator<>(enumerator_1, enumerator_2), enumerator_3); }

    /**
     * Initializes a new instance of the {@link ConcatenatingEnumerator} class from four input enumerators.
     * @param enumerator_1 The first enumerator of the concatenation result.
     * @param enumerator_2 The second enumerator of the concatenation result.
     * @param enumerator_3 The third enumerator of the concatenation result.
     * @param enumerator_4 The fourth enumerator of the concatenation result.
     * @throws ArgumentNullException {@code enumerator_1} and/or {@code enumerator_2} and/or {@code enumerator_3} are {@code null}.
     */
    public ConcatenatingEnumerator(IEnumerator<T> enumerator_1, IEnumerator<T> enumerator_2, IEnumerator<T> enumerator_3, IEnumerator<T> enumerator_4) throws ArgumentNullException { this(enumerator_1, enumerator_2, new ConcatenatingEnumerator<>(enumerator_3, enumerator_4)); }

    @Override
    public T getCurrent() { return cached_current; }

    @Override
    protected void ResetImpl()
    {
        enumerator_1.Reset();
        enumerator_2.Reset();
        flags = FLAG_NONE;
    }

    /*
        The below method is the actual workhorse of doing enumerator concatenation.
        Concatenation in collections is that you get two compatible collections, and you put their elements into a single collection.
        To simplify, imagine that we have two String instances. We use the + operator to perform string concatenation.
        A similar pattern is done by this enumerator implementation in that it fetches next elements until we drain both enumerators.
        The first enumerator is selected for fetching elements.
        Once that enumerator has no more items to fetch, the second one is selected.
        But we don't want to provide 'false' once the first one ends, so we need to run a MoveNext for the second one.
        The below 'while' loop along with a state variable reflects that.
        If we were successful on retrieving an element, we store it into a temp field and that is returned through the getCurrent method.
        This is more convenient than querying the state to see which enumerator has the result which we are willing to obtain.
     */

    @Override
    protected boolean MoveNextImpl()
    {
        IEnumerator<T> en_to_use;
        boolean iterated_first_value;
        while ((flags & FLAG_TERMINATED) == 0)
        {
            iterated_first_value = (flags & FLAG_ITERATED_FIRST) == FLAG_ITERATED_FIRST;
            en_to_use = iterated_first_value ? enumerator_2 : enumerator_1;
            if (en_to_use.MoveNext()) {
                // We have a result, store it to the temp field and return.
                cached_current = en_to_use.getCurrent();
                return true;
            } else if (iterated_first_value) {
                // We have no more elements from the second enumerator, break and give up.
                flags |= FLAG_TERMINATED;
            } else {
                // The first enumerator was completed. Fetch elements from the second one.
                flags |= FLAG_ITERATED_FIRST;
            }
        }
        return false;
    }

    /**
     * Disposes this {@link ConcatenatingEnumerator} instance. <br />
     * Thread-safe.
     */
    @Override
    public void Dispose()
    {
        synchronized (this)
        {
            try {
                super.Dispose();
                if (enumerator_1 != null) {
                    enumerator_1.Dispose();
                }
            } finally {
                try {
                    if (enumerator_2 != null) {
                        enumerator_2.Dispose();
                    }
                } finally {
                    flags = FLAG_NONE;
                    enumerator_1 = null;
                    enumerator_2 = null;
                    cached_current = null;
                }
            }
        }
    }
}
