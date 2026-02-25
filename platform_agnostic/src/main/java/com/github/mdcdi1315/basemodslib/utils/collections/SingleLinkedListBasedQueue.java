package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * An implementation of the {@link ITraversableQueue} interface by using a technique similar to the {@link java.util.LinkedList} class implementation.
 * @param <T> The type of the elements that this queue will hold.
 * @since 1.0.18
 */
public class SingleLinkedListBasedQueue<T>
    implements ITraversableQueue<T>
{
    private static final class Node<T>
    {
        @AllowNull
        public Node<T> before;

        public final T value;

        public Node(T value, @AllowNull Node<T> before)
        {
            this.value = value;
            this.before = before;
        }
    }

    private static final class Enumerator<T>
            implements IEnumerator<T>
    {
        private Node<T> head, current;

        public Enumerator(Node<T> head)
        {
            this.head = head;
            current = null;
        }

        public T getCurrent() { return current.value; }

        public void Dispose() { current = head = null; }

        public boolean MoveNext() {
            return (current == null) ? (current = head) != null : (current = current.before) != null;
        }

        public void Reset() { current = null; }
    }

    private int count;
    private Node<T> tail, head;

    /**
     * Initializes a new instance of the {@link SingleLinkedListBasedQueue} class.
     */
    public SingleLinkedListBasedQueue()
    {
        count = 0;
        tail = head = null;
    }

    @Override
    public T TryDequeue()
    {
        if (count < 1) {
            return null;
        } else {
            T v = head.value;
            head = head.before;
            count--;
            return v;
        }
    }

    @Override
    public T TryPeek() { return (count < 1) ? null : head.value; }

    @Override
    public void Enqueue(T item)
    {
        Node<T> t = new Node<>(item, null);
        switch (count++)
        {
            case 0:
                head = t;
                break;
            case 1:
                head.before = tail = t;
                break;
            default:
                tail.before = t;
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
            Node<T> p = head;
            int t = 0;
            while (t < index)
            {
                p = p.before;
                t++;
            }
            return p.value;
        }
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return new Enumerator<>(head); }
}
