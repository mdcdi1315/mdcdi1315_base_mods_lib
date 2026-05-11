package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides the base scaffolding and support for doubly-linked lists.
 * @param <T> The type of the elements that the double-linked list retains.
 * @since 1.0.31
 */
public interface IDoubleLinkedList<T>
    extends IList<T>
{
    /**
     * Gets a node reference to the specified element in the list at {@code index}.
     * @param index The index of the element to get its node reference.
     * @return A new instance of the {@link DoublyLinkedListNodeReference} class, representing the held node.
     * @throws ArgumentOutOfRangeException {@code index} is negative value. <br /> -or- <br /> {@code index} is greater than the list's bound.
     */
    @NotNull
    DoublyLinkedListNodeReference<T> GetNode(int index) throws ArgumentOutOfRangeException;

    /**
     * Applies a {@link DoublyLinkedListNodeReference} object previously obtained via the {@link #GetNode(int)} method.
     * @param node The {@link DoublyLinkedListNodeReference} object to apply.
     * @throws ArgumentNullException {@code node} is {@code null}.
     * @throws InvalidDoublyLinkedListNodeApplicationException {@code node} cannot be applied to the current object.
     */
    void ApplyNode(DoublyLinkedListNodeReference<T> node) throws ArgumentNullException, InvalidDoublyLinkedListNodeApplicationException;
}
