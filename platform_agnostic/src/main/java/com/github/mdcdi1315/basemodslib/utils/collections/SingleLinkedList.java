package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;
import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;
import com.github.mdcdi1315.basemodslib.utils.JavaObjectEqualsEqualityComparer;
import com.github.mdcdi1315.basemodslib.utils.collections.linkednodes.NodeWithNextPointer;
import com.github.mdcdi1315.basemodslib.utils.collections.linkednodes.NodeWithNextPointerEnumerator;

/**
 * Provides an implementation of the {@link IList} interface implemented using a pointer to the next node.
 * @param <T> The type of the elements to be stored to this single linked list object.
 */
public class SingleLinkedList<T>
    extends BaseEnumerable<T>
    implements IList<T>, ITraversableCollection<T>
{
    private static final class Synchronized<T>
        extends SingleLinkedList<T>
        implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        public Synchronized(IEnumerable<T> items) { super(items); lock = new Object(); }

        public Synchronized(IEqualityComparer<T> comparer) { super(comparer); lock = new Object(); }

        public Synchronized(IEnumerable<T> items, IEqualityComparer<T> comparer) { super(items, comparer); lock = new Object(); }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public T getItem(int index) { synchronized(lock) { return super.getItem(index); } }

        @Override
        public T GetItem(int index) throws ArgumentOutOfRangeException { synchronized(lock) { return super.GetItem(index); } }

        @Override
        public void setItem(int index, T value) { synchronized(lock) { super.setItem(index, value); } }

        @Override
        public int IndexOf(T item) { synchronized(lock) { return super.IndexOf(item); } }

        @Override
        public void Insert(int index, T item) { synchronized(lock) { super.Insert(index, item); } }

        @Override
        public void RemoveAt(int index) { synchronized(lock) { super.RemoveAt(index); } }

        @Override
        public void Add(T item) { synchronized(lock) { super.Add(item); } }

        @Override
        public void Clear() { synchronized(lock) { super.Clear(); } }

        @Override
        public boolean Contains(T item) { synchronized(lock) { return super.Contains(item); } }

        @Override
        public void CopyTo(T[] array, int arrayIndex) throws ArgumentNullException, ArgumentException { synchronized(lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public boolean Remove(T item) { synchronized(lock) { return super.Remove(item); } }

        @Override
        public void ForEach(Action1<T> action) { synchronized(lock) { super.ForEach(action); } }

        @Override
        public IEnumerator<T> GetEnumerator() { synchronized(lock) { return super.GetEnumerator(); } }

        @Override
        public SingleLinkedList<T> FilterBy(Predicate<T> predicate) throws ArgumentNullException { synchronized (lock) { return super.FilterBy(predicate); } }

        @Override
        public <TO> SingleLinkedList<TO> ConvertAll(Converter<T, TO> converter) throws ArgumentNullException { synchronized (lock) { return super.ConvertAll(converter, null); } }

        @Override
        public <TO> SingleLinkedList<TO> ConvertAll(Converter<T, TO> converter, IEqualityComparer<TO> comparer) throws ArgumentNullException { synchronized (lock) { return super.ConvertAll(converter, comparer); } }
    }

    private int count;
    @NotNull
    private final IEqualityComparer<T> comparer;
    @AllowNull
    private NodeWithNextPointer<T> root, current;

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
     * which does utilize the specified {@link IEqualityComparer} for comparing and determining equality of the list's items. Can be {@code null}.
     * @param comparer The equality comparer to be used. Can be {@code null}, in which case the default equality comparer will be instead used.
     */
    public SingleLinkedList(@AllowNull IEqualityComparer<T> comparer)
    {
        count = 0;
        root = current = null;
        this.comparer = (comparer == null) ? new JavaObjectEqualsEqualityComparer<>() : comparer;
    }

    /**
     * Initializes a new instance of the {@link SingleLinkedList} class from the specified items, using the default equality comparer for comparing elements.
     * @param items The items that the {@link SingleLinkedList} class will initially have.
     * @throws ArgumentNullException {@code items} is {@code null}.
     * @since 1.0.26
     */
    public SingleLinkedList(IEnumerable<T> items) throws ArgumentNullException { this(items, null); }

    /**
     * Initializes a new instance of the {@link SingleLinkedList} class from the specified items, and using the specified equality comparer for comparing elements.
     * @param items The items that the {@link SingleLinkedList} class will initially have.
     * @param comparer The {@link IEqualityComparer} instance to be used for comparing elements contained in the returned instance. Can be {@code null}.
     * @throws ArgumentNullException {@code items} is {@code null}.
     * @since 1.0.26
     */
    public SingleLinkedList(IEnumerable<T> items, @AllowNull IEqualityComparer<T> comparer)
        throws ArgumentNullException
    {
        this(comparer);
        ArgumentNullException.ThrowIfNull(items, "items");
        IEnumerator<T> enumerator = items.GetEnumerator();
        try {
            while (enumerator.MoveNext()) { Add(enumerator.getCurrent()); }
        } finally {
            enumerator.Dispose();
        }
    }

    /**
     * Creates a new thread-safe single linked list.
     * @return A new instance of the {@link SingleLinkedList} class that is thread-safe.
     * @since 1.0.19
     */
    public static <T> SingleLinkedList<T> CreateSynchronized() { return new Synchronized<>(); }

    /**
     * Creates a new thread-safe single linked list, which does utilize the specified
     * {@link IEqualityComparer} for comparing and determining equality of the list's items.
     * @return A new instance of the {@link SingleLinkedList} class that is thread-safe.
     * @since 1.0.19
     */
    public static <T> SingleLinkedList<T> CreateSynchronized(@AllowNull IEqualityComparer<T> comparer) { return new Synchronized<>(comparer); }

    /**
     * Creates an empty thread-safe list from the specified items, using the default equality comparer for comparing elements.
     * @param items The items that the {@link SingleLinkedList} class will initially have.
     * @return An object extending the {@link SingleLinkedList} class and is thread-safe.
     * @throws ArgumentNullException {@code items} is {@code null}.
     * @since 1.0.26
     */
    public static <T> SingleLinkedList<T> CreateSynchronized(IEnumerable<T> items) throws ArgumentNullException { return new Synchronized<>(items); }

    /**
     * Creates an empty thread-safe list from the specified items, and using the specified equality comparer for comparing elements.
     * @param items The items that the {@link SingleLinkedList} class will initially have.
     * @param comparer The {@link IEqualityComparer} instance to be used for comparing elements contained in the returned instance. Can be {@code null}.
     * @return An object extending the <see cref="ArrayBasedList{T}"/> class and is thread-safe.
     * @throws ArgumentNullException {@code items} is {@code null}.
     */
    public static <T> SingleLinkedList<T> CreateSynchronized(IEnumerable<T> items, IEqualityComparer<T> comparer) { return new Synchronized<>(items, comparer); }

    @Override
    public T getItem(int index)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "The specified index was negative.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "The specified index was out of the list's bounds.");
        } else {
            int c = 0;
            NodeWithNextPointer<T> p = root;
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
            NodeWithNextPointer<T> next_element;
            // Since NodeWithNextPointer has final the Value field, we are going to special case:
            if (index == 0) {
                // The root element is to be modified.
                next_element = root.Next;
                root = new NodeWithNextPointer<>(value, next_element);
            } else {
                // A next element from the root is to be modified.
                // index - 1 to get to the previous element and manipulate it's Next pointer.
                int I = 0, index_new = index - 1;
                NodeWithNextPointer<T> iterating = root;
                while (I < index_new) { I++; iterating = iterating.Next; }
                next_element = iterating.Next.Next;
                // Modify.
                iterating.Next = new NodeWithNextPointer<>(value, next_element);
                if (index == (count - 1))
                {
                    // Modify the current element to reflect the change there as well.
                    // We need to keep this in sync.
                    current = iterating.Next;
                }
            }
        }
    }

    @Override
    public int IndexOf(T item)
    {
        int index = 0;
        NodeWithNextPointer<T> p = root;
        while (p != null)
        {
            if (comparer.Equals(p.Value, item)) { return index; }
            index++;
            p = p.Next;
        }
        return -1;
    }

    @Override
    public void Insert(int index, T item)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "The specified index was negative.");
        } else if (index > count) {
            throw new ArgumentOutOfRangeException("index", "The specified index was out of the list's bounds.");
        } else if (index == count) {
            // When index == count, it is like adding an item, so most appropriate here is to call the Add method.
            Add(item);
        } else {
            NodeWithNextPointer<T> p = root;
            int I = 0, new_index = index - 1;
            while (I < new_index && p.Next != null) { p = p.Next; I++; }
            // Get the current next item, if any.
            NodeWithNextPointer<T> prev_next = p.Next;
            // Insert the item.
            p.Next = new NodeWithNextPointer<>(item, prev_next);
            // Increase count.
            count++;
        }
    }

    @Override
    public void RemoveAt(int index)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "The specified index was negative.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "The specified index was out of the list's bounds.");
        } else {
            if (count > 1) {
                int c = 0, i = index - 1;
                NodeWithNextPointer<T> p = root;
                while (c < i) { c++; p = p.Next; }
                // p.Next will be the element that is to be deleted
                p.Next = p.Next.Next;
                if (index == (count - 1))
                {
                    // Modify the current element to reflect the change there as well.
                    // We need to keep this in sync.
                    current = p;
                }
            } else {
                // current will be reassigned in the next Add operation, but let's just free memory.
                root = current = null;
            }
            count--;
        }
    }

    @Override
    public int GetCount() { return count; }

    @Override
    public int getCount() { return count; }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    public T GetItem(int index) throws ArgumentOutOfRangeException { return getItem(index); }

    @Override
    public void Add(T item)
    {
        NodeWithNextPointer<T> n = new NodeWithNextPointer<>(item);
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
        NodeWithNextPointer<T> p = root;
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
        ArgumentNullException.ThrowIfNull(array, "array");
        if (arrayIndex < 0) {
            throw new ArgumentOutOfRangeException("arrayIndex", "Array index cannot be a negative value.");
        } else if (arrayIndex + count > array.length) {
            throw new ArgumentException("The array does not have enough space to place all the elements of the current SingleLinkedList object.", "array");
        } else {
            NodeWithNextPointer<T> p = root;
            for (int I = arrayIndex; p != null; p = p.Next) { array[I++] = p.Value; }
        }
    }

    @Override
    public boolean Remove(T item)
    {
        NodeWithNextPointer<T> c = root, prev = null;
        while (c != null)
        {
            if (comparer.Equals(c.Value , item))
            {
                if (prev == null) {
                    root = c.Next;
                } else {
                    // We have a problem here: if c.Next is null, it means that c == this.current.
                    // So, we have to assign current = prev so that we don't break the collection.
                    if ((prev.Next = c.Next) == null) { current = prev; }
                }
                count--; // Forgot to subtract count by one!
                return true;
            }
            prev = c;
            c = c.Next;
        }
        return false;
    }

    @Override
    public <TO> SingleLinkedList<TO> ConvertAll(Converter<T, TO> converter, IEqualityComparer<TO> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");

        SingleLinkedList<TO> converted = new SingleLinkedList<>(comparer);

        IEnumerator<T> enumerator = GetEnumerator();
        try {
            while (enumerator.MoveNext()) { converted.Add(converter.convert(enumerator.getCurrent())); }
        } finally {
            enumerator.Dispose();
        }

        return converted;
    }

    @Override
    public SingleLinkedList<T> FilterBy(Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return this;
        } else if (FunctionManipulations.IsAlwaysFalse(predicate)) {
            return new SingleLinkedList<>(comparer);
        } else {
            IEnumerator<T> enumerator = GetEnumerator();
            SingleLinkedList<T> result = new SingleLinkedList<>(comparer);

            try {
                T item;
                while (enumerator.MoveNext()) {
                    if (predicate.predicate(item = enumerator.getCurrent())) { result.Add(item); }
                }
            } finally {
                enumerator.Dispose();
            }

            return result;
        }
    }

    // Now forwards to ForEachInEnumerable, and it provides better input validation + better controlling over when an exception was occurred in the passed method argument.
    public void ForEach(Action1<T> action) { Extensions.ForEachInEnumerable(this, action); }

    @Override
    public IEnumerator<T> GetEnumerator() { return new NodeWithNextPointerEnumerator<>(root); }

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
        sb.append(String.format("SingleLinkedList<?> (%d) { ", count));
        if (count == 0) {
            sb.append("<EMPTY>");
        } else {
            NodeWithNextPointer<T> p = root, next;
            while (p != null)
            {
                sb.append(p.Value);
                if ((next = p.Next) != null) { sb.append(", "); }
                p = next;
            }
        }
        sb.append(" }");
        return sb.toString();
    }
}
