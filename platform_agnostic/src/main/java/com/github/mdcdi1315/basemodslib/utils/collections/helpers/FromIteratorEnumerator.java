package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

import java.util.Iterator;

public final class FromIteratorEnumerator<T>
    extends BaseEnumerator<T>
{
    private T element;
    private final Iterator<T> iterator;

    public FromIteratorEnumerator(@NotNull Iterator<T> iterator) { this.iterator = iterator; }

    @Override
    public T getCurrent() { return element; }

    @Override
    protected void ResetImpl()
            throws InvalidOperationException
    {
        throw new InvalidOperationException("Cannot call Reset on Java wrapped iterator instances.");
    }

    @Override
    protected boolean MoveNextImpl()
            throws InvalidOperationException
    {
        boolean result = iterator.hasNext();
        if (result) { element = iterator.next(); }
        return result;
    }
}
