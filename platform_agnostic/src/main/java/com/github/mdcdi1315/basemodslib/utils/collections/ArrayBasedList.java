package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;
import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;
import com.github.mdcdi1315.basemodslib.utils.JavaObjectEqualsEqualityComparer;

/**
 * A custom implementation of the {@link IList} interface, backed by an array.
 * @param <T> The type of the elements that this list will store.
 * @since 1.0.19
 */
public class ArrayBasedList<T>
    implements
        IList<T>,
        ITraversableCollection<T>,
        IArrayBasedCollection,
        ISupportsDirectConversionTo<T>,
        ISupportsSlicing<T>,
        ISupportsFiltering<T>
{
    private int count;
    private Object[] elements;
    private final IEqualityComparer<T> comparer;

    /**
     * Initializes an empty instance of the {@link ArrayBasedList} class, using the default equality comparer for comparing elements.
     */
    public ArrayBasedList()
    {
        comparer = new JavaObjectEqualsEqualityComparer<>();
        elements = new Object[0];
        count = 0;
    }

    /**
     * Initializes an empty instance of the {@link ArrayBasedList} class, using the default equality comparer for comparing elements, and will have the specified initial capacity.
     * @param capacity The initial capacity that the returned object will have.
     * @throws ArgumentOutOfRangeException {@code capacity} is negative.
     */
    public ArrayBasedList(int capacity)
        throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative number.");
        } else {
            comparer = new JavaObjectEqualsEqualityComparer<>();
            elements = new Object[capacity];
            count = 0;
        }
    }

    /**
     * Initializes an empty instance of the {@link ArrayBasedList} class, using the specified equality comparer for comparing elements, and will have the specified initial capacity.
     * @param capacity The initial capacity that the returned object will have.
     * @param comparer The {@link IEqualityComparer} instance to be used for comparing elements contained in the returned instance.
     * @throws ArgumentOutOfRangeException {@code capacity} is negative.
     */
    public ArrayBasedList(int capacity, @AllowNull IEqualityComparer<T> comparer)
        throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative number.");
        } else {
            this.comparer = comparer == null ? new JavaObjectEqualsEqualityComparer<>() : comparer;
            elements = new Object[capacity];
            count = 0;
        }
    }

    /**
     * Initializes an empty instance of the {@link ArrayBasedList} class, using the specified equality comparer for comparing elements.
     * @param comparer The {@link IEqualityComparer} instance to be used for comparing elements contained in the returned instance.
     */
    public ArrayBasedList(@AllowNull IEqualityComparer<T> comparer)
    {
        this.comparer = comparer == null ? new JavaObjectEqualsEqualityComparer<>() : comparer;
        elements = new Object[0];
        count = 0;
    }

    /**
     * Initializes a new instance of the {@link ArrayBasedList} class from the specified items, using the default equality comparer for comparing elements.
     * @param items The items that the {@link ArrayBasedList} class will initially have.
     * @throws ArgumentNullException {@code items} is {@code null}.
     */
    public ArrayBasedList(IEnumerable<T> items)
        throws ArgumentNullException
    {
        this();
        AddRange(items);
    }

    /**
     * Initializes a new instance of the {@link ArrayBasedList} class from the specified items, and using the specified equality comparer for comparing elements.
     * @param items The items that the {@link ArrayBasedList} class will initially have.
     * @param comparer The {@link IEqualityComparer} instance to be used for comparing elements contained in the returned instance.
     * @throws ArgumentNullException {@code items} is {@code null}.
     */
    public ArrayBasedList(IEnumerable<T> items, @AllowNull IEqualityComparer<T> comparer)
        throws ArgumentNullException
    {
        this(comparer);
        AddRange(items);
    }

    private static final class Synchronized<T>
        extends ArrayBasedList<T>
        implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized()
        {
            super();
            this.lock = new Object();
        }

        public Synchronized(int capacity)
                throws ArgumentOutOfRangeException
        {
            super(capacity);
            this.lock = new Object();
        }

        public Synchronized(int capacity, IEqualityComparer<T> comparer)
                throws ArgumentOutOfRangeException
        {
            super(capacity, comparer);
            this.lock = new Object();
        }

        public Synchronized(IEqualityComparer<T> comparer)
        {
            super(comparer);
            this.lock = new Object();
        }

        public Synchronized(IEnumerable<T> items)
                throws ArgumentNullException
        {
            super(items);
            this.lock = new Object();
        }

        public Synchronized(IEnumerable<T> items, IEqualityComparer<T> comparer)
                throws ArgumentNullException
        {
            super(items, comparer);
            this.lock = new Object();
        }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public T GetItem(int index) throws ArgumentOutOfRangeException { synchronized (lock) { return getItem(index); } }

        @Override
        public T getItem(int index) throws ArrayIndexOutOfBoundsException { synchronized (lock) { return super.getItem(index); } }

        @Override
        public void setItem(int index, T value) throws ArgumentOutOfRangeException { synchronized (lock) { super.setItem(index, value); } }

        @Override
        public int IndexOf(T item) { synchronized (lock) { return super.IndexOf(item); } }

        @Override
        public void Insert(int index, T item) throws ArgumentOutOfRangeException, OverflowException { synchronized (lock) { super.Insert(index, item); } }

        @Override
        public void InsertRange(int index, T[] items) throws ArgumentOutOfRangeException, ArgumentNullException, OverflowException { synchronized (lock) { super.InsertRange(index, items); } }

        @Override
        public void RemoveAt(int index) { synchronized (lock) { super.RemoveAt(index); } }

        @Override
        public boolean Remove(T item) { synchronized (lock) { return super.Remove(item); } }

        @Override
        public void Add(T item) { synchronized (lock) { super.Add(item); } }

        @Override
        public void Clear() { synchronized (lock) { super.Clear(); } }

        @Override
        public boolean Contains(T item) { synchronized (lock) { return super.Contains(item); } }

        @Override
        public void CopyTo(T[] array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public void TrimExcess() { synchronized (lock) { super.TrimExcess(); } }

        @Override
        public void AddRange(IEnumerable<T> items) throws ArgumentOutOfRangeException { synchronized (lock) { super.AddRange(items); } }

        @Override
        public void AddRange(T... items) throws ArgumentNullException { synchronized (lock) { super.AddRange(items); } }

        @Override
        public void EnsureCapacity(int n_elements) throws ArgumentOutOfRangeException { synchronized (lock) { super.EnsureCapacity(n_elements); } }

        @Override
        public IEnumerator<T> GetEnumerator() { synchronized (lock) { return super.GetEnumerator(); } }

        @Override
        public ArrayBasedList<T> Slice(int count) throws ArgumentException { synchronized (lock) { return super.Slice(count); } }

        @Override
        public ArrayBasedList<T> Slice(int index, int count) throws ArgumentException { synchronized (lock) { return super.Slice(index, count); } }

        @Override
        public ArrayBasedList<T> FilterBy(Predicate<T> predicate) throws ArgumentNullException { synchronized (lock) { return super.FilterBy(predicate); } }

        @Override
        public <TO> ArrayBasedList<TO> ConvertAll(Converter<T, TO> converter) throws ArgumentNullException { synchronized (lock) { return super.ConvertAll(converter, null); } }

        @Override
        public <TG> ArrayBasedList<TG> ConvertAll(Converter<T, TG> converter, IEqualityComparer<TG> comparer) throws ArgumentNullException { synchronized (lock) { return super.ConvertAll(converter, comparer); } }
    }

    /**
     * Creates an empty thread-safe list, using the default equality comparer for comparing elements.
     * @return An object extending the {@link ArrayBasedList} class and is thread-safe.
     */
    public static <T> ArrayBasedList<T> CreateSynchronized() { return new Synchronized<>(); }

    /**
     * Creates an empty thread-safe list, using the default equality comparer for comparing elements, and will have the specified initial capacity.
     * @param capacity The initial capacity that the returned object will have.
     * @return An object extending the {@link ArrayBasedList} class and is thread-safe.
     * @throws ArgumentOutOfRangeException {@code capacity} is negative.
     */
    public static <T> ArrayBasedList<T> CreateSynchronized(int capacity) throws ArgumentOutOfRangeException { return new Synchronized<>(capacity); }

    /**
     * Creates an empty thread-safe list, using the specified equality comparer for comparing elements.
     * @param comparer The {@link IEqualityComparer} instance to be used for comparing elements contained in the returned instance.
     * @return An object extending the {@link ArrayBasedList} class and is thread-safe.
     */
    public static <T> ArrayBasedList<T> CreateSynchronized(@AllowNull IEqualityComparer<T> comparer) { return new Synchronized<>(comparer); }

    /**
     * Creates an empty thread-safe list, using the specified equality comparer for comparing elements, and will have the specified initial capacity.
     * @param capacity The initial capacity that the returned object will have.
     * @param comparer The {@link IEqualityComparer} instance to be used for comparing elements contained in the returned instance.
     * @return An object extending the {@link ArrayBasedList} class and is thread-safe.
     * @throws ArgumentOutOfRangeException {@code capacity} is negative.
     */
    public static <T> ArrayBasedList<T> CreateSynchronized(int capacity, @AllowNull IEqualityComparer<T> comparer) throws ArgumentOutOfRangeException { return new Synchronized<>(capacity, comparer); }

    /**
     * Creates an empty thread-safe list from the specified items, using the default equality comparer for comparing elements.
     * @param items The items that the {@link ArrayBasedList} class will initially have.
     * @return An object extending the {@link ArrayBasedList} class and is thread-safe.
     * @throws ArgumentNullException {@code items} is {@code null}.
     */
    public static <T> ArrayBasedList<T> CreateSynchronized(IEnumerable<T> items) throws ArgumentNullException { return new Synchronized<>(items); }

    /**
     * Creates an empty thread-safe list from the specified items, and using the specified equality comparer for comparing elements.
     * @param items The items that the {@link ArrayBasedList} class will initially have.
     * @param comparer The {@link IEqualityComparer} instance to be used for comparing elements contained in the returned instance.
     * @return An object extending the <see cref="ArrayBasedList{T}"/> class and is thread-safe.
     * @throws ArgumentNullException {@code items} is {@code null}.
     */
    public static <T> ArrayBasedList<T> CreateSynchronized(IEnumerable<T> items, @AllowNull IEqualityComparer<T> comparer) throws ArgumentNullException { return new Synchronized<>(items, comparer); }

    private void EnlargeArray(int by)
    {
        int new_count = count + by;
        if (new_count < 0) {
            // Overflow detected, throw
            throw new OverflowException("The list has reached it's maximum capacity.");
        } else {
            // Check whether we can add 10 more elements to avoid additional resizes.
            int nc_additional = new_count + 10;
            // If nc_additional > 0, we can do that, otherwise we have overflown by this and as such we need to resize by new_count.
            EnlargeInternal(nc_additional > 0 ? nc_additional : new_count);
        }
    }

    private void EnlargeInternal(int nc)
    {
        if (nc > elements.length) {
            Object[] new_elements = new Object[nc];
            System.arraycopy(elements, 0, new_elements, 0, elements.length);
            elements = new_elements;
        }
    }

    @Override
    public T getItem(int index)
        throws ArrayIndexOutOfBoundsException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            return (T) elements[index];
        }
    }

    @Override
    public void setItem(int index, @AllowNull T value)
        throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            elements[index] = value;
        }
    }

    @Override
    public int GetCount() { return count; }

    @Override
    public T GetItem(int index) throws ArgumentOutOfRangeException { return getItem(index); }

    private record IndexOfPredicate<T>(IEqualityComparer<T> eqc, T item)
        implements Predicate<Object>
    {
        @Override
        public boolean predicate(Object obj) { return eqc.Equals((T)obj, item); }
    }

    @Override
    public int IndexOf(T item) {
        return Array.FindIndex(elements,0 , count, new IndexOfPredicate<>(comparer, item));
    }

    @Override
    public void Insert(int index, T item) throws ArgumentOutOfRangeException, OverflowException { InsertRangeInternal(index, new Object[]{ item }); }

    public void InsertRange(int index, T[] items)
        throws ArgumentOutOfRangeException, ArgumentNullException, OverflowException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        InsertRangeInternal(index, items);
    }

    private void InsertRangeInternal(int index, Object[] items)
            throws ArgumentOutOfRangeException, ArgumentNullException, OverflowException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index > count) {
            // index == count is allowed because it is insertion op.
            throw new ArgumentOutOfRangeException("index", "Index was out of the array's bounds.");
        } else if (count > 0) {
            Object[] constructed = new Object[count + items.length];

            // We are going to adapt the 'elements' array in the following way:
            // [0..index-1] -> Elements remain as is.
            // [index..index+items.Length] -> Elements from the 'items' array are put.
            // [index+items.Length..count] -> Element at index goes to index+items.Length, and the rest items are copied as-is.

            Array.Copy(elements, 0, constructed, 0, index);

            Array.Copy(items, 0, constructed, index, items.length);

            int rem_items = count - index;

            if (rem_items > 0) {
                Array.Copy(elements, index, constructed, index + items.length, rem_items);
            }

            elements = constructed;
            count += items.length;
        } else if (index == 0) {
            elements = new Object[items.length];
            Array.Copy(items, 0, elements, 0, items.length);
            count = items.length;
        }
    }

    @Override
    public void RemoveAt(int index)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            int index_after = index + 1;
            Array.Copy(elements, index_after, elements, index, count - index_after);
            count--;
        }
    }

    @Override
    public boolean Remove(T item)
    {
        int index = IndexOf(item);
        if (index > -1) {
            RemoveAt(index);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getCount() { return count; }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    public void Add(T item)
    {
        EnlargeArray(1);
        elements[count++] = item;
    }

    @Override
    public void Clear() { count = 0; }

    @Override
    public boolean Contains(T item) { return IndexOf(item) > -1; }

    @Override
    public void CopyTo(T[] array, int arrayIndex) {
        try {
            Array.Copy(elements, array, arrayIndex);
        } catch (IndexOutOfRangeException e) {
            throw new ArgumentOutOfRangeException("arrayIndex", "Not enough space to store all the list's elements to the specified array.");
        }
    }

    /**
     * Removes the entries that are inaccessible because they were removed. <br />
     * This optimizes the memory usage of the current object.
     */
    @Override
    public void TrimExcess()
    {
        if (count == 0) {
            elements = new Object[0];
        } else if (count != elements.length) {
            Object[] new_elements = new Object[count];
            System.arraycopy(elements, 0, new_elements, 0, count);
            elements = new_elements;
        }
    }

    public void AddRange(IEnumerable<T> items)
            throws ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        if (items instanceof ArrayBasedList<T> list) {
            AddRange_List(list);
        } else if (items instanceof IList<T> other_list) {
            AddRange_List(other_list);
        } else {
            if (items instanceof ICollection<T> collection) {
                EnlargeArray(collection.getCount());
            } else if (items instanceof ITraversableCollection<T> c) {
                EnlargeArray(c.GetCount());
            }
            IEnumerator<T> en = items.GetEnumerator();
            try {
                while (en.MoveNext()) { Add(en.getCurrent()); }
            } finally {
                en.Dispose();
            }
        }
    }

    public void AddRange(T... items)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(items);
        AddRange_Array(items);
    }

    @Override
    public void EnsureCapacity(int n_elements)
            throws ArgumentOutOfRangeException
    {
        if (n_elements < 0) {
            throw new ArgumentOutOfRangeException("n_elements", "Number of elements to be ensured of should not be negative.");
        } else {
            EnlargeArray(n_elements);
        }
    }

    private void AddRange_Array(T[] items)
    {
        int c = items.length;
        EnlargeArray(c);
        Array.Copy(items, 0, elements, count, c);
        count += c;
    }

    private void AddRange_List(IList<T> items)
    {
        int ct = items.getCount();
        EnlargeArray(ct);
        for (int I = 0; I < ct; I++) { elements[count + I] = items.getItem(I); }
        count += ct;
    }

    private void AddRange_List(ArrayBasedList<T> list)
    {
        int ct = list.count;
        EnlargeArray(ct);
        Array.Copy(list.elements, 0, elements, count, ct);
        count += ct;
    }

    /**
     * Converts all the elements of the current {@link ArrayBasedList} class instance and creates a new instance of type {@link ArrayBasedList} that contains the converted elements.
     * @param converter The function that can convert an instance of type {@link T} to an instance of type {@link TG}.
     * @param comparer If required by the user, an {@link IEqualityComparer} implementation to use for the newly created list object.
     * @return The converted list object that contains onl elements of type {@link TG}.
     * @param <TG> The type of the converted elements that the returned instance will contain.
     * @throws ArgumentNullException {@code converter} is {@code null}.
     */
    public <TG> ArrayBasedList<TG> ConvertAll(Converter<T, TG> converter, @AllowNull IEqualityComparer<TG> comparer)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");

        ArrayBasedList<TG> tg = new ArrayBasedList<>(count, comparer);
        for (int I = 0; I < count; I++) {
            tg.elements[I] = converter.convert((T) elements[I]);
        }
        tg.count = count;

        return tg;
    }

    /**
     * Returns a portion of the {@link ArrayBasedList} object, specified by the {@code index} and {@code count} parameters.
     * @param index The index to start the slice from.
     * @param count The number of elements to include into the resulting {@link ArrayBasedList} object.
     * @return A new {@link ArrayBasedList} object that is the slice of the current object.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     * @throws ArgumentException {@code index} + {@code count} value does exceed the list's bounds.
     */
    public ArrayBasedList<T> Slice(int index, int count)
        throws ArgumentOutOfRangeException, ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else {
            int total = index + count;
            if (total > this.count || total < 0) {
                throw new ArgumentException("The specified combination of index and count parameters exceed the list's bounds.");
            } else {
                ArrayBasedList<T> ret = new ArrayBasedList<>(count, comparer);
                Array.Copy(elements, index, ret.elements, 0, count);
                ret.count = count;
                return ret;
            }
        }
    }

    /**
     * Returns a portion of the {@link ArrayBasedList} object, specified by the {@code count} parameter.
     * @param count The number of elements to include into the resulting {@link ArrayBasedList} object.
     * @return A new {@link ArrayBasedList} object that is the slice of the current object.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     * @throws ArgumentException {@code count} value does exceed the list's bounds.
     * @since 1.0.26
     */
    public ArrayBasedList<T> Slice(int count)
        throws ArgumentOutOfRangeException, ArgumentException
    {
        if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (count > this.count) {
            throw new ArgumentException("The specified combination of index and count parameters exceed the list's bounds.");
        } else {
            ArrayBasedList<T> ret = new ArrayBasedList<>(count, comparer);
            Array.Copy(elements, 0, ret.elements, 0, count);
            ret.count = count;
            return ret;
        }
    }

    @Override
    public ArrayBasedList<T> FilterBy(Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return this;
        } else if (FunctionManipulations.IsAlwaysFalse(predicate)) {
            return new ArrayBasedList<>(comparer);
        } else {
            IEnumerator<T> enumerator = GetEnumerator();
            ArrayBasedList<T> result = new ArrayBasedList<>(count, comparer);

            try {
                T item;
                while (enumerator.MoveNext()) {
                    if (predicate.predicate(item = enumerator.getCurrent())) { result.Add(item); }
                }
            } finally {
                enumerator.Dispose();
            }

            result.TrimExcess();
            return result;
        }
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return ArrayEnumerator.ByBounds((T[]) elements, 0, count); }

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
        sb.append(String.format("ArrayBasedList<?> (%d) { ", count));
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
