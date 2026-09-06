package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Defines the base interface for stack (FIFO) collections. <br />
 * Note: Implementations must return the elements from the enumerators in the order in which they will be popped from the stack.
 * @param <T> The type of the elements that this stack will hold.
 */
public interface IStack<T>
    extends IEnumerable<T>
{
    /**
     * Attempts to pop the last pushed value from the stack. <br />
     * If the return value is {@code null}, the stack is empty.
     * @return The popped value, if the stack is not empty. If it is, {@code null} is returned.
     */
    @MaybeNull
    T TryPop();

    /**
     * Attempts to peek the last pushed value from the stack.
     * (That is, getting the last pushed element without popping it) <br />
     * It is equivalent as popping the element, then pushing it again.
     * @return A value whether a value was found in the stack and it was returned.
     */
    @MaybeNull
    T TryPeek();

    /**
     * Pushes a value to the stack.
     * @param item The value to push to the stack.
     */
    void Push(@AllowNull T item);

    /**
     * Removes all the pushed items from the stack.
     */
    void Clear();

    /**
     * Pushes all the values provided by the specified enumerable, in the order they are read from the enumerable.
     * @param items The items to push to the stack.
     * @throws ArgumentNullException {@code items} is {@code null}.
     * @implNote Implementations that have implemented better ways to push all stack items in bulk should override this implementation.
     */
    default void PushAll(IEnumerable<T> items)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        try (IEnumerator<T> en = items.GetEnumerator())
        {
            while (en.MoveNext()) { Push(en.getCurrent()); }
        }
    }

    /**
     * Attempts to duplicate the lastly pushed item of the stack. <br />
     * That is, the item popped from the stack is pushed back two times.
     * @return The item that was duplicated. May be {@code null}.
     * If the stack is empty, nothing is pushed.
     * @since 1.0.37
     */
    @MaybeNull
    default T DuplicateLastItem()
    {
        T value = TryPop();
        if (value != null)
        {
            Push(value);
            Push(value);
        }
        return value;
    }
}
