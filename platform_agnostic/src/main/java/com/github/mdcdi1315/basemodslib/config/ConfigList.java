package com.github.mdcdi1315.basemodslib.config;

import com.github.mdcdi1315.DotNetLayer.System.Array;
import com.github.mdcdi1315.DotNetLayer.System.OverflowException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;

/**
 * Defines the type for list fields in {@link IModConfig} scenarios. <br />
 * List fields cannot be directly created in an {@link IModConfig} instance; they MUST be wired through this instance. <br />
 * During reading the configuration and writing it, special codecs are overriding the de/serialization process in order to finally
 * provide the correct type of data. <br />
 * Finally, the items of this list can be all efficiently converted to an immutable list of the very specific type that is needed.
 * @since 1.0.15
 */
public final class ConfigList
    implements Iterable<Object>
{
    private int count;
    private Object[] items;

    /**
     * Constructs a new and empty instance of the {@link ConfigList} class, with an initial capacity of 10 elements.
     */
    public ConfigList() {
        count = 0;
        items = new Object[10];
    }

    public ConfigList(int initial_capacity)
        throws ArgumentOutOfRangeException
    {
        if (initial_capacity < 0) {
            throw new ArgumentOutOfRangeException("initial_capacity", "Initial capacity must not be a negative value.");
        } else {
            count = 0;
            items = new Object[initial_capacity];
        }
    }

    private void Grow(int by)
    {
        int new_count = count + by;
        if (new_count < 0) {
            throw new OverflowException("The list has reached it's maximum capacity.");
        } else if (new_count > items.length) {
            Object[] temp = new Object[new_count];
            Array.Copy(items, temp, count);
            items = temp;
        }
    }

    private static class IteratorImpl
        implements Iterator<Object>
    {
        private int index;
        private final int bound;
        private final Object[] items;

        public IteratorImpl(ConfigList this_inst)
        {
            bound = this_inst.count;
            items = this_inst.items;
            index = -1;
        }

        @Override
        public boolean hasNext() { return ++index < bound; }

        @Override
        public Object next() { return items[index]; }
    }

    /**
     * Gets the number of elements contained in the current list object.
     * @return The number of elements contained in this list.
     */
    public int GetCount() { return count; }

    /**
     * Adds an item to the list.
     * @param item The item to be added to the list.
     * @throws ArgumentNullException {@code item} is {@code null}.
     */
    public void Add(Object item)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(item, "item");
        Grow(1);
        items[count++] = item;
    }

    /**
     * Adds the specified item(s) to the list.
     * @param items The items to add to the list.
     */
    public void Add(Object... items)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        int len = items.length;
        Grow(len);
        Array.Copy(items, 0 , this.items, count, len);
        count += len;
    }

    /**
     * Removes an item from the list at {@code index}.
     * @param index The index where the item is located to and you wish to be removed.
     * @return A value whether removal was successful.
     */
    public boolean RemoveAt(int index)
    {
        if (index < 0 || index >= count) {
            return false;
        } else {
            int index_after = index + 1;
            Array.Copy(items, index_after, items, index, count - index_after);
            count--;
            return true;
        }
    }

    /**
     * Retrieves an item previously added to the list.
     * @param index The index of the item you wish to be retrieved.
     * @return The item at {@code index}.
     * @throws ArgumentOutOfRangeException {@code index} was out of the current list object bounds.
     */
    public Object Get(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0 || index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index was out of the list's bounds.\nActual value: " + index);
        } else {
            return items[index];
        }
    }

    /**
     * Optimizes the size of the current {@link ConfigList} object.
     */
    public void TrimToSize()
    {
        if (count == 0) {
            items = new Object[0];
        } else {
            Object[] temp = new Object[count];
            Array.Copy(items, temp, count);
            items = temp;
        }
    }

    /**
     * Converts this list object to an immutable list of type {@link T}, effectively taking a snapshot of this list.
     * @param casting_class The class object that is used to cast items to {@link T}.
     * @return An {@link ImmutableList} object describing the elements of the current list as type {@link T}.
     * @param <T> The type of the elements that the list will contain.
     * @throws ArgumentNullException {@code casting_class} is {@code null}.
     * @throws ClassCastException An item in the current list is not of type {@link T}.
     */
    @NotNull
    public <T> ImmutableList<T> AsImmutableList(Class<T> casting_class)
            throws ArgumentNullException, ClassCastException
    {
        ArgumentNullException.ThrowIfNull(casting_class, "casting_class");
        ImmutableList.Builder<T> builder = ImmutableList.builderWithExpectedSize(count);

        for (int I = 0; I < count; I++) {
            builder.add(casting_class.cast(items[I]));
        }

        return builder.build();
    }

    @NotNull
    @Override
    public Iterator<Object> iterator() { return new IteratorImpl(this); }
}
