package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.utils.JavaObjectEqualsEqualityComparer;

/**
 * Provides a default implementation of the {@link ITraversableRegister} interface. <br />
 * Note down that this register implementation cannot enforce object singularity.
 * @param <T> The type of items that this register will retain.
 * @since 1.0.22
 */
public class ArrayBasedRegister<T>
    implements ITraversableRegister<T>, IArrayBasedCollection, ISynchronized
{
    private int count;
    private Object[] elements;
    private final IEqualityComparer<T> comparer;

    /**
     * Initializes a new and empty instance of the {@link ArrayBasedRegister} class.
     */
    public ArrayBasedRegister()
    {
        super();
        comparer = new JavaObjectEqualsEqualityComparer<>();
        elements = new Object[0];
        count = 0;
    }

    /**
     * Initializes a new and empty instance of the {@link ArrayBasedRegister} class with the specified initial capacity.
     * @param capacity The initial capacity of the register.
     * @throws ArgumentOutOfRangeException {@code capacity} is negative.
     */
    public ArrayBasedRegister(int capacity)
        throws ArgumentOutOfRangeException
    {
        super();
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity" , "Initial collection capacity cannot be negative.");
        } else {
            comparer = new JavaObjectEqualsEqualityComparer<>();
            elements = new Object[capacity];
            count = 0;
        }
    }

    /**
     * Initializes a new and empty instance of the {@link ArrayBasedRegister} class, by using the specified equality comparer for comparing registered elements.
     * @param comparer The equality comparer to be used. Can also be {@code null}.
     */
    public ArrayBasedRegister(@AllowNull IEqualityComparer<T> comparer)
    {
        this.comparer = (comparer == null) ? new JavaObjectEqualsEqualityComparer<>() : comparer;
        elements = new Object[0];
        count = 0;
    }

    /**
     * Initializes a new and empty instance of the {@link ArrayBasedRegister} class, by using the specified equality comparer for comparing registered elements and with the specified initial capacity.
     * @param capacity The initial capacity of the register.
     * @param comparer The equality comparer to be used. Can also be {@code null}.
     * @throws ArgumentOutOfRangeException {@code capacity} is negative.
     */
    public ArrayBasedRegister(int capacity, @AllowNull IEqualityComparer<T> comparer)
        throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Initial collection capacity cannot be negative.");
        } else {
            this.comparer = (comparer == null) ? new JavaObjectEqualsEqualityComparer<>() : comparer;
            elements = new Object[capacity];
            count = 0;
        }
    }

    /**
     * Initializes a new instance of the {@link ArrayBasedRegister} class containing the specified items from the specified enumerable of items.
     * @param items The items to be added to this register instance.
     * @throws ArgumentNullException {@code items} is {@code null}.
     */
    public ArrayBasedRegister(IEnumerable<T> items)
            throws ArgumentNullException
    {
        this();
        RegisterRange(items);
    }

    /**
     * Initializes a new instance of the {@link ArrayBasedRegister} class containing the specified items from the specified enumerable of items.
     * @param items The items to be added to this register instance.
     * @param comparer The equality comparer to be used. Can also be {@code null}.
     */
    public ArrayBasedRegister(IEnumerable<T> items, @AllowNull IEqualityComparer<T> comparer)
    {
        this(comparer);
        RegisterRange(items);
    }

    private void Grow(int by)
    {
        int new_count = count + by;
        if (new_count < 0) {
            // Overflow detected, throw
            throw new OverflowException("The register has reached it's maximum capacity.");
        } else if (new_count > elements.length) {
            Object[] new_elements = new Object[new_count];
            System.arraycopy(elements, 0, new_elements, 0, elements.length);
            elements = new_elements;
        }
    }

    private void RegisterUnchecked(@AllowNull T item) { elements[count++] = item; }

    private record IndexOfPredicate<T>(IEqualityComparer<T> eqc, T item)
            implements Predicate<Object>
    {
        @Override
        public boolean predicate(Object obj) { return eqc.Equals((T)obj, item); }
    }

    private void RegisterRange_List(IList<T> list)
    {
        int c = list.getCount();
        Grow(c);
        for (int I = 0; I < c; I++) { elements[count + I] = list.getItem(I); }
        count += c;
    }

    private void RegisterRange_Register(ArrayBasedRegister<T> reg)
    {
        int c = reg.count;
        Grow(c);
        Array.Copy(reg.elements, 0, elements, count, c);
        count += c;
    }

    @Override
    public int GetCount() { return count; }

    @Override
    public IEnumerator<T> GetEnumerator() { return ArrayEnumerator.ByBounds((T[])elements, 0, count); }

    @Override
    public int IndexOf(T item) { return Array.FindIndex(elements,0 , count, new IndexOfPredicate<>(comparer, item)); }

    @Override
    public void Register(T item)
            throws ArgumentException
    {
        Grow(1);
        RegisterUnchecked(item);
    }

    @Override
    public void RegisterRange(IEnumerable<T> items)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        if (items instanceof IList<T> list) {
            RegisterRange_List(list);
        } else if (items instanceof ArrayBasedRegister<T> reg) {
            RegisterRange_Register(reg);
        } else {
            boolean has_fast_path = true;
            if (items instanceof ICollection<T> c) {
                Grow(c.getCount());
            } else {
                has_fast_path = false;
            }
            IEnumerator<T> enumerator = items.GetEnumerator();
            try {
                if (has_fast_path) {
                    while (enumerator.MoveNext()) { RegisterUnchecked(enumerator.getCurrent()); }
                } else {
                    while (enumerator.MoveNext()) { Register(enumerator.getCurrent()); }
                }
            } finally {
                enumerator.Dispose();
            }
        }
    }

    @Override
    public T GetItem(int index)
            throws ArgumentOutOfRangeException
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

    @Override
    public void EnsureCapacity(int n_elements)
            throws OverflowException, ArgumentOutOfRangeException
    {
        if (n_elements < 0) {
            throw new ArgumentOutOfRangeException("n_elements", "Number of elements to be ensured of should not be negative.");
        } else {
            Grow(n_elements);
        }
    }
}
