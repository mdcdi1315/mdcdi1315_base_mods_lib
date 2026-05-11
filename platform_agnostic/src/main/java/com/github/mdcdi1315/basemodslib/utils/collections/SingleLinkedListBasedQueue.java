package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;
import com.github.mdcdi1315.basemodslib.utils.collections.linkednodes.NodeWithPreviousPointer;
import com.github.mdcdi1315.basemodslib.utils.collections.linkednodes.NodeWithPreviousPointerEnumerator;

/**
 * An implementation of the {@link ITraversableQueue} interface by using a technique similar to the {@link java.util.LinkedList} class implementation.
 * @param <T> The type of the elements that this queue will hold.
 * @since 1.0.18
 */
public class SingleLinkedListBasedQueue<T>
    extends BaseEnumerable<T>
    implements ITraversableQueue<T>
{
    private static final class Synchronized<T>
            extends SingleLinkedListBasedQueue<T>
            implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public T TryDequeue() { synchronized (lock) { return super.TryDequeue(); } }

        @Override
        public T TryPeek() { synchronized (lock) { return super.TryPeek(); } }

        @Override
        public void Enqueue(T item) { synchronized(lock) { super.Enqueue(item); } }

        @Override
        public IEnumerator<T> GetEnumerator() { synchronized(lock) { return super.GetEnumerator(); } }

        @Override
        public void Clear() { synchronized(lock) { super.Clear(); } }

        @Override
        public T GetItem(int index) throws ArgumentOutOfRangeException { synchronized(lock) { return super.GetItem(index); } }

        @Override
        public void EnqueueAll(IEnumerable<T> items)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(items, "items");
            // Instead of taking the lock each time on every enqueue, we will get it only once.
            synchronized (lock) {
                IEnumerator<T> enumerator = items.GetEnumerator();
                try {
                    while (enumerator.MoveNext()) { super.Enqueue(enumerator.getCurrent()); }
                } finally {
                    enumerator.Dispose();
                }
            }
        }
    }

    private int count;
    private NodeWithPreviousPointer<T> tail, head;

    /**
     * Initializes a new instance of the {@link SingleLinkedListBasedQueue} class.
     */
    public SingleLinkedListBasedQueue()
    {
        count = 0;
        tail = head = null;
    }

    /**
     * Creates a thread-safe queue.
     * @return An object extending the {@link SingleLinkedListBasedQueue} class and is thread-safe.
     * @since 1.0.19
     */
    public static <T> SingleLinkedListBasedQueue<T> CreateSynchronized() { return new Synchronized<>(); }

    @Override
    public T TryDequeue()
    {
        if (count < 1) {
            return null;
        } else {
            T v = head.Value;
            head = head.Previous;
            count--;
            return v;
        }
    }

    @Override
    public T TryPeek() { return (count < 1) ? null : head.Value; }

    @Override
    public void Enqueue(T item)
    {
        NodeWithPreviousPointer<T> t = new NodeWithPreviousPointer<>(item);
        switch (count++)
        {
            case 0:
                head = t;
                break;
            case 1:
                head.Previous = tail = t;
                break;
            default:
                tail.Previous = t;
                tail = t;
                break;
        }
    }

    @Override
    public void Clear() {
        count = 0;
        tail = head = null;
    }

    @Override
    public int GetCount() { return count; }

    @Override
    public T GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "The specified index was negative.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "The specified index was out of the queue bounds.");
        } else {
            NodeWithPreviousPointer<T> p = head;
            int t = 0;
            while (t < index)
            {
                p = p.Previous;
                t++;
            }
            return p.Value;
        }
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return new NodeWithPreviousPointerEnumerator<>(head); }

    /**
     * Provides a string representation of this object. <br />
     * For debugging purposes only.
     * @return A string representation of this object.
     * @since 1.0.31
     */
    @NotNull
    @Override
    public final String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("SingleLinkedListBasedQueue<?> (%d) { ", count));
        if (count == 0) {
            sb.append("<EMPTY>");
        } else {
            NodeWithPreviousPointer<T> p = head, next;
            while (p != null)
            {
                sb.append(p.Value);
                if ((next = p.Previous) != null) { sb.append(", "); }
                p = next;
            }
        }
        sb.append(" }");
        return sb.toString();
    }
}
