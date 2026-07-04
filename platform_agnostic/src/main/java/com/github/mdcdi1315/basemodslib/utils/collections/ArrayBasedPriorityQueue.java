package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.OverflowException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.NextNextEnumerator;
import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.PriorityQueueBank;

import java.util.Arrays;

/**
 * Provides a default implementation of the {@link IPriorityQueue} interface, that manages the priority banks by an array.
 * @param <T> The type of the elements to be stored to this priority queue.
 * @since 1.0.35
 */
public class ArrayBasedPriorityQueue<T>
    implements IPriorityQueue<T>, IArrayBasedCollection
{
    private Object[] banks;
    private int priority_bank_count, dq_index;

    /**
     * Constructs a new and empty instance of the {@link ArrayBasedPriorityQueue} class.
     */
    public ArrayBasedPriorityQueue()
    {
        banks = new Object[0];
        dq_index = priority_bank_count = 0;
    }

    /**
     * Constructs a new and empty instance of the {@link ArrayBasedPriorityQueue} class,
     * specifying the initial bank slots that the object will manage.
     * @param number_of_initial_banks The number of desired initial banks to be managed by the returned object.
     * @throws ArgumentOutOfRangeException {@code number_of_initial_banks} is a negative value.
     */
    public ArrayBasedPriorityQueue(int number_of_initial_banks)
            throws ArgumentOutOfRangeException
    {
        if (number_of_initial_banks < 0) {
            throw new ArgumentOutOfRangeException("number_of_initial_banks", "Number of initial priority queue cannot be negative!");
        } else {
            dq_index = priority_bank_count = 0;
            banks = new Object[number_of_initial_banks];
        }
    }

    /**
     * Creates a new and empty instance of the {@link ArrayBasedPriorityQueue} class. <br />
     * The object that is returned from this method is thread-safe.
     * @return A {@link ArrayBasedPriorityQueue} instance that it's operations are synchronized.
     * @param <T> The type of the elements to be stored to the newly created priority queue.
     * @apiNote Note that the returned object does also implement the {@link ISynchronizedByObject} interface.
     */
    @NotNull
    public static <T> ArrayBasedPriorityQueue<T> CreateSynchronized() { return new Synchronized<>(); }

    /**
     * Creates a new and empty instance of the {@link ArrayBasedPriorityQueue} class,
     * specifying the initial bank slots that the object will manage. <br />
     * The object that is returned from this method is thread-safe.
     * @param number_of_initial_banks The number of desired initial banks to be managed by the returned object.
     * @throws ArgumentOutOfRangeException {@code number_of_initial_banks} is a negative value.
     * @param <T> The type of the elements to be stored to the newly created priority queue.
     * @apiNote Note that the returned object does also implement the {@link ISynchronizedByObject} interface.
     */
    @NotNull
    public static <T> ArrayBasedPriorityQueue<T> CreateSynchronized(int number_of_initial_banks) throws ArgumentOutOfRangeException { return new Synchronized<>(number_of_initial_banks); }

    private static final class Synchronized<T>
        extends ArrayBasedPriorityQueue<T>
        implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        public Synchronized(int number_of_initial_banks) { super(number_of_initial_banks); lock = new Object(); }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public void Clear() { synchronized (lock) { super.Clear(); } }

        @Override
        public T TryPeek() { synchronized (lock) { return super.TryPeek(); } }

        @Override
        public void TrimExcess() { synchronized (lock) { super.TrimExcess(); } }

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
        public void EnsureCapacity(int n_elements) throws OverflowException, ArgumentOutOfRangeException { synchronized (lock) { super.EnsureCapacity(n_elements); } }

        @Override
        public void EnqueueAll(int priority, IEnumerable<T> items) throws ArgumentNullException, ArgumentOutOfRangeException { synchronized (lock) { super.EnqueueAll(priority, items); } }
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private PriorityQueueBank<T> GetPriorityBank(int index)
    {
        PriorityQueueBank<T> ret_bank;
        Object bank = banks[index];
        if (bank == null) {
            dq_index = 0; // Reset dequeue tracking index to ensure that newly inserted items are taken into account.
            priority_bank_count++;
            ret_bank = new PriorityQueueBank<>();
            banks[index] = ret_bank;
        } else {
            ret_bank = (PriorityQueueBank<T>) bank;
        }
        return ret_bank;
    }

    @NotNull
    private PriorityQueueBank<T> GetOrEnsurePriorityBank(int index)
    {
        // Reset dequeue tracking index to ensure that newly inserted items are taken into account.
        dq_index = 0;
        if (index < this.banks.length) {
            return GetPriorityBank(index);
        } else {
            int new_size = index + 1;
            Object[] new_banks = new Object[new_size];
            // Note here that we are copying the entire original array; this happens because
            // some of the array's positions in the end may have a valid instance,
            // which would lose them if were using the value of priority_bank_count field.
            System.arraycopy(this.banks, 0, new_banks, 0, banks.length);
            this.banks = new_banks;
            priority_bank_count++;
            PriorityQueueBank<T> created_bank = new PriorityQueueBank<>();
            this.banks[index] = created_bank;
            return created_bank;
        }
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
        } else if (priority > this.banks.length) {
            throw new ArgumentOutOfRangeException("priority", "Priority index out of bounds");
        } else {
            return GetPriorityBank(priority).GetEnumerator();
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
    public void EnqueueAll(int priority, IEnumerable<T> items)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        if (priority < 0) {
            throw new ArgumentOutOfRangeException("priority", "Priority index out of bounds");
        } else {
            PriorityQueueBank<T> bk_reference = GetOrEnsurePriorityBank(priority);
            try (IEnumerator<T> e = bk_reference.GetEnumerator())
            {
                while (e.MoveNext()) { bk_reference.Enqueue(e.getCurrent()); }
            }
        }
    }

    @Override
    public void EnqueueLast(T item)
    {
        GetOrEnsurePriorityBank(
                priority_bank_count == 0 ? IMMEDIATE_PRIORITY_INDEX_VALUE : banks.length - 1
        ).Enqueue(item);
    }

    @SuppressWarnings("unchecked")
    private PriorityQueueBank<T> GetNextBankToPeekOrDequeue()
    {
        PriorityQueueBank<T> t = null;
        while (dq_index < banks.length && (t = (PriorityQueueBank<T>) banks[dq_index]) == null) { dq_index++; }
        return t;
    }

    @Override
    public T TryDequeue()
    {
        PriorityQueueBank<T> t = GetNextBankToPeekOrDequeue();
        return t == null ? null : t.Dequeue();
    }

    @Override
    public T TryPeek()
    {
        PriorityQueueBank<T> t = GetNextBankToPeekOrDequeue();
        return t == null ? null : t.Peek();
    }

    @Override
    public void Clear()
    {
        dq_index = 0;
        priority_bank_count = 0;
        Arrays.fill(banks, null);
    }

    /**
     * {@inheritDoc}
     * @apiNote Note that this method is going to reorder the priority banks in occurrence
     * order, as they are retrieved from the original array.
     * While the priority sequence is retained, the original priority index values are lost. <br />
     * So, use this only when you are not interested in losing the exact priority positions.
     */
    @Override
    public void TrimExcess()
    {
        // To trim excess array positions, we are going to store them all in an array of priority_bank_count length.
        // This is going to adversely affect priority values, but you can do it if you do not care about their exact positions.
        Object o;
        int found_count = 0, I = 0;
        Object[] new_array = new Object[priority_bank_count];
        for (; I < banks.length && found_count < priority_bank_count; I++)
        {
            if ((o = banks[I]) != null) { new_array[found_count++] = o; }
        }
        if (found_count > 0)
        {
            // Reorder dequeue index so that it picks up the change.
            // Otherwise, we could have bad index access.
            dq_index = 0;
            banks = new_array;
            priority_bank_count = found_count;
        }
    }

    @Override
    public void EnsureCapacity(int n_elements)
            throws OverflowException, ArgumentOutOfRangeException
    {
        if (n_elements < 0) {
            throw new ArgumentOutOfRangeException("n_elements", "Number of elements to be ensured of should not be negative.");
        } else {
            // While in most collections we use the count field, we cannot do that here.
            // This happens because not all the banks may be defined, and all can be in non-contiguous placement,
            // as the collection user defines the priorities to use.
            int new_count = banks.length + n_elements;
            if (new_count < 0) {
                throw new OverflowException("The priority queue has reached it's maximum capacity.");
            } else {
                Object[] new_array = new Object[new_count];
                System.arraycopy(banks, 0, new_array, 0, banks.length);
                banks = new_array;
            }
        }
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public final String toString()
    {
        StringBuilder sb = new StringBuilder("ArrayBasedPriorityQueue<?> { NumberOfPriorityBanks = ")
                .append(priority_bank_count)
                .append(", ExpandablePriorities = true, Values = [ ");

        Object o;

        for (int I = 0, B = 0; I < banks.length && B < priority_bank_count; I++)
        {
            if ((o = banks[I]) != null)
            {
                sb.append('{');
                try (NextNextEnumerator<T> enumerator = new NextNextEnumerator<>(((PriorityQueueBank<T>)o).GetEnumerator()))
                {
                    while (enumerator.MoveNext())
                    {
                        sb.append(enumerator.getCurrent());
                        if (enumerator.HasNextNextElement()) { sb.append(", "); }
                    }
                }
                if ((B+1) < priority_bank_count) {
                    sb.append("}, ");
                } else {
                    sb.append('}');
                }
                B++;
            }
        }

        return sb.append(" ] }").toString();
    }
}
