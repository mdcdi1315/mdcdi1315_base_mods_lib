package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a {@link BaseWrappedEnumerator} implementation that filters the elements given a specified
 * {@link Predicate} that returns those elements that pass the {@link Predicate} test.
 * @param <T> The type of the elements to be enumerated and filtered by.
 * @implNote The implementation of this enumerator is fundamentally simple: <br />
 * -&gt; Fetch elements until one matches the predicate, and return that element. <br />
 * -&gt; Do the above until all elements are processed.
 * @since 1.0.26
 */
@SuppressWarnings("resource")
public final class FilteringEnumerator<T>
    extends BaseWrappedEnumerator<T>
{
    private T current_item;
    private Predicate<T> predicate;

    /**
     * Provides a new instance of the {@link FilteringEnumerator} class by specifying the {@link IEnumerator}
     * to obtain elements from, and the {@link Predicate} to enumerate elements from.
     * @param enumerator The {@link IEnumerator} to wrap.
     * @param predicate The {@link Predicate} to filter the elements by.
     * @throws ArgumentNullException {@code enumerator} and/or {@code predicate} are {@code null}.
     */
    public FilteringEnumerator(IEnumerator<T> enumerator, Predicate<T> predicate)
            throws ArgumentNullException
    {
        super(enumerator);
        ArgumentNullException.ThrowIfNull(this.predicate = predicate, "predicate");
        current_item = null;
    }

    @Override
    public T getCurrent() { return current_item; }

    @Override
    protected void ResetImpl() { GetWrapped().Reset(); }

    /**
     * Gets the {@link Predicate} that provides the criteria for matching against elements of the wrapped enumerator.
     * @return The {@link Predicate} for matching candidate elements. Guaranteed to be non-{@code null}.
     */
    @NotNull
    public Predicate<T> GetMatchPredicate() { return predicate; }

    @Override
    protected boolean MoveNextImpl()
    {
        T c;
        IEnumerator<T> w = GetWrapped();
        while (w.MoveNext())
        {
            if (predicate.predicate(c = w.getCurrent()))
            {
                current_item = c;
                return true;
            }
        }
        current_item = null;
        return false;
    }

    @Override
    protected void DisposeAdditional()
    {
        predicate = null;
        current_item = null;
    }
}
