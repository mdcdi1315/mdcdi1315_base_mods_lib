package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseWrappedEnumerator;

/**
 * A wrapping implementation around the {@link IEnumerator} interface to find out while running in a loop whether a next element does exist from the underlying enumerator.
 * @param <T> The type of the elements to be enumerated.
 */
@SuppressWarnings("resource")
public class NextNextEnumerator<T>
    extends BaseWrappedEnumerator<T>
{
    private T current, next;
    private boolean hasnextnext;

    /**
     * Initializes a new instance of the {@link NextNextEnumerator} class by wrapping the specified {@link IEnumerator} implementation.
     * @param enumerator The enumerator implementation to be wrapped.
     * @throws ArgumentNullException {@code enumerator} is {@code null}.
     */
    public NextNextEnumerator(IEnumerator<T> enumerator)
            throws ArgumentNullException
    {
        super(enumerator);
        hasnextnext = false;
        current = next = null;
    }

    /**
     * Gets a value whether the enumerator has a next element in the collection. <br />
     * If this returns {@code true}, and then calling the {@link #MoveNext()} method will make that found element the current one.
     */
    public final boolean HasNextNextElement() { return hasnextnext; }

    @Override
    public final T getCurrent() { return current; }

    @Override
    protected final boolean MoveNextImpl()
    {
        IEnumerator<T> enumerator = GetWrapped();
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
    protected final void ResetImpl()
    {
        GetWrapped().Reset();
        hasnextnext = false;
        current = next = null;
    }
}

