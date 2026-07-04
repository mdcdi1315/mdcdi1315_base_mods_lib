package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.collections.helpers.PriorityQueue_AllPriorityBanksDefaultEnumerator;

/**
 * Provides the base interface for priority queues.
 * <h4>The conceptualization of priority queues in BML</h4>
 * A priority queue has as a base interface the {@link IPriorityQueue} interface,
 * that it defines the API contract for defining priority queues. <br />
 * The priorities are defined through discrete 32-bit integers that are defined as follows:
 * <ul>
 *     <li>
 *         Are in a range of a normal Java array index,
 *         that is the range [0..{@link Integer#MAX_VALUE}].
 *     </li>
 *     <li>
 *         Priority accesses are sorted in an ascending manner, that is,
 *         items enqueued at priority 0 are always dequeued first. <br />
 *         So, items that have been enqueued with priorities closer to
 *         {@link Integer#MAX_VALUE} are less likely to be dequeued than
 *         items that have been enqueued with priorities closer to zero. <br />
 *         The constant {@link #IMMEDIATE_PRIORITY_INDEX_VALUE} can be used
 *         to get the immediate priority bank of all the priority queue implementations.
 *     </li>
 *     <li>
 *         They are conceptually called 'priority banks' since each priority
 *         should be an independent queue.
 *     </li>
 * </ul>
 * While it is possible to enqueue an item to any priority bank, it is not possible to
 * dequeue from any priority bank. While that is also possible in typical PQ implementations, this interface declaration
 * does not do that to avoid any potential state corruption caused by removing items directly from the desired priority. <br />
 * Instead, the {@link #TryDequeue()} method should get that item that it is to be dequeued, based on where
 * the next non-empty priority bank is located. <br />
 * Implementations most of the time should not override the {@link #GetEnumerator()} method; instead, they must define
 * the {@link #GetEnumerator(int)} method overload, that enumerates the elements of the selected priority bank. <br />
 * Note also that there is <em>not</em> a requirement to return the elements of each priority bank in their queued order. <br />
 * Furthermore, the class provides a default implementation of the {@link #GetEnumerator()} method. <br />
 * That method's contract is to return the elements of all priority banks in the ascending manner described above. <br />
 * The default implementation of the method is that it gets all the defined priority banks one by one, then exposes the elements
 * of each bank back to the enumerator's {@link IEnumerator#getCurrent()} method. <br />
 * What's more, a helper method {@link #EnqueueLast(Object)} is defined, that allows enqueues in the lowest priority as possible. <br />
 * To be noted, if someone uses only one priority bank, it is like using a normal {@link IQueue} implementation.
 * However, an implementation should not also implement the {@link ITraversableQueue} interface, as that is
 * incompatible with the abstraction details of this interface and as such it should not be exposed.
 * @param <T> The type of the elements that will be contained in the priority queue.
 * @since 1.0.35
 */
public interface IPriorityQueue<T>
    extends IQueue<T>
{
    /**
     * Defines a constant of the index value of the immediate priority of all priority queues extending from the {@link IPriorityQueue} interface.
     */
    int IMMEDIATE_PRIORITY_INDEX_VALUE = 0;

    /**
     * Gets the number of priorities that are defined in the current priority queue.
     * @return The number of priority banks defined in the queue.
     * @apiNote Count of priority banks is defined as follows: <br />
     * In initial and empty state, this returns 0, if the method {@link #PrioritiesAreExpandable()} returns {@code true}. <br />
     * If an {@link #Enqueue(int, Object)} call happens at priority index, the value of this method should update if that priority
     * does not exist in the queue and {@link #PrioritiesAreExpandable()} returns {@code true}.
     */
    int GetPriorityCount();

    /**
     * Queries whether the current queue implementation has an expandable number of priority banks or not.
     * @return A value whether the current object supports adding new priority banks as needed.
     */
    boolean PrioritiesAreExpandable();

    /**
     * Gets all the enqueued items at the specified priority.
     * @param priority The priority bank index to get all the enqueued items for that priority bank.
     * @return An enumerator object able to enumerate through all the enqueued items of the specified priority.
     * @throws ArgumentOutOfRangeException {@code priority} is less than zero, or out of the currently defined priority count.
     */
    @NotNull
    IEnumerator<T> GetEnumerator(int priority) throws ArgumentOutOfRangeException;

    /**
     * Enqueues an item with the specified dequeue priority.
     * @param priority The priority of the item to be enqueued.
     * @param element The element to be enqueued.
     * @throws ArgumentOutOfRangeException {@code priority} is less than zero, or,
     * if the priority banks are non-expandable, that the defined priority is out of the currently defined priority count.
     */
    void Enqueue(int priority, @AllowNull T element) throws ArgumentOutOfRangeException;

    /**
     * Enqueues all the values provided by the specified enumerable at the specified dequeue priority, in the order they are read from the enumerable. <br />
     * @param items The items to push to the queue.
     * @implNote Implementations that have implemented better ways to enqueue all queue items in bulk should override this implementation.
     * @throws ArgumentNullException {@code items} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code priority} is less than zero.
     */
    default void EnqueueAll(int priority, IEnumerable<T> items)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        if (priority < 0) {
            throw new ArgumentOutOfRangeException("priority", "Priority cannot be negative");
        } else {
            try (IEnumerator<T> enumerator = items.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    Enqueue(priority, enumerator.getCurrent());
                }
            }
        }
    }

    /**
     * Enqueues an item to the immediate priority queue.
     * @param item The item to enqueue.
     * @see IPriorityQueue#IMMEDIATE_PRIORITY_INDEX_VALUE
     * @implNote The default method implementation delegates to the {@link #Enqueue(int, Object)} method.
     */
    @Override
    default void Enqueue(@AllowNull T item) { Enqueue(IMMEDIATE_PRIORITY_INDEX_VALUE, item); }

    /**
     * Enqueues an item to the lastly-defined priority bank.
     * @param item The item to enqueue.
     * @implNote The default method implementation delegates to the {@link #Enqueue(int, Object)} method,
     * and calls the {@link #GetPriorityCount()} method to get the lastly-defined priority bank.
     * @apiNote Implementations of this method must check if the queue has not any priority banks,
     * in which case the enqueue operation must be attempted to the immediate priority bank.
     */
    default void EnqueueLast(@AllowNull T item)
    {
        int count = GetPriorityCount();
        Enqueue(count == 0 ? IMMEDIATE_PRIORITY_INDEX_VALUE : count - 1, item);
    }

    /**
     * Enqueues all the given items to the lastly-defined priority bank.
     * @param items The items to push to the queue.
     * @throws ArgumentNullException {@code items} is {@code null}.
     * @implNote The default method implementation delegates to the {@link #EnqueueAll(int, IEnumerable)} method,
     * and calls the {@link #GetPriorityCount()} method to get the lastly-defined priority bank.
     * @apiNote Implementations of this method must check if the queue has not any priority banks,
     * in which case the enqueue operation must be attempted to the immediate priority bank.
     */
    @Override
    default void EnqueueAll(IEnumerable<T> items)
            throws ArgumentNullException 
    {
        int count = GetPriorityCount();
        EnqueueAll(count == 0 ? IMMEDIATE_PRIORITY_INDEX_VALUE : count - 1, items);
    }

    /**
     * Gets all the enqueued items on this priority queue object.
     * @return An enumerator able to enumerate through all the enqueued priority queue items.
     * @apiNote You do not need to implement this method; but if you feel that you have found a faster
     * alternative compared to the provided enumerator implementation, you are free to override it.
     */
    @NotNull
    @Override
    default IEnumerator<T> GetEnumerator() { return new PriorityQueue_AllPriorityBanksDefaultEnumerator<>(this); }
}
