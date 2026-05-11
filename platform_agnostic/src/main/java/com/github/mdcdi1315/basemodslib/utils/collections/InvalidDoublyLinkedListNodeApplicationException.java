package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

/**
 * Exception class thrown when a node is applied to a {@link IDoubleLinkedList}, but the node reference and/or the modifications applied to it are not applicable to the current list object.
 * @since 1.0.31
 */
public class InvalidDoublyLinkedListNodeApplicationException
    extends InvalidOperationException
{
    /**
     * Initializes a new instance of the {@link InvalidDoublyLinkedListNodeApplicationException} class, with a default error message.
     */
    public InvalidDoublyLinkedListNodeApplicationException() { super("Could not apply the specified node reference to the list."); }

    /**
     * Initializes a new instance of the {@link InvalidDoublyLinkedListNodeApplicationException} class, with the specified error message.
     * @param message The custom error message to specify.
     */
    public InvalidDoublyLinkedListNodeApplicationException(@AllowNull String message) { super(message); }

    /**
     * Initializes a new instance of the {@link InvalidDoublyLinkedListNodeApplicationException} class, with the specified error message,
     * as well as the specified {@link Exception} that is the cause of this exception object to be created.
     * @param message The custom error message to specify.
     * @param cause The cause that is the reason of creating an instance of the exception.
     */
    public InvalidDoublyLinkedListNodeApplicationException(@AllowNull String message, @AllowNull Exception cause) { super(message, cause); }

    /**
     * Throws an exception if, and only if, the list reference specified in {@code reference} is different from the list object specified in {@code current_list} parameter.
     * @param current_list The current {@link IDoubleLinkedList} object from where this is invoked.
     * @param reference The {@link DoublyLinkedListNodeReference} object provided.
     * @param <T> The type of the elements that the list node and the list are.
     * @throws InvalidDoublyLinkedListNodeApplicationException If the condition described in the summary is not satisfied.
     */
    public static <T> void ThrowIfForeignList(IDoubleLinkedList<T> current_list, DoublyLinkedListNodeReference<T> reference)
            throws InvalidDoublyLinkedListNodeApplicationException
    {
        if (current_list != reference.GetList()) {
            throw new InvalidDoublyLinkedListNodeApplicationException("The specified node reference does not belong to this list!");
        }
    }
}
