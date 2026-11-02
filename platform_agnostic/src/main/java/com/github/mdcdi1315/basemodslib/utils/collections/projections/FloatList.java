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
 * Provides a {@link BasePrimitiveList} for {@link Float} values, implements {@link IList} and {@link IFloatEnumerable}.
 * @since 1.0.40
 */
public class FloatList
        extends BasePrimitiveList
        implements IList<Float>,
        ITraversableCollection<Float>,
        IFloatEnumerable
{
    private float[] array;

    /**
     * Initializes a new instance of the {@link FloatList} class.
     */
    public FloatList() { array = new float[16]; }

    /**
     * Initializes a new instance of the {@link FloatList} class. <br />
     * The specified array is owned by the newly created object.
     * @param array The array to provide.
     */
    public FloatList(@AllowNull float[] array) { this.array = (array == null) ? new float[16] : array; }

    /**
     * Initializes a new instance of the {@link FloatList} class. <br />
     * The specified {@code capacity} value specifies the initial number of
     * elements that will be able to be contained without the backing array
     * to be resized.
     * @param capacity The initial number of elements that are expected to be contained in the list.
     * @throws ArgumentOutOfRangeException {@code capacity} is a negative value.
     */
    public FloatList(int capacity)
            throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative value.");
        } else {
            array = new float[capacity];
        }
    }

    /**
     * Initializes a new instance of the {@link FloatList} class. <br />
     * The specified {@code enumerable} instance contains the elements to
     * copy to this {@link FloatList} instance.
     * @param enumerable The {@link IFloatEnumerable} to copy its elements to the newly created instance.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    public FloatList(IFloatEnumerable enumerable)
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
     * Creates a new instance of the {@link FloatList} class. <br />
     * The returned object is synchronized.
     */
    @NotNull
    public static FloatList CreateSynchronized() { return new Synchronized(); }

    /**
     * Creates a new instance of the {@link FloatList} class. <br />
     * The returned object is synchronized. <br />
     * The specified array is owned by the newly created object.
     * @param array The array to provide.
     */
    @NotNull
    public static FloatList CreateSynchronized(@AllowNull float[] array) { return new Synchronized(array); }

    /**
     * Creates a new instance of the {@link FloatList} class. <br />
     * The returned object is synchronized. <br />
     * The specified {@code capacity} value specifies the initial number of
     * elements that will be able to be contained without the backing array
     * to be resized.
     * @param capacity The initial number of elements that are expected to be contained in the list.
     * @throws ArgumentOutOfRangeException {@code capacity} is a negative value.
     */
    @NotNull
    public static FloatList CreateSynchronized(int capacity) throws ArgumentOutOfRangeException { return new Synchronized(capacity); }

    /**
     * Creates a new instance of the {@link FloatList} class. <br />
     * The returned object is synchronized. <br />
     * The specified {@code enumerable} instance contains the elements to
     * copy to this {@link FloatList} instance.
     * @param enumerable The {@link IFloatEnumerable} to copy its elements to the newly created instance.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    @NotNull
    public static FloatList CreateSynchronized(IFloatEnumerable enumerable) throws ArgumentNullException { return new Synchronized(enumerable); }

    private static final class Synchronized
            extends FloatList
            implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        public Synchronized(float[] array) { super(array); this.lock = new Object(); }

        public Synchronized(int capacity) { super(capacity); this.lock = new Object(); }

        public Synchronized(IFloatEnumerable enumerable) { super(enumerable); this.lock = new Object(); }

        @Override
        public Object getSyncRoot() { return lock; }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public boolean getIsSynchronized() { return true; }

        @Override
        public void Clear() { synchronized (lock) { super.Clear(); } }

        @Override
        public void Add(float item) { synchronized (lock) { super.Add(item); } }

        @Override
        public void Add(Float item) { synchronized (lock) { super.Add(item); } }

        @Override
        public void TrimExcess() { synchronized (lock) { super.TrimExcess(); } }

        @Override
        public void RemoveAt(int index) { synchronized (lock) { super.RemoveAt(index); } }

        @Override
        public int IndexOf(Float item) { synchronized (lock) { return super.IndexOf(item); } }

        @Override
        public Float GetItem(int index) { synchronized (lock) { return super.getItem(index); } }

        @Override
        public boolean Remove(Float item) { synchronized (lock) { return super.Remove(item); } }

        @Override
        public boolean Remove(float item) { synchronized (lock) { return super.Remove(item); } }

        @Override
        public Float getItem(int index) { synchronized (lock) { return super.getItem(index); } }

        @Override
        public boolean Contains(Float item) { synchronized (lock) { return super.Contains(item); } }

        @Override
        public boolean Contains(float item) { synchronized (lock) { return super.Contains(item); } }

        @Override
        public void Insert(int index, Float item) { synchronized (lock) { super.Insert(index, item); } }

        @Override
        public void Insert(int index, float item) { synchronized (lock) { super.Insert(index, item); } }

        @Override
        public void setItem(int index, Float item) { synchronized (lock) { super.setItem(index, item); } }

        @Override
        public void EnsureCapacity(int n_elements) { synchronized (lock) { super.EnsureCapacity(n_elements); } }

        @Override
        public void CopyTo(Object array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public void CopyTo(Float[] array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public void CopyTo(float[] array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }
    }

    @Override
    public int getCount() { return GetCount(); }

    public void Add(float item) { AddItem(item); }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    protected Object GetArrayReference() { return array; }

    @Override
    public int IndexOf(Float item) { return IndexOfItem(item); }

    @Override
    public boolean Remove(Float item) { return RemoveItem(item); }

    public boolean Remove(float item) { return RemoveItem(item); }

    @Override
    public boolean Contains(Float item) { return IndexOfItem(item) > -1; }

    public boolean Contains(float item) { return IndexOfItem(item) > -1; }

    @Override
    public void Insert(int index, Float item) { InsertItem(index, item); }

    public void Insert(int index, float item) { InsertItem(index, item); }

    @Override
    protected void SetArrayReference(Object array) { this.array = (float[]) array; }

    @Override
    public void CopyTo(Float[] array, int arrayIndex) { CopyTo((Object)array, arrayIndex); }

    public void CopyTo(float[] array, int arrayIndex) { CopyTo((Object)array, arrayIndex); }

    @Override
    public IFloatEnumerator GetEnumerator() { return new FloatArrayEnumerator(array, 0, GetCount()); }

    @NotNull
    @Override
    public Float getItem(int index)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else {
            CollectionHelpers.CheckIndexCountInsideCollectionBound(index, 1, GetCount());
            return array[index];
        }
    }

    @Override
    public void setItem(int index, @DisallowNull Float item)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else {
            CollectionHelpers.CheckIndexCountInsideCollectionBound(index, 1, GetCount());
            array[index] = item;
        }
    }

    @Override
    public void Add(Float item)
    {
        ArgumentNullException.ThrowIfNull(item, "item");
        AddItem(item);
    }

    @Override
    public Float GetItem(int index)
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
