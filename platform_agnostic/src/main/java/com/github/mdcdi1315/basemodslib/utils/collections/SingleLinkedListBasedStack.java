package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.linkednodes.NodeWithPreviousPointer;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.linkednodes.NodeWithPreviousPointerEnumerator;

/**
 * A default implementation of the {@link IStack} interface, by using a reverse single linked list. <br />
 * Since 1.0.18, it implements the {@link ITraversableStack} interface as well.
 * @param <T> The type of the elements that this stack will hold.
 */
public class SingleLinkedListBasedStack<T>
    extends BaseEnumerable<T>
    implements ITraversableStack<T>, ICloneableEnumerable<T>
{
    private static final class Synchronized<T>
            extends SingleLinkedListBasedStack<T>
            implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public void Clear() { synchronized (lock) { super.Clear(); } }

        @Override
        public T TryPop() { synchronized (lock) { return super.TryPop(); } }

        @Override
        public T TryPeek() { synchronized (lock) { return super.TryPeek(); } }

        @Override
        public void Push(T item) { synchronized (lock) { super.Push(item); } }

        @Override
        public T DuplicateLastItem() { synchronized (lock) { return super.DuplicateLastItem(); } }

        @Override
        public IEnumerator<T> GetEnumerator() { synchronized (lock) { return super.GetEnumerator(); } }

        @Override
        public T GetItem(int index) throws ArgumentOutOfRangeException { synchronized (lock) { return super.GetItem(index); } }

        @Override
        public void PushAll(IEnumerable<T> items)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(items, "items");
            // Instead of taking the lock each time on every push, we will get it only once.
            synchronized (lock)
            {
                try (IEnumerator<T> enumerator = items.GetEnumerator())
                {
                    while (enumerator.MoveNext())
                    {
                        super.Push(enumerator.getCurrent());
                    }
                }
            }
        }

        @Override
        public Synchronized<T> Clone()
        {
            Synchronized<T> copy = new Synchronized<>();
            synchronized (lock)
            {
                ((SingleLinkedListBasedStack<T>)copy).current = super.current;
                ((SingleLinkedListBasedStack<T>)copy).count = super.count;
            }
            return copy;
        }
    }

    private int count;
    private NodeWithPreviousPointer<T> current;

    /**
     * Initializes a new instance of the {@link SingleLinkedListBasedStack} class.
     */
    public SingleLinkedListBasedStack()
    {
        count = 0;
        current = null;
    }

    /**
     * Creates a thread-safe stack.
     * @return An object extending the {@link SingleLinkedListBasedStack} class and is thread-safe.
     * @since 1.0.19
     */
    public static <T> SingleLinkedListBasedStack<T> CreateSynchronized() { return new Synchronized<>(); }

    @Override
    public T TryPop()
    {
        if (current == null) {
            return null;
        } else {
            NodeWithPreviousPointer<T> p = current.Previous;
            T v = current.Value;
            current = p;
            count--;
            return v;
        }
    }

    @Override
    public void Push(T item)
    {
        NodeWithPreviousPointer<T> n = new NodeWithPreviousPointer<>(item);
        n.Previous = current;
        current = n;
        count++;
    }

    @Override
    public T DuplicateLastItem()
    {
        if (current == null) {
            return null;
        } else {
            T value = current.Value;
            NodeWithPreviousPointer<T> n = new NodeWithPreviousPointer<>(value);
            n.Previous = current;
            current = n;
            count++;
            return value;
        }
    }

    @Override
    public SingleLinkedListBasedStack<T> Clone()
    {
        SingleLinkedListBasedStack<T> copy = new SingleLinkedListBasedStack<>();
        copy.count = this.count;
        copy.current = this.current;
        return copy;
    }

    @Override
    public T GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "The specified index was negative.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "The specified index was out of the stack bounds.");
        } else {
            NodeWithPreviousPointer<T> p = current;
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
    public int GetCount() { return count; }

    @Override
    public void Clear() { current = null; count = 0; }

    @Override
    public T TryPeek() { return (current == null) ? null : current.Value; }

    @Override
    public IEnumerator<T> GetEnumerator() { return new NodeWithPreviousPointerEnumerator<>(current); }

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
        sb.append(String.format("SingleLinkedListBasedStack<?> (%d) { ", count));
        if (count == 0) {
            sb.append("<EMPTY>");
        } else {
            NodeWithPreviousPointer<T> p = current, next;
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
