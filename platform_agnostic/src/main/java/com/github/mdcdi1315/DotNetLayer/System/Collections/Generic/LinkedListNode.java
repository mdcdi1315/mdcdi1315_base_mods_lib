package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Represents a node in a {@link LinkedList}. <br />
 * This class cannot be inherited.
 * @param <T> Specifies the element type of the linked list.
 */
public final class LinkedListNode<T>
{
    @AllowNull
    LinkedList<T> list;
    @AllowNull
    LinkedListNode<T> next;
    @AllowNull
    LinkedListNode<T> prev;
    @AllowNull
    private T item;

    /**
     * Initializes a new instance of the {@link LinkedListNode} class, containing the specified value.
     * @param value The value to contain in the {@link LinkedListNode}.
     */
    public LinkedListNode(@AllowNull T value)
    {
        list = null;
        next = prev = null;
        item = value;
    }

    LinkedListNode(@AllowNull LinkedList<T> list, @AllowNull T value)
    {
        this.list = list;
        this.item = value;
        next = prev = null;
    }

    /**
     * Gets the {@link LinkedList} that the {@link LinkedListNode} belongs to.
     * @return A reference to the {@link LinkedList} that the {@link LinkedListNode} belongs to, or {@code null} if the {@link LinkedListNode} is not linked.
     */
    @MaybeNull
    public LinkedList<T> GetList() { return list; }

    /**
     * Gets the next node in the {@link LinkedList}.
     * @return A reference to the next node in the {@link LinkedList}, or {@code null} if the current node is the last element ({@link LinkedList#GetLast()}) of the {@link LinkedList}.
     */
    @MaybeNull
    public LinkedListNode<T> GetNext() { return (next == null || (list != null && next == list.head)) ? null : next; }

    /**
     * Gets the previous node in the {@link LinkedList}.
     * @return A reference to the previous node in the {@link LinkedList}, or {@code null} if the current node is the last element ({@link LinkedList#GetFirst()}) of the {@link LinkedList}.
     */
    @MaybeNull
    public LinkedListNode<T> GetPrevious() { return (prev == null || (list != null && this == list.head)) ? null : prev; }

    /**
     * Gets the value contained in the node.
     * @return The value contained in the node.
     */
    @MaybeNull
    public T GetValue() { return item; }

    /**
     * Sets the value contained in the node.
     * @param value The new value that the node will contain after this method returns.
     */
    public void SetValue(@AllowNull T value) { item = value; }

    void Invalidate()
    {
        list = null;
        next = null;
        prev = null;
    }
}
