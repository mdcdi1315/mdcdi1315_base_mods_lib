package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;
import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;

/**
 * Provides an {@link IList} implementation based on an array that it's size cannot be changed.
 * @param <T> The type of the elements that this list will store.
 * @since 1.0.37
 */
public class FixedArrayBasedList<T>
    extends BaseEnumerable<T>
    implements IList<T>,
        IReadOnlyList<T>,
        ISupportsCloning<T>
{
    private int count;
    private final int index;
    private final Object[] array;
    @AllowNull
    private final IEqualityComparer<T> comparer;

    /**
     * Initializes an empty instance of the {@link FixedArrayBasedList} class, using the default equality comparer for comparing elements.
     * @param capacity The maximum capacity that the list object will have.
     * @throws ArgumentOutOfRangeException {@code capacity} is negative.
     */
    public FixedArrayBasedList(int capacity) throws ArgumentOutOfRangeException { this(capacity, null); }

    /**
     * Initializes an empty instance of the {@link FixedArrayBasedList} class, specifying the equality comparer to use for comparing elements.
     * @param capacity The maximum capacity that the list object will have.
     * @param comparer The {@link IEqualityComparer} instance to be used for comparing elements contained in the returned instance.
     * @throws ArgumentOutOfRangeException {@code capacity} is negative.
     */
    public FixedArrayBasedList(int capacity, @AllowNull IEqualityComparer<T> comparer)
            throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative value.");
        } else {
            index = 0;
            count = 0;
            array = new Object[capacity];
            this.comparer = (comparer == null) ? new EqualityComparer.ObjectEqualityComparer<>() : comparer;
        }
    }

    private FixedArrayBasedList(Object[] elements, int index, int count, @AllowNull IEqualityComparer<T> comparer)
    {
        this.index = index;
        this.count = count;
        this.array = elements;
        this.comparer = (comparer == null) ? new EqualityComparer.ObjectEqualityComparer<>() : comparer;
    }

    /**
     * Creates an empty thread-safe list, using the default equality comparer for comparing elements, and will have the specified maximum capacity.
     * @param capacity The maximum capacity that the returned object will have.
     * @return An object extending the {@link FixedArrayBasedList} class and is thread-safe.
     * @throws ArgumentOutOfRangeException {@code capacity} is negative.
     */
    @NotNull
    public static <T> FixedArrayBasedList<T> CreateSynchronized(int capacity) throws ArgumentOutOfRangeException { return new Synchronized<>(capacity); }

    /**
     * Creates an empty thread-safe list, using the specified equality comparer for comparing elements, and will have the specified maximum capacity.
     * @param capacity The maximum capacity that the returned object will have.
     * @param comparer The {@link IEqualityComparer} instance to be used for comparing elements contained in the returned instance.
     * @return An object extending the {@link FixedArrayBasedList} class and is thread-safe.
     * @throws ArgumentOutOfRangeException {@code capacity} is negative.
     */
    @NotNull
    public static <T> FixedArrayBasedList<T> CreateSynchronized(int capacity, @AllowNull IEqualityComparer<T> comparer) throws ArgumentOutOfRangeException { return new Synchronized<>(capacity, comparer); }

    private static final class Synchronized<T>
        extends FixedArrayBasedList<T>
        implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized(int capacity) { super(capacity); lock = new Object(); }

        public Synchronized(int capacity, IEqualityComparer<T> comparer) { super(capacity, comparer); lock = new Object(); }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public int getCount() { return super.getCount(); }

        @Override
        public int GetCount() { return super.GetCount(); }

        @Override
        public void Clear() { synchronized (lock) { super.Clear(); } }

        @Override
        public void Add(T item) { synchronized (lock) { super.Add(item); } }

        @Override
        public int IndexOf(T item) { synchronized (lock) { return super.IndexOf(item); } }

        @Override
        public boolean Remove(T item) { synchronized (lock) { return super.Remove(item); } }

        @Override
        public boolean getIsReadOnly() { synchronized (lock) { return super.getIsReadOnly(); } }

        @Override
        public boolean Contains(T item) { synchronized (lock) { return super.Contains(item); } }

        @Override
        public void Insert(int index, T item) { synchronized (lock) { super.Insert(index, item); } }

        @Override
        public IEnumerator<T> GetEnumerator() { synchronized (lock) { return super.GetEnumerator(); } }

        @Override
        public void RemoveAt(int index) throws ArgumentOutOfRangeException { synchronized (lock) { super.RemoveAt(index); } }

        @Override
        public T GetItem(int index) throws ArgumentOutOfRangeException { synchronized (lock) { return super.GetItem(index); } }

        @Override
        public T getItem(int index) throws ArgumentOutOfRangeException { synchronized (lock) { return super.getItem(index); } }

        @Override
        public FixedArrayBasedList<T> Slice(int count) throws ArgumentException { synchronized (lock) { return super.Slice(count); } }

        @Override
        public void setItem(int index, T item) throws ArgumentOutOfRangeException { synchronized (lock) { super.setItem(index, item); } }

        @Override
        public void CopyTo(T[] array, int arrayIndex) throws ArgumentOutOfRangeException { synchronized (lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public FixedArrayBasedList<T> Slice(int index, int count) throws ArgumentException { synchronized (lock) { return super.Slice(index, count); } }

        @Override
        public <TO> Synchronized<TO> ConvertAll(Converter<T, TO> converter) throws ArgumentNullException { return ConvertAll(converter, null); }

        @Override
        public Synchronized<T> FilterBy(Predicate<T> predicate)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(predicate, "predicate");
            if (FunctionManipulations.IsAlwaysTrue(predicate)) {
                return this;
            } else if (CollectionManipulations.IsEmpty(this) || FunctionManipulations.IsAlwaysFalse(predicate)) {
                return new Synchronized<>(0, super.comparer);
            } else {
                synchronized (lock)
                {
                    Synchronized<T> ret_list = new Synchronized<>(super.count, super.comparer);

                    try (IEnumerator<T> en = GetEnumerator())
                    {
                        T item;
                        while (en.MoveNext())
                        {
                            if (predicate.predicate(item = en.getCurrent())) { ret_list.Add(item); }
                        }
                    }

                    return ret_list;
                }
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <TO> Synchronized<TO> ConvertAll(Converter<T, TO> converter, IEqualityComparer<TO> comparer)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(converter, "converter");
            synchronized (lock)
            {
                Synchronized<TO> ret = new Synchronized<>(super.count, comparer);
                int bound = super.index + super.count;
                for (int I = super.index, J = ((FixedArrayBasedList<T>)ret).index; I < bound; I++, J++)
                {
                    ((FixedArrayBasedList<T>)ret).array[J] = converter.convert((T)super.array[I]);
                }
                ((FixedArrayBasedList<T>)ret).count = super.count;
                return ret;
            }
        }

        @Override
        public Synchronized<T> Clone()
        {
            synchronized (lock)
            {
                Synchronized<T> new_list = new Synchronized<>(super.count, super.comparer);
                System.arraycopy(super.array, super.index, ((FixedArrayBasedList<T>)new_list).array, ((FixedArrayBasedList<T>)new_list).index, super.count);
                ((FixedArrayBasedList<T>)new_list).count = super.count;
                return new_list;
            }
        }
    }

    private record IndexOfPredicate<T>(IEqualityComparer<T> eqc, T item)
            implements Predicate<Object>
    {
        @Override
        @SuppressWarnings("unchecked")
        public boolean predicate(Object obj) { return eqc.Equals(item, (T)obj); }
    }

    @Override
    public void Clear() { count = 0; }

    @Override
    public int GetCount() { return count; }

    @Override
    public int getCount() { return count; }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    public boolean Contains(T item) { return IndexOf(item) > -1; }

    @Override
    public T GetItem(int index) throws ArgumentOutOfRangeException { return getItem(index); }

    @Override
    public IEnumerator<T> GetEnumerator() { return ArrayEnumerator.ByBoundsCasted(array, index, count); }

    @Override
    public int IndexOf(T item) { return Array.FindIndex(array, index, count, new IndexOfPredicate<>(comparer, item)); }

    @Override
    @MaybeNull
    @SuppressWarnings("unchecked")
    public T getItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            return (T) array[this.index + index];
        }
    }

    @Override
    public void setItem(int index, @AllowNull T item)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            array[this.index + index] = item;
        }
    }

    @Override
    public boolean Remove(T item)
    {
        int I = IndexOf(item);
        if (I > -1) {
            RemoveAt(I);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void Insert(int index, T item)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index > count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else if (index == count) {
            Add(item);
        } else if (Math.addExact(this.index, count) < array.length) {
            // Shift all elements by 1 index value.
            int index_stop = this.index + index;
            for (int I = (this.index + count) - 1; I >= index_stop; I--) { this.array[I+1] = this.array[I]; }
            this.array[index] = item;
            count++;
        } else {
            throw new OverflowException("The list has reached it's maximum capacity.");
        }
    }

    @Override
    public void RemoveAt(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            int ct_total = (this.index + count) - 1;
            for (int I = this.index + index; I < ct_total; I++) { this.array[I] = this.array[I+1]; }
            count--;
        }
    }

    @Override
    public void Add(T item)
    {
        if (Math.addExact(this.index, count) < array.length) {
            array[count++] = item;
        } else {
            throw new OverflowException("The list has reached it's maximum capacity.");
        }
    }

    @Override
    public void CopyTo(T[] array, int arrayIndex)
            throws ArgumentOutOfRangeException
    {
        try {
            Array.Copy(this.array, index, array, arrayIndex, count);
        } catch (IndexOutOfRangeException e) {
            throw new ArgumentOutOfRangeException("arrayIndex", "Not enough space to store all the list's elements to the specified array.");
        }
    }

    @NotNull
    @Override
    public FixedArrayBasedList<T> Slice(int count)
            throws ArgumentException
    {
        if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (count > this.count) {
            throw new ArgumentException("The specified combination of index and count parameters exceed the list's bounds.");
        } else {
            return new FixedArrayBasedList<>(
                    this.array,
                    this.index,
                    count,
                    this.comparer
            );
        }
    }

    @NotNull
    @Override
    public FixedArrayBasedList<T> Slice(int index, int count)
            throws ArgumentException
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
                return new FixedArrayBasedList<>(
                        this.array,
                        this.index + index,
                        count,
                        this.comparer
                );
            }
        }
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <TO> FixedArrayBasedList<TO> ConvertAll(Converter<T, TO> converter, IEqualityComparer<TO> comparer)
            throws ArgumentNullException
    {
        FixedArrayBasedList<TO> ret_list = new FixedArrayBasedList<>(count, comparer);
        int bound = this.index + count;
        for (int I = this.index, J = ret_list.index; I < bound; I++, J++)
        {
            ret_list.array[J] = converter.convert((T)this.array[I]);
        }
        ret_list.count = count;
        return ret_list;
    }

    @NotNull
    @Override
    public FixedArrayBasedList<T> FilterBy(Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return this;
        } else if (CollectionManipulations.IsEmpty(this) || FunctionManipulations.IsAlwaysFalse(predicate)) {
            return new FixedArrayBasedList<>(0, comparer);
        } else {
            FixedArrayBasedList<T> ret_list = new FixedArrayBasedList<>(count, comparer);

            try (IEnumerator<T> en = GetEnumerator())
            {
                T item;
                while (en.MoveNext())
                {
                    if (predicate.predicate(item = en.getCurrent())) { ret_list.Add(item); }
                }
            }

            return ret_list;
        }
    }

    @NotNull
    @Override
    public FixedArrayBasedList<T> Clone()
    {
        FixedArrayBasedList<T> new_list = new FixedArrayBasedList<>(this.count, comparer);
        System.arraycopy(this.array, this.index, new_list.array, new_list.index, this.count);
        new_list.count = this.count;
        return new_list;
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
        StringBuilder sb = new StringBuilder(
                String.format("FixedArrayBasedList<?> (%d, %d) { ", array.length, count)
        );
        switch (count)
        {
            case 0:
                sb.append("<EMPTY>");
                break;
            case 1:
                sb.append(array[this.index]);
                break;
            default:
                int bound = (this.index + this.count) - 1;
                for (int I = this.index; I < bound; I++) { sb.append(array[I]).append(", "); }
                sb.append(array[bound]);
                break;
        }
        return sb.append(" }").toString();
    }
}
