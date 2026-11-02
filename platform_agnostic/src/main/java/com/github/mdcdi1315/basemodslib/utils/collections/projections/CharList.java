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
 * Provides a {@link BasePrimitiveList} for {@link Character} values, implements {@link IList} and {@link ICharEnumerable}.
 * @since 1.0.40
 */
public class CharList
        extends BasePrimitiveList
        implements IList<Character>,
        ITraversableCollection<Character>,
        ICharEnumerable
{
    private char[] array;

    /**
     * Initializes a new instance of the {@link CharList} class.
     */
    public CharList() { array = new char[16]; }

    /**
     * Initializes a new instance of the {@link CharList} class. <br />
     * The specified array is owned by the newly created object.
     * @param array The array to provide.
     */
    public CharList(@AllowNull char[] array) { this.array = (array == null) ? new char[16] : array; }

    /**
     * Initializes a new instance of the {@link CharList} class. <br />
     * The specified {@code capacity} value specifies the initial number of
     * elements that will be able to be contained without the backing array
     * to be resized.
     * @param capacity The initial number of elements that are expected to be contained in the list.
     * @throws ArgumentOutOfRangeException {@code capacity} is a negative value.
     */
    public CharList(int capacity)
            throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative value.");
        } else {
            array = new char[capacity];
        }
    }

    /**
     * Initializes a new instance of the {@link CharList} class. <br />
     * The specified {@code enumerable} instance contains the elements to
     * copy to this {@link CharList} instance.
     * @param enumerable The {@link ICharEnumerable} to copy its elements to the newly created instance.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    public CharList(ICharEnumerable enumerable)
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
     * Creates a new instance of the {@link CharList} class. <br />
     * The returned object is synchronized.
     */
    @NotNull
    public static CharList CreateSynchronized() { return new Synchronized(); }

    /**
     * Creates a new instance of the {@link CharList} class. <br />
     * The returned object is synchronized. <br />
     * The specified array is owned by the newly created object.
     * @param array The array to provide.
     */
    @NotNull
    public static CharList CreateSynchronized(@AllowNull char[] array) { return new Synchronized(array); }

    /**
     * Creates a new instance of the {@link CharList} class. <br />
     * The returned object is synchronized. <br />
     * The specified {@code capacity} value specifies the initial number of
     * elements that will be able to be contained without the backing array
     * to be resized.
     * @param capacity The initial number of elements that are expected to be contained in the list.
     * @throws ArgumentOutOfRangeException {@code capacity} is a negative value.
     */
    @NotNull
    public static CharList CreateSynchronized(int capacity) throws ArgumentOutOfRangeException { return new Synchronized(capacity); }

    /**
     * Creates a new instance of the {@link CharList} class. <br />
     * The returned object is synchronized. <br />
     * The specified {@code enumerable} instance contains the elements to
     * copy to this {@link CharList} instance.
     * @param enumerable The {@link ICharEnumerable} to copy its elements to the newly created instance.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    @NotNull
    public static CharList CreateSynchronized(ICharEnumerable enumerable) throws ArgumentNullException { return new Synchronized(enumerable); }

    private static final class Synchronized
            extends CharList
            implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        public Synchronized(char[] array) { super(array); this.lock = new Object(); }

        public Synchronized(int capacity) { super(capacity); this.lock = new Object(); }

        public Synchronized(ICharEnumerable enumerable) { super(enumerable); this.lock = new Object(); }

        @Override
        public Object getSyncRoot() { return lock; }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public boolean getIsSynchronized() { return true; }

        @Override
        public void Clear() { synchronized (lock) { super.Clear(); } }

        @Override
        public void Add(char item) { synchronized (lock) { super.Add(item); } }

        @Override
        public void Add(Character item) { synchronized (lock) { super.Add(item); } }

        @Override
        public void TrimExcess() { synchronized (lock) { super.TrimExcess(); } }

        @Override
        public void RemoveAt(int index) { synchronized (lock) { super.RemoveAt(index); } }

        @Override
        public int IndexOf(Character item) { synchronized (lock) { return super.IndexOf(item); } }

        @Override
        public Character GetItem(int index) { synchronized (lock) { return super.getItem(index); } }

        @Override
        public boolean Remove(Character item) { synchronized (lock) { return super.Remove(item); } }

        @Override
        public boolean Remove(char item) { synchronized (lock) { return super.Remove(item); } }

        @Override
        public Character getItem(int index) { synchronized (lock) { return super.getItem(index); } }

        @Override
        public boolean Contains(Character item) { synchronized (lock) { return super.Contains(item); } }

        @Override
        public boolean Contains(char item) { synchronized (lock) { return super.Contains(item); } }

        @Override
        public void Insert(int index, Character item) { synchronized (lock) { super.Insert(index, item); } }

        @Override
        public void Insert(int index, char item) { synchronized (lock) { super.Insert(index, item); } }

        @Override
        public void setItem(int index, Character item) { synchronized (lock) { super.setItem(index, item); } }

        @Override
        public void EnsureCapacity(int n_elements) { synchronized (lock) { super.EnsureCapacity(n_elements); } }

        @Override
        public void CopyTo(Object array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public void CopyTo(Character[] array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public void CopyTo(char[] array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }
    }

    @Override
    public int getCount() { return GetCount(); }

    public void Add(char item) { AddItem(item); }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    protected Object GetArrayReference() { return array; }

    @Override
    public int IndexOf(Character item) { return IndexOfItem(item); }

    @Override
    public boolean Remove(Character item) { return RemoveItem(item); }

    public boolean Remove(char item) { return RemoveItem(item); }

    @Override
    public boolean Contains(Character item) { return IndexOfItem(item) > -1; }

    public boolean Contains(char item) { return IndexOfItem(item) > -1; }

    @Override
    public void Insert(int index, Character item) { InsertItem(index, item); }

    public void Insert(int index, char item) { InsertItem(index, item); }

    @Override
    protected void SetArrayReference(Object array) { this.array = (char[]) array; }

    @Override
    public void CopyTo(Character[] array, int arrayIndex) { CopyTo((Object)array, arrayIndex); }

    public void CopyTo(char[] array, int arrayIndex) { CopyTo((Object)array, arrayIndex); }

    @Override
    public ICharEnumerator GetEnumerator() { return new CharArrayEnumerator(array, 0, GetCount()); }

    @NotNull
    @Override
    public Character getItem(int index)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else {
            CollectionHelpers.CheckIndexCountInsideCollectionBound(index, 1, GetCount());
            return array[index];
        }
    }

    @Override
    public void setItem(int index, @DisallowNull Character item)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else {
            CollectionHelpers.CheckIndexCountInsideCollectionBound(index, 1, GetCount());
            array[index] = item;
        }
    }

    @Override
    public void Add(Character item)
    {
        ArgumentNullException.ThrowIfNull(item, "item");
        AddItem(item);
    }

    @Override
    public Character GetItem(int index)
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
