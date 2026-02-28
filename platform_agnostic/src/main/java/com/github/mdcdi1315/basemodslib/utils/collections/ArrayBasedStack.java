package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Array;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.OverflowException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;

/**
 * An {@link ITraversableStack} implementation by using an array as the backing storage.
 * @param <T> The type of the elements this stack will hold.
 * @since 1.0.19
 */
public class ArrayBasedStack<T>
    implements ITraversableStack<T>, IArrayBasedCollection
{
    private int count;
    private Object[] elements;

    private static final class Synchronized<T>
        extends ArrayBasedStack<T>
        implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public T TryPop() { synchronized (lock) { return super.TryPop(); } }

        @Override
        public T TryPeek() { synchronized (lock) { return super.TryPeek(); } }

        @Override
        public void Push(T item) { synchronized (lock) { super.Push(item); } }

        @Override
        public void Clear() { synchronized (lock) { super.Clear(); } }

        @Override
        public IEnumerator<T> GetEnumerator() { synchronized (lock) { return super.GetEnumerator(); } }

        @Override
        public T GetItem(int index) throws ArgumentOutOfRangeException { synchronized (lock) { return super.GetItem(index); } }

        @Override
        public void TrimExcess() { synchronized (lock) { super.TrimExcess(); } }

        @Override
        public void EnsureCapacity(int n_elements) throws OverflowException, ArgumentOutOfRangeException { synchronized (lock) { super.EnsureCapacity(n_elements); } }

        @Override
        public void PushAll(IEnumerable<T> items) throws OverflowException, ArgumentNullException { synchronized (lock) { super.PushAll(items); } }

        @Override
        public void PushAll(T... items) throws OverflowException, ArgumentNullException { synchronized (lock) { super.PushAll(items); } }
    }

    /**
     * Creates a new instance of the {@link ArrayBasedStack} class.
     */
    public ArrayBasedStack()
    {
        count = 0;
        elements = new Object[0];
    }

    /**
     * Creates a thread-safe stack.
     * @return An object extending the {@link ArrayBasedStack} class and is thread-safe.
     */
    public static <T> ArrayBasedStack<T> CreateSynchronized() { return new Synchronized<>(); }

    @Override
    public T TryPop() { return (count > 0) ? (T)elements[--count] : null; }

    @Override
    public T TryPeek() { return (count > 0) ? (T)elements[count - 1] : null; }

    @Override
    public void Push(T item)
    {
        EnlargeArray(1);
        elements[count++] = item;
    }

    @Override
    public void Clear() { count = 0; }

    @Override
    public IEnumerator<T> GetEnumerator() { return ReversedArrayEnumerator.ByBounds((T[])elements, 0, count); }

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
            return (T) elements[count - index];
        }
    }

    private void EnlargeArray(int by)
    {
        int new_count = elements.length + by;
        if (new_count < 0) {
            // Overflow detected, throw
            throw new OverflowException("The stack has reached it's maximum capacity.");
        } else if (new_count > elements.length) {
            Object[] copy = new Object[new_count];
            if (count > 0) {
                Array.Copy(elements, 0, copy, 0, count);
            }
            elements = copy;
        }
    }

    @Override
    public void TrimExcess()
    {
        if (count == 0) {
            elements = new Object[0];
        } else if (count != elements.length) {
            Object[] copy = new Object[count];
            Array.Copy(elements, 0, copy, 0, count);
            elements = copy;
        }
    }

    @Override
    public void EnsureCapacity(int n_elements)
            throws OverflowException, ArgumentOutOfRangeException
    {
        if (n_elements < 0) {
            throw new ArgumentOutOfRangeException("n_elements", "Number of elements to be ensured of should not be negative.");
        } else {
            EnlargeArray(n_elements);
        }
    }

    public void PushAll(IEnumerable<T> items)
            throws OverflowException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        if (items instanceof IList<T> list) {
            PushByList(list);
        } else if (items instanceof ICollection<T> collection) {
            PushByCollection(collection);
        } else {
            IEnumerator<T> enumerator = items.GetEnumerator();
            try {
                while (enumerator.MoveNext()) { Push(enumerator.getCurrent()); }
            } finally {
                enumerator.Dispose();
            }
        }
    }

    /**
     * Specialization of the {@link #PushAll(IEnumerable)} method, for pushing elements statically known.
     * @param items The items to push on the stack.
     * @throws OverflowException Adding the specified items would cause the stack to overflow.
     * @throws ArgumentNullException {@code items} is {@code null}.
     */
    public void PushAll(T... items)
            throws OverflowException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        int len = items.length;
        EnlargeArray(len);
        Array.Copy(items, 0, elements, count, len);
        count += len;
    }

    private void PushByList(IList<T> items)
    {
        int c = items.getCount();
        EnlargeArray(c);
        for (int I = 0; I < c; I++) { elements[count+I] = items.getItem(I); }
    }

    private void PushByCollection(ICollection<T> items)
    {
        int c = items.getCount();
        EnlargeArray(c);
        int I = 0;
        IEnumerator<T> e = items.GetEnumerator();
        try {
            while (e.MoveNext()) { elements[count + (I++)] = e.getCurrent(); }
        } finally {
            e.Dispose();
        }
    }

    /**
     * Provides a string representation of this object. <br />
     * For debugging purposes only.
     * @return A string representation of this object.
     */
    @NotNull
    @Override
    public final String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("ArrayBasedStack<?> (%d) { ", count));
        switch (count)
        {
            case 0:
                sb.append("<EMPTY>");
                break;
            case 1:
                sb.append(elements[0]);
                break;
            default:
                int bound = count - 1;
                for (int I = 0; I < bound; I++) {
                    sb.append(elements[I]);
                    sb.append(", ");
                }
                sb.append(elements[bound]);
                break;
        }
        sb.append(" }");
        return sb.toString();
    }
}
