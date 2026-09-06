package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;

/**
 * An {@link ITraversableStack} implementation by using an array as the backing storage.
 * @param <T> The type of the elements this stack will hold.
 * @since 1.0.19
 */
public class ArrayBasedStack<T>
    extends BaseEnumerable<T>
    implements ITraversableStack<T>,
        IArrayBasedCollection,
        ICloneable
{
    private int count;
    private Object[] elements;

    private static final class Synchronized<T>
        extends ArrayBasedStack<T>
        implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        public Synchronized(int capacity) { super(capacity); lock = new Object(); }

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
        public void TrimExcess() { synchronized (lock) { super.TrimExcess(); } }

        @Override
        public T DuplicateLastItem() { synchronized (lock) { return super.DuplicateLastItem(); } }

        @Override
        public IEnumerator<T> GetEnumerator() { synchronized (lock) { return super.GetEnumerator(); } }

        @Override
        public T GetItem(int index) throws ArgumentOutOfRangeException { synchronized (lock) { return super.GetItem(index); } }

        @Override
        @SuppressWarnings("unchecked")
        public void PushAll(T... items) throws OverflowException, ArgumentNullException { synchronized (lock) { super.PushAll(items); } }

        @Override
        public void PushAll(IEnumerable<T> items) throws OverflowException, ArgumentNullException { synchronized (lock) { super.PushAll(items); } }

        @Override
        public void EnsureCapacity(int n_elements) throws OverflowException, ArgumentOutOfRangeException { synchronized (lock) { super.EnsureCapacity(n_elements); } }

        @Override
        public Synchronized<T> Clone()
        {
            synchronized (lock)
            {
                Synchronized<T> ret = new Synchronized<>(super.count);
                System.arraycopy(super.elements, 0, ((ArrayBasedStack<T>)ret).elements, 0, super.count);
                ((ArrayBasedStack<T>)ret).count = super.count;
                return ret;
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <TO> ArrayBasedStack<TO> ConvertAll(Converter<T, TO> converter)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(converter, "converter");
            synchronized (lock)
            {
                Synchronized<TO> target = new Synchronized<>(super.count);

                for (int I = 0; I < super.count; I++)
                {
                    ((ArrayBasedStack<TO>)target).elements[I] = converter.convert((T)super.elements[I]);
                }
                ((ArrayBasedStack<TO>)target).count = super.count;

                return target;
            }
        }
    }

    /**
     * Initializes a new instance of the {@link ArrayBasedStack} class.
     */
    @Pure
    public ArrayBasedStack()
    {
        count = 0;
        elements = new Object[0];
    }

    /**
     * Initializes a new instance of the {@link ArrayBasedStack} class,
     * specifying the initial capacity that the newly created collection should have.
     * @param capacity The initial capacity of the newly created object.
     * @throws ArgumentOutOfRangeException {@code capacity} is less than 0.
     * @since 1.0.37
     */
    public ArrayBasedStack(int capacity)
        throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative value.");
        } else {
            count = 0;
            elements = new Object[capacity];
        }
    }

    /**
     * Creates a thread-safe stack.
     * @return An object extending the {@link ArrayBasedStack} class and is thread-safe.
     */
    @Pure
    @NotNull
    public static <T> ArrayBasedStack<T> CreateSynchronized() { return new Synchronized<>(); }

    /**
     * Creates a thread-safe array-based stack,
     * specifying the initial capacity that the backing array will have.
     * @param capacity The initial capacity of the newly created object.
     * @return An object extending the {@link ArrayBasedStack} class and is thread-safe.
     * @throws ArgumentOutOfRangeException {@code capacity} is less than 0.
     * @since 1.0.37
     */
    @NotNull
    public static <T> ArrayBasedStack<T> CreateSynchronized(int capacity) throws ArgumentOutOfRangeException { return new Synchronized<>(capacity); }

    private void EnlargeArray(int by)
    {
        int new_count = elements.length + by;
        if (new_count < 0) {
            // Overflow detected, throw
            throw new OverflowException("The stack has reached it's maximum capacity.");
        } else if (new_count > elements.length) {
            Object[] copy = new Object[new_count];
            if (count > 0) {
                System.arraycopy(elements, 0, copy, 0, count);
            }
            elements = copy;
        }
    }

    private void PushByList(IList<T> items)
    {
        int c = items.getCount();
        EnlargeArray(c);
        for (int I = 0; I < c; I++) { elements[count+I] = items.getItem(I); }
    }

    @Pure
    @Override
    public void Clear() { count = 0; }

    @Pure
    @Override
    public int GetCount() { return count; }

    @Pure
    @Override
    @MaybeNull
    @SuppressWarnings("unchecked")
    public T TryPop() { return (count > 0) ? (T)elements[--count] : null; }

    @Pure
    @Override
    @MaybeNull
    @SuppressWarnings("unchecked")
    public T TryPeek() { return (count > 0) ? (T)elements[count - 1] : null; }

    @NotNull
    @Override
    public IEnumerator<T> GetEnumerator() { return ReversedArrayEnumerator.ByBoundsCasted(elements, 0, count); }

    @Override
    public void Push(T item)
    {
        EnlargeArray(1);
        elements[count++] = item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T DuplicateLastItem()
    {
        T item = null;
        if (count > 0)
        {
            item = (T)elements[count - 1];
            EnlargeArray(1);
            elements[count++] = item;
        }
        return item;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
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

    @Override
    public void PushAll(IEnumerable<T> items)
            throws OverflowException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        if (items instanceof IList<T> list) {
            PushByList(list);
        } else {
            if (items instanceof ICollection<T> c) {
                EnlargeArray(c.getCount());
            } else if (items instanceof ITraversableCollection<T> t) {
                EnlargeArray(t.GetCount());
            }
            try (IEnumerator<T> enumerator = items.GetEnumerator())
            {
                while (enumerator.MoveNext()) { Push(enumerator.getCurrent()); }
            }
        }
    }

    /**
     * Specialization of the {@link #PushAll(IEnumerable)} method, for pushing elements statically known.
     * @param items The items to push on the stack.
     * @throws OverflowException Adding the specified items would cause the stack to overflow.
     * @throws ArgumentNullException {@code items} is {@code null}.
     */
    @SuppressWarnings("unchecked")
    public void PushAll(T... items)
            throws OverflowException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        int len = items.length;
        EnlargeArray(len);
        System.arraycopy(items, 0, elements, count, len);
        count += len;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <TO> ArrayBasedStack<TO> ConvertAll(Converter<T, TO> converter)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");
        ArrayBasedStack<TO> target = new ArrayBasedStack<>(count);

        for (int I = 0; I < count; I++)
        {
            target.elements[I] = converter.convert((T)elements[I]);
        }
        target.count = count;

        return target;
    }

    @NotNull
    @Override
    public ArrayBasedStack<T> Clone()
    {
        ArrayBasedStack<T> ret = new ArrayBasedStack<>(count);
        System.arraycopy(elements, 0, ret.elements, 0, count);
        ret.count = count;
        return ret;
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
