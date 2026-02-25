package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * A default implementation of the {@link IStack} interface, by using a reverse single linked list. <br />
 * From 1.0.18, it now implements the {@link ITraversableStack} interface as well.
 * @param <T> The type of the elements that this stack will hold.
 */
public class SingleLinkedListBasedStack<T>
    implements ITraversableStack<T>
{
    private static final class Node<T>
    {
        @AllowNull
        public Node<T> Parent;

        public final T Value;

        public Node(T value)
        {
            Parent = null;
            Value = value;
        }
    }

    private static final class Enumerator<T>
            implements IEnumerator<T>
    {
        private boolean finished;
        private Node<T> point, current;

        public Enumerator(Node<T> point)
        {
            this.point = point;
            current = null;
            finished = false;
        }

        public void Dispose()
        {
            point = null;
            current = null;
            finished = true;
        }

        public boolean MoveNext()
        {
            if (finished) {
                return false;
            } else if ((current = (current == null) ? point : current.Parent) == null) {
                finished = true;
                return false;
            } else {
                return true;
            }
        }

        public void Reset()
        {
            current = null;
            finished = false;
        }

        @Override
        public T getCurrent() { return (current == null) ? null : current.Value; }
    }

    private int count;
    private Node<T> current;

    /**
     * Initializes a new instance of the {@link SingleLinkedListBasedStack} class.
     */
    public SingleLinkedListBasedStack()
    {
        count = 0;
        current = null;
    }

    @Override
    public T TryPop()
    {
        if (current == null) {
            return null;
        } else {
            Node<T> p = current.Parent;
            T v = current.Value;
            current = p;
            count--;
            return v;
        }
    }

    @Override
    public T TryPeek() { return (current == null) ? null : current.Value; }

    @Override
    public void Push(T item)
    {
        Node<T> n = new Node<>(item);
        n.Parent = current;
        current = n;
        count++;
    }

    @Override
    public void Clear() { current = null; count = 0; }

    @Override
    public int GetCount() { return count; }

    @Override
    public T GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "The specified index was negative.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "The specified index was out of the stack bounds.");
        } else {
            Node<T> p = current;
            int t = 0;
            while (t < index)
            {
                p = p.Parent;
                t++;
            }
            return p.Value;
        }
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return new Enumerator<>(current); }
}
