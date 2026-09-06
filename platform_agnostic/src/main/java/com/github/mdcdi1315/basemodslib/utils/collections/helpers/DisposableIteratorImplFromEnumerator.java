package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.DisposableIterator;

import java.util.function.Consumer;
import java.util.NoSuchElementException;

// We have to note the iterator semantics here:
// The hasNext method just reports whether there IS an element
// to retrieve and NOT necessarily moving to a next element.
//
// To translate the instances, a boolean 'filled' field controls
// whether the 'current' field has been provided by the enumerator.
// If not, the next() call retrieves the next element on the fly.
// If hasNext() is called first, the enumerator is moved, caches
// the current value to 'current', and when doing next(), it
// is retrieved automatically, while the next element is fetched
// to keep track of the hasNext() value.
//
// As such, now only when needing to actually move to next element,
// it is done so appropriately.
public final class DisposableIteratorImplFromEnumerator<T>
    implements DisposableIterator<T>
{
    private T current;
    private boolean filled;
    private final IEnumerator<T> enumerator;

    public DisposableIteratorImplFromEnumerator(IEnumerator<T> enumerator)
    {
        filled = false;
        this.enumerator = enumerator;
    }

    @Override
    public void close() { enumerator.Dispose(); }

    @Override
    @SuppressWarnings("AssignmentUsedAsCondition")
    public T next()
            throws NoSuchElementException
    {
        if (filled) {
            T c = current;
            if (filled = enumerator.MoveNext()) { current = enumerator.getCurrent(); }
            return c;
        } else if (enumerator.MoveNext()) {
            return enumerator.getCurrent();
        } else {
            throw new NoSuchElementException("The enumeration has been ended.");
        }
    }

    @Override
    public boolean hasNext()
    {
        if (filled) {
            return true;
        } else if (enumerator.MoveNext()) {
            current = enumerator.getCurrent();
            filled = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action)
    {
        while (enumerator.MoveNext())
        {
            action.accept(enumerator.getCurrent());
        }
    }
}
