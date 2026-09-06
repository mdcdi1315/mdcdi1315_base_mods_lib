package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.NextNextEnumerator;
import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.PriorityQueueBank;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.linkednodes.NodeWithNextPointer;

/**
 * Provides a default implementation of the {@link IPriorityQueue} interface, that manages the priority banks by a single linked list.
 * @param <T> The type of the elements to be stored to this priority queue.
 * @since 1.0.35
 */
public class SingleLinkedListBasedPriorityQueue<T>
    implements IPriorityQueue<T>
{
    private int priority_bank_count;
    private NodeWithNextPointer<PriorityQueueBank<T>> head, current, deq_node;

    /**
     * Constructs a new and empty instance of the {@link SingleLinkedListBasedPriorityQueue} class.
     */
    public SingleLinkedListBasedPriorityQueue()
    {
        priority_bank_count = 0;
        deq_node = head = current = null;
    }

    /**
     * Creates a new and empty instance of the {@link SingleLinkedListBasedPriorityQueue} class. <br />
     * The object that is returned from this method is thread-safe.
     * @return A {@link SingleLinkedListBasedPriorityQueue} instance that it's operations are synchronized.
     * @param <T> The type of the elements to be stored to the newly created priority queue.
     * @apiNote Note that the returned object does also implement the {@link ISynchronizedByObject} interface.
     */
    @NotNull
    public static <T> SingleLinkedListBasedPriorityQueue<T> CreateSynchronized() { return new Synchronized<>(); }

    private static final class Synchronized<T>
        extends SingleLinkedListBasedPriorityQueue<T>
        implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public void Clear() { synchronized (lock) { super.Clear(); } }

        @Override
        public T TryPeek() { synchronized (lock) { return super.TryPeek(); } }

        @Override
        public T TryDequeue() { synchronized (lock) { return super.TryDequeue(); } }

        @Override
        public void Enqueue(T item) { synchronized (lock) { super.Enqueue(item); } }

        @Override
        public void EnqueueLast(T item) { synchronized (lock) { super.EnqueueLast(item); } }

        @Override
        public IEnumerator<T> GetEnumerator() { synchronized (lock) { return super.GetEnumerator(); } }

        @Override
        public void EnqueueAll(IEnumerable<T> items) throws ArgumentNullException { synchronized (lock) { super.EnqueueAll(items); } }

        @Override
        public void Enqueue(int priority, T element) throws ArgumentOutOfRangeException { synchronized (lock) { super.Enqueue(priority, element); } }

        @Override
        public IEnumerator<T> GetEnumerator(int priority) throws ArgumentOutOfRangeException { synchronized (lock) { return super.GetEnumerator(priority); } }

        @Override
        public void EnqueueAll(int priority, IEnumerable<T> items) throws ArgumentNullException, ArgumentOutOfRangeException { synchronized (lock) { super.EnqueueAll(priority, items); } }
    }

    @MaybeNull
    private PriorityQueueBank<T> GetPriorityBank(int index)
    {
        NodeWithNextPointer<PriorityQueueBank<T>> current = this.head;
        while ((index--) > 0 && current != null) { current = current.Next; }
        return (current == null) ? null : current.Value;
    }

    @NotNull
    private PriorityQueueBank<T> GetOrEnsurePriorityBank(int index)
    {
        if (head == null) {
            priority_bank_count++;
            head = this.current = new NodeWithNextPointer<>(new PriorityQueueBank<>());
        }
        NodeWithNextPointer<PriorityQueueBank<T>> current = head;
        while ((index--) > 0)
        {
            if (current.Next == null)
            {
                priority_bank_count++;
                this.current = current.Next = new NodeWithNextPointer<>(new PriorityQueueBank<>());
            }
            current = current.Next;
        }
        // If the node reference that tracks dequeue has finished enumeration,
        // or has not been defined yet, then the below if statement will make
        // sure that we correctly update this variable.
        if (deq_node == null) { deq_node = head; }
        return current.Value;
    }

    @Override
    public boolean PrioritiesAreExpandable() { return true; }

    @Override
    public int GetPriorityCount() { return priority_bank_count; }

    @Override
    public IEnumerator<T> GetEnumerator(int priority)
            throws ArgumentOutOfRangeException
    {
        if (priority < 0) {
            throw new ArgumentOutOfRangeException("priority", "Priority index out of bounds");
        } else if (priority > priority_bank_count) {
            throw new ArgumentOutOfRangeException("priority", "Priority index out of bounds");
        } else {
            PriorityQueueBank<T> bank = GetPriorityBank(priority);
            if (bank == null) {
                throw new ArgumentOutOfRangeException("priority", "Priority index out of bounds");
            } else {
                return bank.GetEnumerator();
            }
        }
    }

    @Override
    public void Enqueue(int priority, T element)
            throws ArgumentOutOfRangeException
    {
        if (priority < 0) {
            throw new ArgumentOutOfRangeException("priority", "Priority index out of bounds");
        } else {
            GetOrEnsurePriorityBank(priority).Enqueue(element);
        }
    }

    @Override
    public void EnqueueLast(T item)
    {
        if (current == null)
        {
            priority_bank_count++;
            head = current = new NodeWithNextPointer<>(new PriorityQueueBank<>());
        }
        current.Value.Enqueue(item);
        // Because we do not call the GetOrEnsurePriorityBank here, we must ensure that our enumeration pointer is correct.
        if (deq_node == null) { deq_node = head; }
    }

    @Override
    public void Enqueue(T item) { GetOrEnsurePriorityBank(IMMEDIATE_PRIORITY_INDEX_VALUE).Enqueue(item); }

    @Override
    public void EnqueueAll(int priority, IEnumerable<T> items)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        if (priority < 0) {
            throw new ArgumentOutOfRangeException("priority", "Priority cannot be negative");
        } else {
            PriorityQueueBank<T> bank = GetOrEnsurePriorityBank(priority);
            try (IEnumerator<T> enumerator = items.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    bank.Enqueue(enumerator.getCurrent());
                }
            }
        }
    }

    @Override
    public T TryDequeue()
    {
        T value = null;
        NodeWithNextPointer<PriorityQueueBank<T>> current = deq_node;
        while (current != null && (value = current.Value.Dequeue()) == null)
        {
            current.Value.ClearReferences(); // Makes sure that any node references are deleted to aid reducing memory footprint.
            current = current.Next;
        }
        deq_node = current;
        return value;
    }

    @Override
    public T TryPeek()
    {
        T value = null;
        NodeWithNextPointer<PriorityQueueBank<T>> current = deq_node;
        while (current != null && (value = current.Value.Peek()) == null)
        {
            current.Value.ClearReferences(); // Makes sure that any node references are deleted to aid reducing memory footprint.
            current = current.Next;
        }
        deq_node = current;
        return value;
    }

    @Override
    public void Clear()
    {
        priority_bank_count = 0;
        head = current = null;
    }

    @NotNull
    @Override
    public final String toString()
    {
        StringBuilder sb = new StringBuilder("SingleLinkedListBasedPriorityQueue<?> { NumberOfPriorityBanks = ")
                .append(priority_bank_count)
                .append(", ExpandablePriorities = true, Values = [ ");

        NodeWithNextPointer<PriorityQueueBank<T>> current = head;

        while (current != null)
        {
            sb.append('{');
            try (NextNextEnumerator<T> enumerator = new NextNextEnumerator<>(current.Value.GetEnumerator()))
            {
                while (enumerator.MoveNext())
                {
                    sb.append(enumerator.getCurrent());
                    if (enumerator.HasNextNextElement()) { sb.append(", "); }
                }
            }
            current = current.Next;
            if (current == null) {
                sb.append('}');
            } else {
                sb.append("}, ");
            }
        }

        return sb.append(" ] }").toString();
    }
}
