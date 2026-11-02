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
 * Provides a {@link BasePrimitiveList} for {@link Integer} values, implements {@link IList} and {@link IIntEnumerable}.
 * @since 1.0.40
 */
public class IntList
        extends BasePrimitiveList
        implements IList<Integer>,
        ITraversableCollection<Integer>,
        IIntEnumerable
{
    private int[] array;

    /**
     * Initializes a new instance of the {@link IntList} class.
     */
    public IntList() { array = new int[16]; }

    /**
     * Initializes a new instance of the {@link IntList} class. <br />
     * The specified array is owned by the newly created object.
     * @param array The array to provide.
     */
    public IntList(@AllowNull int[] array) { this.array = (array == null) ? new int[16] : array; }

    /**
     * Initializes a new instance of the {@link IntList} class. <br />
     * The specified {@code capacity} value specifies the initial number of
     * elements that will be able to be contained without the backing array
     * to be resized.
     * @param capacity The initial number of elements that are expected to be contained in the list.
     * @throws ArgumentOutOfRangeException {@code capacity} is a negative value.
     */
    public IntList(int capacity)
            throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative value.");
        } else {
            array = new int[capacity];
        }
    }

    /**
     * Initializes a new instance of the {@link IntList} class. <br />
     * The specified {@code enumerable} instance contains the elements to
     * copy to this {@link IntList} instance.
     * @param enumerable The {@link IIntEnumerable} to copy its elements to the newly created instance.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    public IntList(IIntEnumerable enumerable)
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
     * Creates a new instance of the {@link IntList} class. <br />
     * The returned object is synchronized.
     */
    @NotNull
    public static IntList CreateSynchronized() { return new Synchronized(); }

    /**
     * Creates a new instance of the {@link IntList} class. <br />
     * The returned object is synchronized. <br />
     * The specified array is owned by the newly created object.
     * @param array The array to provide.
     */
    @NotNull
    public static IntList CreateSynchronized(@AllowNull int[] array) { return new Synchronized(array); }

    /**
     * Creates a new instance of the {@link IntList} class. <br />
     * The returned object is synchronized. <br />
     * The specified {@code capacity} value specifies the initial number of
     * elements that will be able to be contained without the backing array
     * to be resized.
     * @param capacity The initial number of elements that are expected to be contained in the list.
     * @throws ArgumentOutOfRangeException {@code capacity} is a negative value.
     */
    @NotNull
    public static IntList CreateSynchronized(int capacity) throws ArgumentOutOfRangeException { return new Synchronized(capacity); }

    /**
     * Creates a new instance of the {@link IntList} class. <br />
     * The returned object is synchronized. <br />
     * The specified {@code enumerable} instance contains the elements to
     * copy to this {@link IntList} instance.
     * @param enumerable The {@link IIntEnumerable} to copy its elements to the newly created instance.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    @NotNull
    public static IntList CreateSynchronized(IIntEnumerable enumerable) throws ArgumentNullException { return new Synchronized(enumerable); }

    private static final class Synchronized
            extends IntList
            implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        public Synchronized(int[] array) { super(array); this.lock = new Object(); }

        public Synchronized(int capacity) { super(capacity); this.lock = new Object(); }

        public Synchronized(IIntEnumerable enumerable) { super(enumerable); this.lock = new Object(); }

        @Override
        public Object getSyncRoot() { return lock; }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public boolean getIsSynchronized() { return true; }

        @Override
        public void Clear() { synchronized (lock) { super.Clear(); } }

        @Override
        public void Add(int item) { synchronized (lock) { super.Add(item); } }

        @Override
        public void Add(Integer item) { synchronized (lock) { super.Add(item); } }

        @Override
        public void TrimExcess() { synchronized (lock) { super.TrimExcess(); } }

        @Override
        public void RemoveAt(int index) { synchronized (lock) { super.RemoveAt(index); } }

        @Override
        public int IndexOf(Integer item) { synchronized (lock) { return super.IndexOf(item); } }

        @Override
        public Integer GetItem(int index) { synchronized (lock) { return super.getItem(index); } }

        @Override
        public boolean Remove(Integer item) { synchronized (lock) { return super.Remove(item); } }

        @Override
        public boolean Remove(int item) { synchronized (lock) { return super.Remove(item); } }

        @Override
        public Integer getItem(int index) { synchronized (lock) { return super.getItem(index); } }

        @Override
        public boolean Contains(Integer item) { synchronized (lock) { return super.Contains(item); } }

        @Override
        public boolean Contains(int item) { synchronized (lock) { return super.Contains(item); } }

        @Override
        public void Insert(int index, Integer item) { synchronized (lock) { super.Insert(index, item); } }

        @Override
        public void Insert(int index, int item) { synchronized (lock) { super.Insert(index, item); } }

        @Override
        public void setItem(int index, Integer item) { synchronized (lock) { super.setItem(index, item); } }

        @Override
        public void EnsureCapacity(int n_elements) { synchronized (lock) { super.EnsureCapacity(n_elements); } }

        @Override
        public void CopyTo(Object array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public void CopyTo(Integer[] array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public void CopyTo(int[] array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }
    }

    @Override
    public int getCount() { return GetCount(); }

    public void Add(int item) { AddItem(item); }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    protected Object GetArrayReference() { return array; }

    @Override
    public int IndexOf(Integer item) { return IndexOfItem(item); }

    @Override
    public boolean Remove(Integer item) { return RemoveItem(item); }

    public boolean Remove(int item) { return RemoveItem(item); }

    @Override
    public boolean Contains(Integer item) { return IndexOfItem(item) > -1; }

    public boolean Contains(int item) { return IndexOfItem(item) > -1; }

    @Override
    public void Insert(int index, Integer item) { InsertItem(index, item); }

    public void Insert(int index, int item) { InsertItem(index, item); }

    @Override
    protected void SetArrayReference(Object array) { this.array = (int[]) array; }

    @Override
    public void CopyTo(Integer[] array, int arrayIndex) { CopyTo((Object)array, arrayIndex); }

    public void CopyTo(int[] array, int arrayIndex) { CopyTo((Object)array, arrayIndex); }

    @Override
    public IIntEnumerator GetEnumerator() { return new IntArrayEnumerator(array, 0, GetCount()); }

    @NotNull
    @Override
    public Integer getItem(int index)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else {
            CollectionHelpers.CheckIndexCountInsideCollectionBound(index, 1, GetCount());
            return array[index];
        }
    }

    @Override
    public void setItem(int index, @DisallowNull Integer item)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else {
            CollectionHelpers.CheckIndexCountInsideCollectionBound(index, 1, GetCount());
            array[index] = item;
        }
    }

    @Override
    public void Add(Integer item)
    {
        ArgumentNullException.ThrowIfNull(item, "item");
        AddItem(item);
    }

    @Override
    public Integer GetItem(int index)
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
