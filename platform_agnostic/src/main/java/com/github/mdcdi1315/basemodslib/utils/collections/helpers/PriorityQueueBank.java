package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.collections.helpers.linkednodes.NodeWithPreviousPointer;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.linkednodes.NodeWithPreviousPointerEnumerator;

public final class PriorityQueueBank<T>
        implements IEnumerable<T>
{
    private NodeWithPreviousPointer<T> head, tail;

    public PriorityQueueBank() { head = tail = null; }

    public void Enqueue(T item)
    {
        NodeWithPreviousPointer<T> t = new NodeWithPreviousPointer<>(item);
        if (head == null) {
            head = t;
        } else if (tail == null) {
            head.Previous = tail = t;
        } else {
            tail.Previous = t;
            tail = t;
        }
    }

    @MaybeNull
    public T Dequeue()
    {
        if (head == null) {
            return null;
        } else {
            T v = head.Value;
            head = head.Previous;
            return v;
        }
    }

    @MaybeNull
    public T Peek() { return (head == null) ? null : head.Value; }

    public void ClearReferences() { head = tail = null; }

    @NotNull
    @Override
    public IEnumerator<T> GetEnumerator() { return new NodeWithPreviousPointerEnumerator<>(head); }
}