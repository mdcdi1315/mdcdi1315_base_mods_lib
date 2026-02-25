package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Defines the base interface for queue (LIFO) collections. <br />
 * Note: Implementations must return the elements from the enumerators in the order in which they will be dequeued from the queue.
 * @param <T> The type of the elements that this queue will hold.
 * @since 1.0.18
 */
public interface IQueue<T>
    extends IEnumerable<T>
{
    /**
     * Attempts to dequeue an item from the working queue. <br />
     * The returned item is the item that was actually dequeued from the queue.
     * @return The item that was dequeued. If {@code null}, it means that the queue is empty and no items can be dequeued from it.
     */
    @MaybeNull
    T TryDequeue();

    /**
     * Attempts to peek the next value that will be dequeued from the queue.
     * @return The item that will be dequeued on the next {@link #TryDequeue()} call. If {@code null}, it means that the queue is empty and no items can be dequeued from it.
     */
    @MaybeNull
    T TryPeek();

    /**
     * Enqueues an item into the current queue.
     * @param item The item to enqueue.
     */
    void Enqueue(@AllowNull T item);

    /**
     * Removes all the enqueued items from the queue.
     */
    void Clear();

    /**
     * Enqueues all the values provided by the specified enumerable, in the order they are read from the enumerable. <br />
     * @param items The items to push to the queue.
     * @implNote Implementations that have implemented better ways to enqueue all queue items in bulk should override this implementation.
     * @throws ArgumentNullException {@code items} is {@code null}.
     */
    default void EnqueueAll(IEnumerable<T> items)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(items);
        IEnumerator<T> e = items.GetEnumerator();
        try {
            while (e.MoveNext()) { Enqueue(e.getCurrent()); }
        } finally {
            e.Dispose();
        }
    }
}
