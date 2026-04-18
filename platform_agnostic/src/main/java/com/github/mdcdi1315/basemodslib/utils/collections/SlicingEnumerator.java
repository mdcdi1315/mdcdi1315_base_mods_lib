package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * Provides a wrapper over an {@link IEnumerator} instance that does only enumerate the specified portion of the input enumerator instance.
 * @param <T> The type of the elements that are provided by the wrapped enumerator.
 * @since 1.0.26
 */
public final class SlicingEnumerator<T>
    extends BaseWrappedEnumerator<T>
{
    private int index;
    private final int count, start_index;
    private boolean not_consumed_start_index;

    /**
     * Provides a new instance of the {@link SlicingEnumerator} class, by only enumerating the specified number of elements.
     * @param enumerator_to_wrap The {@link IEnumerator} instance to wrap.
     * @param count The number of elements to iterate only.
     * @throws ArgumentNullException {@code enumerator_to_wrap} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code count} is a negative value.
     */
    public SlicingEnumerator(IEnumerator<T> enumerator_to_wrap, int count) throws ArgumentNullException, ArgumentOutOfRangeException { this(enumerator_to_wrap, 0, count); }

    /**
     * Provides a new instance of the {@link SlicingEnumerator} class, by only enumerating the specified number of elements,
     * and starting returning elements on the specified zero-based start index.
     * @param enumerator_to_wrap The {@link IEnumerator} instance to wrap.
     * @param start_index The index from where the enumerator starts returning elements.
     * @param count The number of elements to iterate only.
     * @throws ArgumentNullException {@code enumerator_to_wrap} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code count} and/or {@code start_index} are negative values.
     */
    public SlicingEnumerator(IEnumerator<T> enumerator_to_wrap, int start_index, int count)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        super(enumerator_to_wrap);
        if (start_index < 0) {
            throw new ArgumentOutOfRangeException("start_index", "Starting index cannot be less than 0.");
        } else if ((this.count = count) < 0) {
            throw new ArgumentOutOfRangeException("count", "Number of elements to iterate cannot be less than 0.");
        } else {
            this.index = -1;
            this.not_consumed_start_index = (this.start_index = start_index) > 0;
        }
    }

    /**
     * Gets the maximum number of times that the {@link IEnumerator#MoveNext()} method will be called on the wrapped enumerator.
     * @return The number of elements to be returned through this enumerator.
     */
    public int GetNumberOfIterations() { return count; }

    /**
     * Gets the index from which the enumerator begins to return elements from.
     * @return The index from which the enumerator begins.
     */
    public int GetStartingIndex() { return start_index; }

    @Override
    public T getCurrent() { return GetWrapped().getCurrent(); }

    @Override
    protected void ResetImpl()
    {
        GetWrapped().Reset();
        not_consumed_start_index = true;
    }

    @Override
    protected boolean MoveNextImpl()
    {
        if (not_consumed_start_index)
        {
            // A gotcha here: If, for example, we have specified a starting index of 0,
            // no elements are needed to be skipped over.
            index = start_index;
            while (index > 0 && GetWrapped().MoveNext()) { index--; }
            index = -1;
            not_consumed_start_index = false;
        }

        // normally iterate 'count' elements.
        return ++index < count && GetWrapped().MoveNext();
    }
}
