package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Array;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.OverflowException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;

/**
 * An {@link ITraversableQueue} implementation by using an array as the backing storage.
 * @param <T> The type of the elements this queue will hold.
 * @since 1.0.19
 */
public class ArrayBasedQueue<T>
    extends BaseEnumerable<T>
    implements ITraversableQueue<T>, IArrayBasedCollection
{
    // This class manages the 'elements' array in a reverse manner.
    // That is, the item that will be dequeued (namely the 'head') is located
    // close to the beginning of the array.

    private int count;
    private int head; // -1 means no head (empty collection)
    private int tail; // -1 means no tail (empty collection)
    private Object[] elements;

    // A threshold value for when it is more appropriate to shift
    // elements rather than resizing the array to enqueue a new value.
    private static final int SHIFT_THRESHOLD = 4;

    private static final class Synchronized<T>
            extends ArrayBasedQueue<T>
            implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        public Synchronized(int capacity) throws ArgumentOutOfRangeException { super(capacity); lock = new Object(); }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public T TryDequeue() { synchronized (lock) { return super.TryDequeue(); } }

        @Override
        public T TryPeek() { synchronized (lock) { return super.TryPeek(); } }

        @Override
        public void Enqueue(T item) { synchronized(lock) { super.Enqueue(item); } }

        @Override
        public IEnumerator<T> GetEnumerator() { synchronized(lock) { return super.GetEnumerator(); } }

        @Override
        public void Clear() { synchronized(lock) { super.Clear(); } }

        @Override
        public T GetItem(int index) throws ArgumentOutOfRangeException { synchronized(lock) { return super.GetItem(index); } }

        @Override
        public void TrimExcess() { synchronized(lock) { super.TrimExcess(); } }

        @Override
        public void EnsureCapacity(int n_elements) throws OverflowException, ArgumentOutOfRangeException { synchronized(lock) { super.EnsureCapacity(n_elements); } }

        @Override
        public void EnqueueAll(IEnumerable<T> items) throws OverflowException, ArgumentNullException { synchronized(lock) { super.EnqueueAll(items); } }

        @Override
        public void EnqueueAll(T... items) throws OverflowException, ArgumentNullException { synchronized(lock) { super.EnqueueAll(items); } }
    }

    /**
     * Initializes a new, empty instance of the {@link ArrayBasedQueue} class.
     */
    public ArrayBasedQueue()
    {
        tail = -1;
        count = 0;
        head = -1;
        elements = new Object[0];
    }

    /**
     * Initializes a new, empty instance of the {@link ArrayBasedQueue} class, with the specified initial capacity.
     * @param capacity The initial capacity of the newly created array-based queue object.
     * @throws ArgumentOutOfRangeException {@code capacity} is negative.
     */
    public ArrayBasedQueue(int capacity)
        throws ArgumentOutOfRangeException
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative number.");
        } else {
            tail = -1;
            count = 0;
            head = -1;
            elements = new Object[capacity];
        }
    }

    /**
     * Creates a thread-safe queue.
     * @return An object extending the {@link ArrayBasedQueue} class and is thread-safe.
     */
    public static <T> ArrayBasedQueue<T> CreateSynchronized() { return new Synchronized<>(); }

    /**
     * Creates a thread-safe queue, with the specified initial capacity.
     * @param capacity The initial capacity that the thread-safe queue object will have.
     * @return An object extending the {@link ArrayBasedQueue} class and is thread-safe.
     */
    public static <T> ArrayBasedQueue<T> CreateSynchronized(int capacity) { return new Synchronized<>(capacity); }

    @Override
    public T TryDequeue()
    {
        if (count < 1) {
            return null;
        } else {
            count--;
            return (T) elements[head++];
        }
    }

    @Override
    public T TryPeek() { return (count < 1) ? null : (T) elements[head]; }

    @Override
    public void Enqueue(T item)
    {
        if (count == 0) {
            if (elements.length == 0) { elements = new Object[1]; }
            elements[0] = item;
            tail = head = 0;
        } else if (tail + 1 < elements.length) {
            PutAtTail(item);
        } else {
            // OK. We need to enlarge the array.
            // Let's see first if we can avoid to enlarge by shifting the elements.
            // Note: shifting is not best to be done at all times because we must
            // consume time to shift ALL the elements at the beginning.
            // Since we already need that time, it is best to enlarge and shift at the same time instead.
            if (head > SHIFT_THRESHOLD) {
                // We can avoid the enlarge, shift the elements.
                ShiftElements();
            } else {
                EnlargeAndShift(1);
            }
            // Now we can put our element
            PutAtTail(item);
        }
        count++;
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return ArrayEnumerator.ByBoundsCasted(elements, head, count); }

    @Override
    public int GetCount() { return count; }

    @Override
    public void Clear() { head = tail = -1; count = 0; }

    @Override
    public T GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be larger than the queue's current bounds.");
        } else {
            return (T) elements[head+index];
        }
    }

    // This is just primitive instructions, we can replace this method call with it's body.
    // It is just defined for convenience, nothing else.
    private void PutAtTail(T item) { elements[++tail] = item; }

    // Shifts ALL the queue's elements at the beginning.
    // It updates head and tail values once done.
    private void ShiftElements()
    {
        if (count == 0) { return; } // No meaning to execute if we do not have any items to process
        for (int I = head, J = 0; I <= tail; I++)
        {
            elements[J++] = elements[I];
        }
        head = 0;
        tail = count - 1;
    }

    // Enlarges the array + shifting its valid elements to the beginning.
    // It updates head and tail values if the queue has valid data in it.
    private void EnlargeAndShift(int by)
    {
        int new_count = count + by;
        if (new_count < 0) {
            // Overflow detected, throw
            throw new OverflowException("The list has reached it's maximum capacity.");
        } else if (new_count > elements.length) {
            Object[] copy = new Object[new_count];
            if (count > 0)
            {
                Array.Copy(elements, head, copy, 0, count);
                head = 0;
                tail = count - 1;
            }
            elements = copy;
        }
    }

    @Override
    public void TrimExcess()
    {
        if (count == 0) {
            elements = new Object[0];
        } else {
            // Maybe we should call here the EnlargeAndShift method with ct == 0
            // but performing element shifting and resizing at the same time is more robust.
            Object[] copy = new Object[count];
            System.arraycopy(elements, head, copy, 0, count);
            elements = copy;
            head = 0;
            tail = count - 1;
        }
    }

    @Override
    public void EnsureCapacity(int n_elements)
            throws OverflowException, ArgumentOutOfRangeException
    {
        if (n_elements < 0) {
            throw new ArgumentOutOfRangeException("n_elements", "Number of elements to be ensured of should not be negative.");
        } else {
            EnlargeAndShift(n_elements);
        }
    }

    public void EnqueueAll(IEnumerable<T> items)
            throws OverflowException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        if (items instanceof IList<T> list) {
            EnqueueList(list);
        } else if (items instanceof ICollection<T> collection) {
            EnqueueCollection(collection);
        } else {
            IEnumerator<T> enumerator = items.GetEnumerator();
            try {
                while (enumerator.MoveNext()) { Enqueue(enumerator.getCurrent()); }
            } finally {
                enumerator.Dispose();
            }
        }
        // If it happens that the first enqueue happened through this method, the head might not be appropriately updated.
        // As such, we need to update it here ourselves.
        if (head == -1) { head = 0; }
    }

    /**
     * Specialization of the {@link #EnqueueAll(IEnumerable)} method, for enqueuing elements statically known.
     * @param items The items to enqueue on the queue.
     * @throws OverflowException Adding the specified items would cause the collection to overflow.
     * @throws ArgumentNullException {@code items} is {@code null}.
     */
    public void EnqueueAll(T... items)
            throws OverflowException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(items, "items");
        EnqueueArray(items);
        // If it happens that the first enqueue happened through this method, the head might not be appropriately updated.
        // As such, we need to update it here ourselves.
        if (head == -1) { head = 0; }
    }

    private void EnqueueArray(T[] array)
    {
        int array_len = array.length;
        if (array_len == 0) { return; }
        // Check first that we have such space.
        // Check for enlarge measures - will be done automatically if needed for us
        EnlargeAndShift(array_len);
        // Now copy our elements and we are then done...
        // Special handling is required for tail == -1.
        Array.Copy(array, 0, elements, tail == -1 ? 0 : tail + 1, array_len);
        // Update tail...
        tail += array_len;
        // Update count...
        count += array_len;
    }

    private void EnqueueList(IList<T> list)
    {
        int ct = list.getCount();
        if (ct == 0) { return; }
        // Check first that we have such space.
        // Check for enlarge measures - will be done automatically if needed for us
        EnlargeAndShift(ct);
        // Now copy our elements and we are then done...
        // Special handling is required for tail == -1.
        int offset = (tail == -1) ? 0 : tail + 1;
        for (int I = 0; I < ct; I++) {
            elements[offset + I] = list.getItem(I);
        }
        // Update tail...
        tail += ct;
        // Update count...
        count += ct;
    }

    private void EnqueueCollection(ICollection<T> collection)
    {
        int ct = collection.getCount();
        if (ct == 0) { return; }
        // Check for enlarge measures - will be done automatically if needed for us
        EnlargeAndShift(ct);
        // Now copy our elements and we are then done...
        // Special handling is required for tail == -1.
        int I = (tail == -1) ? 0 : tail + 1;
        IEnumerator<T> e = collection.GetEnumerator();
        try {
            while (e.MoveNext()) { elements[I++] = e.getCurrent(); }
        } finally {
            e.Dispose();
        }
        // Update tail...
        tail += ct;
        // Update count...
        count += ct;
    }

    /**
     * Provides a string representation of this object. <br />
     * For debugging purposes only.
     * @return A string representation of this object.
     */
    @NotNull
    @Override
    public final String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("ArrayBasedQueue<?> (%d) { ", count));
        switch (count)
        {
            case 0:
                sb.append("<EMPTY>");
                break;
            case 1:
                sb.append(elements[head]);
                break;
            default:
                int bound = count - 1;
                for (int I = head; I < bound; I++) {
                    sb.append(elements[I]);
                    sb.append(", ");
                }
                sb.append(elements[bound]);
                break;
        }
        sb.append(" }");
        return sb.toString();
    }
}
