package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

/**
 * A default implementation of the {@link IStack} interface, by using a reverse single linked list.
 * @param <T> The type of the elements that this stack will hold.
 */
public class SingleLinkedListBasedStack<T>
    implements IStack<T>
{
    private static final class Node<T>
    {
        public T Value;
        public Node<T> Parent;

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

    private Node<T> current;

    @Override
    public T TryPop()
    {
        if (current == null) {
            return null;
        } else {
            Node<T> p = current.Parent;
            T v = current.Value;
            current = p;
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
    }

    @Override
    public void Clear() { current = null; }

    @Override
    public IEnumerator<T> GetEnumerator() { return new Enumerator<>(current); }
}
