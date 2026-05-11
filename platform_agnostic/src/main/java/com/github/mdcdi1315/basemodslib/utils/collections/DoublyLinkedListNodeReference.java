package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Provides the reference to a node in a {@link IDoubleLinkedList} object. <br />
 * Essentially, this provides an abstraction layer over the actual backed node provided by the doubly-linked list object. <br />
 * Classes implementing {@link IDoubleLinkedList} should also provide an overridden version of this class as well when returning node references.
 * @param <T> The type of the value that the node reference retains.
 * @since 1.0.31
 */
public class DoublyLinkedListNodeReference<T>
{
    private static final byte MOD_FLAG_NONE = 0,
            MOD_FLAG_MOD_PREVIOUS = 1 << 0 ,
            MOD_FLAG_MOD_NEXT = 1 << 1,
            MOD_FLAG_MOD_CURRENT = 1 << 2,
            MOD_FLAG_REMOVE_CURRENT = 1 << 3;

    private byte modification_flags;
    private final IDoubleLinkedList<T> list;
    private T current_value, prev_value, next_value;

    /**
     * Initializes a new instance of the {@link DoublyLinkedListNodeReference} class.
     * @param list The {@link IDoubleLinkedList} object to initialize the node reference from.
     * @param current The current node value in the list.
     * @param previous The previous node value in the list.
     * @param next The next node value in the list.
     * @throws ArgumentNullException {@code list} is {@code null}.
     */
    public DoublyLinkedListNodeReference(IDoubleLinkedList<T> list, @AllowNull T current, @AllowNull T previous, @AllowNull T next)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.list = list, "list");
        this.next_value = next;
        this.prev_value = previous;
        this.current_value = current;
        modification_flags = MOD_FLAG_NONE;
    }

    /**
     * Gets the value of the currently scoped-to node.
     * @return The value of the current node.
     */
    @MaybeNull
    public final T GetValue() { return current_value; }

    /**
     * Gets the value of the next from the current scoped-to node.
     * @return The value of the next node.
     */
    @MaybeNull
    public final T GetNextNodeValue() { return next_value; }

    /**
     * Gets the value of the previous from the current scoped-to node.
     * @return The value of the previous node.
     */
    @MaybeNull
    public final T GetPreviousNodeValue() { return prev_value; }

    /**
     * Gets the {@link IDoubleLinkedList} object that this node reference is constructed from.
     * @return The {@link IDoubleLinkedList} object.
     */
    // Allow overrides to happen on this method so that derived classes can provide the actual list object.
    @NotNull
    public IDoubleLinkedList<T> GetList() { return list; }

    private boolean HasFlagFast(byte flag) { return (modification_flags & flag) == flag; }

    /**
     * Gets a value whether the next node value is modified by using the {@link #SetNextValue(Object)} method.
     * @return A boolean value indicating modification of the next node value.
     */
    public final boolean NextValueModified() { return HasFlagFast(MOD_FLAG_MOD_NEXT); }

    /**
     * Gets a value whether the current node value is modified by using the {@link #SetNewValue(Object)} method.
     * @return A boolean value indicating modification of the current node value.
     */
    public final boolean CurrentValueModified() { return HasFlagFast(MOD_FLAG_MOD_CURRENT); }

    /**
     * Gets a value whether the previous node value is modified by using the {@link #SetPreviousValue(Object)} method.
     * @return A boolean value indicating modification of the previous node value.
     */
    public final boolean PreviousValueModified() { return HasFlagFast(MOD_FLAG_MOD_PREVIOUS); }

    /**
     * Gets a value whether the current scoped-to node should be removed, after applying any value changes to next/previous nodes.
     * @return A boolean value indicating removal of the current scoped-to node.
     */
    public final boolean IsToBeRemoved() { return HasFlagFast(MOD_FLAG_REMOVE_CURRENT); }

    /**
     * Sets a new value for the current doubly-linked list node.
     * @param value The new value to assign. Can be {@code null}.
     */
    public final void SetNewValue(@AllowNull T value)
    {
        synchronized (this)
        {
            current_value = value;
            modification_flags |= MOD_FLAG_MOD_CURRENT;
        }
    }

    /**
     * Sets a new value for the previous from the current doubly-linked list node.
     * @param value The new value to assign. Can be {@code null}.
     */
    public final void SetPreviousValue(@AllowNull T value)
    {
        synchronized (this)
        {
            prev_value = value;
            modification_flags |= MOD_FLAG_MOD_PREVIOUS;
        }
    }

    /**
     * Sets a new value for the next from the current doubly-linked list node.
     * @param value The new value to assign. Can be {@code null}.
     */
    public final void SetNextValue(@AllowNull T value)
    {
        synchronized (this)
        {
            next_value = value;
            modification_flags |= MOD_FLAG_MOD_NEXT;
        }
    }

    /**
     * Call this method to remove the backed node from the list after applying any modifications to the next/previous node values.
     */
    public final void Remove()
    {
        synchronized (this) {
            modification_flags |= MOD_FLAG_REMOVE_CURRENT;
        }
    }
}
