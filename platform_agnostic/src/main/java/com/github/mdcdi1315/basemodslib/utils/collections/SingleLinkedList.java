package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.JavaObjectEqualsEqualityComparer;

/**
 * Provides an implementation of the {@link IList} interface implemented using a pointer to the next node.
 * @param <T> The type of the elements to be stored to this single linked list object.
 */
public class SingleLinkedList<T>
    implements IList<T>
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

    private int count;
    @AllowNull
    private Node<T> root, current;
    @NotNull
    private final IEqualityComparer<T> comparer;

    /**
     * Initializes a new and empty instance of the {@link SingleLinkedList} class.
     */
    public SingleLinkedList()
    {
        count = 0;
        root = current = null;
        comparer = new JavaObjectEqualsEqualityComparer<>();
    }

    /**
     * Initializes a new and empty instance of the {@link SingleLinkedList} class,
     * which does utilize the specified {@link IEqualityComparer} for comparing and determining equality of the list's items.
     * @param comparer The equality comparer to be used. Can be {@code null}, in which case the default equality comparer will be instead used.
     */
    public SingleLinkedList(@AllowNull IEqualityComparer<T> comparer)
    {
        count = 0;
        root = current = null;
        this.comparer = (comparer == null) ? new JavaObjectEqualsEqualityComparer<>() : comparer;
    }

    @Override
    public T getItem(int index)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "The specified index was negative.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "The specified index was out of the list's bounds.");
        } else {
            int c = 0;
            Node<T> p = root;
            while (c < index) { c++; p = p.Next; }
            return p.Value;
        }
    }

    @Override
    public void setItem(int index, T value)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "The specified index was negative.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "The specified index was out of the list's bounds.");
        } else {
            int c = 0;
            Node<T> p = root;
            while (c < index) { c++; p = p.Next; }
            p.Value = value;
        }
    }

    @Override
    public int IndexOf(T item) {
        int index = 0;
        Node<T> p = root;
        while (p != null) {
            if (comparer.Equals(p.Value, item)) { return index; }
            index++;
            p = p.Next;
        }
        return -1;
    }

    @Override
    public void Insert(int index, T item) {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "The specified index was negative.");
        } else if (index > count) {
            throw new ArgumentOutOfRangeException("index", "The specified index was out of the list's bounds.");
        } else if (index == count) {
            // When index == count, it is like adding an item, so most appropriate here is to call the Add method.
            Add(item);
        } else {
            Node<T> p = root;
            while (p.Next != null) { p = p.Next; }
            p.Next = new Node<>(item);
            count++;
        }
    }

    @Override
    public void RemoveAt(int index) {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "The specified index was negative.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "The specified index was out of the list's bounds.");
        } else {
            if (count > 1) {
                int c = 0, i = index - 1;
                Node<T> p = root;
                while (c < i) { c++; p = p.Next; }
                // p.Next will be the element that is to be deleted
                p.Next = p.Next.Next;
            } else {
                root = null;
            }
            count--;
        }
    }

    @Override
    public int getCount() { return count; }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    public void Add(T item)
    {
        Node<T> n = new Node<>(item);
        if (count == 0) {
            root = current = n;
        } else {
            current.Next = n;
            current = n;
        }
        count++;
    }

    @Override
    public void Clear() {
        count = 0;
        root = current = null;
    }

    @Override
    public boolean Contains(T item)
    {
        Node<T> p = root;
        while (p != null) {
            if (comparer.Equals(p.Value, item)) { return true; }
            p = p.Next;
        }
        return false;
    }

    @Override
    public void CopyTo(T[] array, int arrayIndex)
        throws ArgumentNullException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(array);
        if (arrayIndex < 0) {
            throw new ArgumentOutOfRangeException("arrayIndex", "Array index cannot be a negative value.");
        } else if (arrayIndex + count > array.length) {
            throw new ArgumentException("The array does not have enough space to place all the elements of the current SingleLinkedList object.", "array");
        } else {
            Node<T> p = root;
            for (int I = arrayIndex; p != null; p = p.Next) { array[I++] = p.Value; }
        }
    }

    @Override
    public boolean Remove(T item)
    {
        Node<T> c = root, prev = null;
        while (c != null)
        {
            if (comparer.Equals(c.Value , item))
            {
                if (prev == null) {
                    root = c.Next;
                } else {
                    prev.Next = c.Next;
                }
                return true;
            }
            prev = c;
            c = c.Next;
        }
        return false;
    }

    public void ForEach(Action1<T> action)
    {
        ArgumentNullException.ThrowIfNull(action , "action");

        IEnumerator<T> en = new Enumerator<>(root);

        try {
            while (en.MoveNext()) { action.action(en.getCurrent()); }
        } finally {
            en.Dispose();
        }
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return new Enumerator<>(root); }
}
