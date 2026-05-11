package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableQueue;

import java.util.*;

public final class WrappedJavaQueueFromIQueue<T>
    implements Queue<T>
{
    private final ITraversableQueue<T> queue;

    public WrappedJavaQueueFromIQueue(ITraversableQueue<T> queue) { this.queue = queue; }

    @Override
    public int size() { return queue.GetCount(); }

    @Override
    public boolean isEmpty() { return queue.TryPeek() == null; }

    @Override
    public boolean contains(Object o) { return CollectionBridgingHelpers.Contains(queue, o); }

    @Override
    public Iterator<T> iterator() { return new DisposableIteratorImplFromEnumerator<>(queue.GetEnumerator()); }

    @Override
    public Object[] toArray() { return CollectionBridgingHelpers.ToArray(queue, ITraversableQueue::GetCount); }

    @Override
    public <T1> T1[] toArray(T1[] a) { return CollectionBridgingHelpers.ToArray(queue, ITraversableQueue::GetCount, a); }

    @Override
    public boolean remove(Object o) { throw new UnsupportedOperationException("Cannot remove from a ITraversableQueue"); }

    @Override
    public boolean addAll(Collection<? extends T> c) { return CollectionBridgingHelpers.AddAll(queue, c, queue::Enqueue); }

    @Override
    public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException("Cannot remove from a ITraversableQueue"); }

    @Override
    public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException("Cannot remove from a ITraversableQueue"); }

    @Override
    public boolean containsAll(Collection<?> c) { return CollectionBridgingHelpers.ContainsAll(queue, c); }

    @Override
    public void clear() { queue.Clear(); }

    @Override
    public boolean add(T t)
    {
        queue.Enqueue(t);
        return true;
    }

    @Override
    public boolean offer(T t)
    {
        queue.Enqueue(t);
        return true;
    }

    @Override
    public T remove()
    {
        T removed = queue.TryDequeue();
        if (removed == null) {
            throw new NoSuchElementException();
        } else {
            return removed;
        }
    }

    @Override
    public T poll() { return queue.TryDequeue(); }

    @Override
    public T element()
    {
        T peeked = queue.TryPeek();
        if (peeked == null) {
            throw new NoSuchElementException();
        } else {
            return peeked;
        }
    }

    @Override
    public T peek() { return queue.TryPeek(); }
}
