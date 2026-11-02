package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;
import com.github.mdcdi1315.basemodslib.utils.collections.ICountableCollectionBase;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.CollectionHelpers;

/**
 * Provides a {@link BasePrimitiveList} for {@link Double} values, implements {@link IList} and {@link IDoubleEnumerable}.
 * @since 1.0.40
 */
public class DoubleList
        extends BasePrimitiveList
        implements IList<Double>,
        ITraversableCollection<Double>,
        IDoubleEnumerable
{
    private double[] array;

    /**
     * Initializes a new instance of the {@link DoubleList} class.
     */
    public DoubleList() { array = new double[16]; }

    /**
     * Initializes a new instance of the {@link DoubleList} class. <br />
     * The specified array is owned by the newly created object.
     * @param array The array to provide.
     */
    public DoubleList(@AllowNull double[] array) { this.array = (array == null) ? new double[16] : array; }

    /**
     * Initializes a new instance of the {@link DoubleList} class. <br />
     * The specified {@code capacity} value specifies the initial number of
     * elements that will be able to be contained without the backing array
     * to be resized.
     * @param capacity The initial number of elements that are expected to be contained in the list.
     * @throws ArgumentOutOfRangeException {@code capacity} is a negative value.
     */
    public DoubleList(int capacity)
            throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative value.");
        } else {
            array = new double[capacity];
        }
    }

    /**
     * Initializes a new instance of the {@link DoubleList} class. <br />
     * The specified {@code enumerable} instance contains the elements to
     * copy to this {@link DoubleList} instance.
     * @param enumerable The {@link IDoubleEnumerable} to copy its elements to the newly created instance.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    public DoubleList(IDoubleEnumerable enumerable)
            throws ArgumentNullException
    {
        this(16);
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        int count = 0;
        if (enumerable instanceof ICountableCollectionBase b) {
            count = b.GetCount();
        } else if (enumerable instanceof ICollection c) {
            count = c.getCount();
        }
        EnsureCapacity(count);
        try (var en = enumerable.GetEnumerator()) { AddItem(en.getCurrent()); }
    }

    /**
     * Creates a new instance of the {@link DoubleList} class. <br />
     * The returned object is synchronized.
     */
    @NotNull
    public static DoubleList CreateSynchronized() { return new Synchronized(); }

    /**
     * Creates a new instance of the {@link DoubleList} class. <br />
     * The returned object is synchronized. <br />
     * The specified array is owned by the newly created object.
     * @param array The array to provide.
     */
    @NotNull
    public static DoubleList CreateSynchronized(@AllowNull double[] array) { return new Synchronized(array); }

    /**
     * Creates a new instance of the {@link DoubleList} class. <br />
     * The returned object is synchronized. <br />
     * The specified {@code capacity} value specifies the initial number of
     * elements that will be able to be contained without the backing array
     * to be resized.
     * @param capacity The initial number of elements that are expected to be contained in the list.
     * @throws ArgumentOutOfRangeException {@code capacity} is a negative value.
     */
    @NotNull
    public static DoubleList CreateSynchronized(int capacity) throws ArgumentOutOfRangeException { return new Synchronized(capacity); }

    /**
     * Creates a new instance of the {@link DoubleList} class. <br />
     * The returned object is synchronized. <br />
     * The specified {@code enumerable} instance contains the elements to
     * copy to this {@link DoubleList} instance.
     * @param enumerable The {@link IDoubleEnumerable} to copy its elements to the newly created instance.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    @NotNull
    public static DoubleList CreateSynchronized(IDoubleEnumerable enumerable) throws ArgumentNullException { return new Synchronized(enumerable); }

    private static final class Synchronized
            extends DoubleList
            implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        public Synchronized(double[] array) { super(array); this.lock = new Object(); }

        public Synchronized(int capacity) { super(capacity); this.lock = new Object(); }

        public Synchronized(IDoubleEnumerable enumerable) { super(enumerable); this.lock = new Object(); }

        @Override
        public Object getSyncRoot() { return lock; }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public boolean getIsSynchronized() { return true; }

        @Override
        public void Clear() { synchronized (lock) { super.Clear(); } }

        @Override
        public void Add(double item) { synchronized (lock) { super.Add(item); } }

        @Override
        public void Add(Double item) { synchronized (lock) { super.Add(item); } }

        @Override
        public void TrimExcess() { synchronized (lock) { super.TrimExcess(); } }

        @Override
        public void RemoveAt(int index) { synchronized (lock) { super.RemoveAt(index); } }

        @Override
        public int IndexOf(Double item) { synchronized (lock) { return super.IndexOf(item); } }

        @Override
        public Double GetItem(int index) { synchronized (lock) { return super.getItem(index); } }

        @Override
        public boolean Remove(Double item) { synchronized (lock) { return super.Remove(item); } }

        @Override
        public boolean Remove(double item) { synchronized (lock) { return super.Remove(item); } }

        @Override
        public Double getItem(int index) { synchronized (lock) { return super.getItem(index); } }

        @Override
        public boolean Contains(Double item) { synchronized (lock) { return super.Contains(item); } }

        @Override
        public boolean Contains(double item) { synchronized (lock) { return super.Contains(item); } }

        @Override
        public void Insert(int index, Double item) { synchronized (lock) { super.Insert(index, item); } }

        @Override
        public void Insert(int index, double item) { synchronized (lock) { super.Insert(index, item); } }

        @Override
        public void setItem(int index, Double item) { synchronized (lock) { super.setItem(index, item); } }

        @Override
        public void EnsureCapacity(int n_elements) { synchronized (lock) { super.EnsureCapacity(n_elements); } }

        @Override
        public void CopyTo(Object array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public void CopyTo(Double[] array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public void CopyTo(double[] array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }
    }

    @Override
    public int getCount() { return GetCount(); }

    public void Add(double item) { AddItem(item); }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    protected Object GetArrayReference() { return array; }

    @Override
    public int IndexOf(Double item) { return IndexOfItem(item); }

    @Override
    public boolean Remove(Double item) { return RemoveItem(item); }

    public boolean Remove(double item) { return RemoveItem(item); }

    @Override
    public boolean Contains(Double item) { return IndexOfItem(item) > -1; }

    public boolean Contains(double item) { return IndexOfItem(item) > -1; }

    @Override
    public void Insert(int index, Double item) { InsertItem(index, item); }

    public void Insert(int index, double item) { InsertItem(index, item); }

    @Override
    protected void SetArrayReference(Object array) { this.array = (double[]) array; }

    @Override
    public void CopyTo(Double[] array, int arrayIndex) { CopyTo((Object)array, arrayIndex); }

    public void CopyTo(double[] array, int arrayIndex) { CopyTo((Object)array, arrayIndex); }

    @Override
    public IDoubleEnumerator GetEnumerator() { return new DoubleArrayEnumerator(array, 0, GetCount()); }

    @NotNull
    @Override
    public Double getItem(int index)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else {
            CollectionHelpers.CheckIndexCountInsideCollectionBound(index, 1, GetCount());
            return array[index];
        }
    }

    @Override
    public void setItem(int index, @DisallowNull Double item)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else {
            CollectionHelpers.CheckIndexCountInsideCollectionBound(index, 1, GetCount());
            array[index] = item;
        }
    }

    @Override
    public void Add(Double item)
    {
        ArgumentNullException.ThrowIfNull(item, "item");
        AddItem(item);
    }

    @Override
    public Double GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else {
            CollectionHelpers.CheckIndexCountInsideCollectionBound(index, 1, GetCount());
            return array[index];
        }
    }
}