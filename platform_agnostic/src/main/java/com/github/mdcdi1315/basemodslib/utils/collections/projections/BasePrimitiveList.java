package com.github.mdcdi1315.basemodslib.utils.collections.projections;

import com.github.mdcdi1315.DotNetLayer.System.OverflowException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.collections.IArrayBasedCollection;
import com.github.mdcdi1315.basemodslib.utils.collections.ICountableCollectionBase;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.CollectionHelpers;

import java.lang.reflect.Array;

/**
 * Provides the base type for all the primitive-type lists based on arrays.
 * @since 1.0.40
 */
public abstract class BasePrimitiveList
    implements IArrayBasedCollection, ICollection, ICountableCollectionBase
{
    private int count;

    /**
     * Initializes a new instance of the {@link BasePrimitiveList} class.
     */
    protected BasePrimitiveList() { count = 0; }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    private void Enlarge(int new_capacity)
    {
        Object old_array = GetArrayReference();
        Object new_array = Array.newInstance(old_array.getClass().componentType(), new_capacity);
        System.arraycopy(old_array, 0, new_array, 0, count);
        SetArrayReference(new_array);
    }

    private static final class UnknownArrayEnumerator
            implements IEnumerator
    {
        private int index;
        private final int count;
        private final Object array;

        public UnknownArrayEnumerator(Object array, int count)
        {
            this.index = -1;
            this.array = array;
            this.count = count;
        }

        @Override
        public void Reset() { index = -1; }

        @Override
        public boolean MoveNext() { return ++index < count; }

        @Override
        public Object getCurrent() { return Array.get(array, index); }
    }

    /**
     * Gets the held reference to the backing array.
     * @return The reference to the backing array.
     */
    @NotNull
    protected abstract Object GetArrayReference();

    /**
     * Sets the held reference to the backing array.
     * @param array The new array object.
     */
    protected abstract void SetArrayReference(@DisallowNull Object array);

    /**
     * MUST be invoked when a new item has been added to the collection.
     */
    protected final void OnItemAdded() { count++; }

    /**
     * MUST be invoked when an item has been removed from the collection.
     */
    protected final void OnItemRemoved() { count--; }

    protected final void AddItem(Object item) { EnsureCapacity(1); Array.set(GetArrayReference(), count++, item); }

    protected final int IndexOfItem(Object item)
    {
        Object array = GetArrayReference();
        for (int I = 0; I < count; I++)
        {
            if (Array.get(array, I).equals(item)) { return I; }
        }
        return -1;
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    protected final void InsertItem(int index, Object item)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index > count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            Object array = GetArrayReference();
            EnsureCapacity(1);
            int n_elements_to_copy = count - index - 1;
            if (n_elements_to_copy > 0) {
                System.arraycopy(array, index, array, index+1, n_elements_to_copy);
            }
            Array.set(array, index, item);
        }
    }

    protected final boolean RemoveItem(Object item)
    {
        int index = IndexOfItem(item);
        if (index > -1) {
            RemoveAt(index);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object getSyncRoot() { return this; }

    @Override
    public boolean getIsSynchronized() { return false; }

    /**
     * Gets the number of elements contained in this {@link BasePrimitiveList}.
     * @return The number of contained elements.
     */
    @Pure
    public final int GetCount() { return count; }

    /**
     * Removes all the added items from this {@link BasePrimitiveList}.
     */
    public void Clear() { count = 0; }

    /**
     * Removes the {@link BasePrimitiveList} item at the specified index.
     * @param index The zero-based index of the item to remove.
     * @exception com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException index is not a valid index in the {@link BasePrimitiveList}.
     * @exception com.github.mdcdi1315.DotNetLayer.System.NotSupportedException The {@link BasePrimitiveList} is read-only.
     */
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public void RemoveAt(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            int n_elements_to_copy = count - index - 1;
            if (n_elements_to_copy > 0)
            {
                Object array_reference = GetArrayReference();
                System.arraycopy(array_reference, index+1, array_reference, index, n_elements_to_copy);
            }
            OnItemRemoved();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public void CopyTo(Object array, int arrayIndex)
            throws ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (arrayIndex < 0) {
            throw new ArgumentOutOfRangeException("arrayIndex", "Index cannot be a negative value.");
        } else {
            Object source = GetArrayReference();
            CollectionHelpers.CheckCopyToArguments(arrayIndex, Array.getLength(source), count);
            System.arraycopy(source, 0, array, arrayIndex, count);
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    public IEnumerator GetEnumerator() { return new UnknownArrayEnumerator(GetArrayReference(), count); }

    /**
     * {@inheritDoc}
     */
    @Override
    public void EnsureCapacity(int n_elements)
            throws OverflowException, ArgumentOutOfRangeException
    {
        if (n_elements < 0) {
            throw new ArgumentOutOfRangeException("n_elements", "Number of elements cannot be a negative value");
        } else {
            int new_count = count + n_elements;
            if (new_count < 0) {
                // Overflow detected, throw
                throw new OverflowException("The list has reached it's maximum capacity.");
            } else if (new_count > Array.getLength(GetArrayReference())) {
                Enlarge(new_count);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public void TrimExcess()
    {
        Object array = GetArrayReference();
        if (count > 0 && Array.getLength(array) != count)
        {
            Object new_array = Array.newInstance(array.getClass().componentType(), count);
            System.arraycopy(array, 0, new_array, 0, count);
            SetArrayReference(new_array);
        }
    }
}
