package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Provides a simple and fast implementation of the {@link IRegister} interface. <br />
 * It is based off on a single-linked-list that has addition time O(1), and traversal time O(n),
 * where {@code n} the number of elements registered.
 * @param <T> The type of elements to be registered and enumerated at a later time.
 */
public class SingleLinkedListBasedRegister<T>
    implements IRegister<T>
{
    private static final class Node<T>
    {
        public T Value;
        public Node<T> Next;

        public Node(T value)
        {
            Next = null;
            Value = value;
        }
    }

    private static final class Enumerator<T>
        implements IEnumerator<T>
    {
        private boolean reset;
        private Node<T> root, current;

        public Enumerator(Node<T> rt)
        {
            root = rt;
            reset = true;
            current = null;
        }

        @Override
        public T getCurrent() { return current.Value; }

        @Override
        public boolean MoveNext()
        {
            Node<T> t_next;
            if (reset) {
                reset = false;
                return (current = root) != null;
            } else if ((t_next = current.Next) != null) {
                current = t_next;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void Reset() { reset = true; }

        @Override
        public void Dispose() { root = current = null; }
    }

    @AllowNull
    private Node<T> root, current;

    /**
     * Initializes a new and empty instance of the {@link SingleLinkedListBasedRegister} class.
     */
    public SingleLinkedListBasedRegister() { root = current = null; }

    @Override
    public void Register(T item)
            throws ArgumentException
    {
        Node<T> n = new Node<>(item);
        if (root == null) {
            root = current = n;
        } else {
            current.Next = n;
            current = n;
        }
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return new Enumerator<>(root); }
}
