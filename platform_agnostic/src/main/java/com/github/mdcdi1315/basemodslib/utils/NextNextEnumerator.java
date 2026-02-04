package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * A wrapping implementation around the {@link IEnumerator} interface to find out while running in a loop whether a next element does exist from the underlying enumerator.
 * @param <T> The type of the elements to be enumerated.
 */
public class NextNextEnumerator<T>
    implements IEnumerator<T>
{
    private T current, next;
    private boolean hasnextnext;
    private final IEnumerator<T> enumerator;

    /**
     * Initializes a new instance of the {@link NextNextEnumerator} class by wrapping the specified {@link IEnumerator} implementation.
     * @param enumerator The enumerator implementation to be wrapped.
     * @throws ArgumentNullException {@code enumerator} is {@code null}.
     */
    public NextNextEnumerator(IEnumerator<T> enumerator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerator);
        this.enumerator = enumerator;
        current = next = null;
        hasnextnext = false;
    }

    /**
     * Gets a value whether the enumerator has a next element in the collection. <br />
     * If this returns {@code true}, and then calling the {@link #MoveNext()} method will make that found element the current one.
     */
    public final boolean HasNextNextElement() { return hasnextnext; }

    @Override
    public final T getCurrent() { return current; }

    @Override
    public final boolean MoveNext()
    {
        if (hasnextnext) {
            current = next;
            next = (hasnextnext = enumerator.MoveNext()) ? enumerator.getCurrent() : null;
            return true;
        } else if (enumerator.MoveNext()) {
            current = enumerator.getCurrent();
            next = (hasnextnext = enumerator.MoveNext()) ? enumerator.getCurrent() : null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final void Reset()
    {
        enumerator.Reset();
        hasnextnext = false;
        current = next = null;
    }

    /**
     * Disposes this {@link NextNextEnumerator} instance.
     */
    @Override
    public void Dispose() { enumerator.Dispose(); }
}

