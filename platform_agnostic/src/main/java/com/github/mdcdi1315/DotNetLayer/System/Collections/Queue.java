package com.github.mdcdi1315.DotNetLayer.System.Collections;

// A simple Queue of Objects.  Internally it is implemented as a circular
// buffer, so Enqueue can be O(n).  Dequeue is O(1).

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class Queue
        implements ICollection, ICloneable
{
    private @AllowNull Object[] _array; // Do not rename (binary serialization)
    private int _head; // First valid element in the queue. Do not rename (binary serialization)
    private int _tail; // Last valid element in the queue. Do not rename (binary serialization)
    private int _size; // Number of elements. Do not rename (binary serialization)
    private final int _growFactor; // 100 == 1.0, 130 == 1.3, 200 == 2.0. Do not rename (binary serialization)
    private int _version; // Do not rename (binary serialization)

    private static final int MinimumGrow = 4;

    // Creates a queue with room for capacity Objects. The default initial
    // capacity and grow factor are used.
    public Queue()
    {
        this(32, 2.0f);
    }

    // Creates a queue with room for capacity Objects. The default grow factor
    // is used.
    //
    public Queue(int capacity)
    {
        this(capacity, 2.0f);
    }

    // Creates a queue with room for capacity Objects. When full, the new
    // capacity is set to the old capacity * growFactor.
    //
    public Queue(int capacity, float growFactor)
    {
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative value.");
        } else if (growFactor < 1.0f) {
            throw new ArgumentOutOfRangeException("growFactor", "Grow factor cannot be less than 1.");
        } else if (growFactor > 10.0f) {
            throw new ArgumentOutOfRangeException("growFactor", "Grow factor cannot be greater than 10.");
        } else {
            _array = new Object[capacity];
            _head = 0;
            _tail = 0;
            _size = 0;
            _growFactor = (int)(growFactor * 100);
        }
    }
    
    @StackTraceHidden
    private static int GetCountSafe(ICollection collection)
    {
        if (collection == null) {
            throw new ArgumentNullException("col");
        } else {
            return collection.getCount();
        }
    }

    // Fills a Queue with the elements of an ICollection.  Uses the enumerator
    // to get each of the elements.
    //
    public Queue(ICollection col)
    {
        this(GetCountSafe(col));
        IEnumerator en = col.GetEnumerator();
        while (en.MoveNext())
            Enqueue(en.getCurrent());
    }

    @Override
    public int getCount() { return _size; }

    public Object Clone()
    {
        Queue q = new Queue(_size);
        q._size = _size;

        int numToCopy = _size;
        int firstPart = Math.min(_array.length - _head, numToCopy);
        Array.Copy(_array, _head, q._array, 0, firstPart);
        numToCopy -= firstPart;
        if (numToCopy > 0)
            Array.Copy(_array, 0, q._array, _array.length - _head, numToCopy);

        q._version = _version;
        return q;
    }

    @Override
    public boolean getIsSynchronized() { return false; }

    @Override
    public Object getSyncRoot() { return this; }

    // Removes all Objects from the queue.
    public void Clear()
    {
        if (_size != 0)
        {
            if (_head < _tail)
                Array.Clear(_array, _head, _size);
            else
            {
                Array.Clear(_array, _head, _array.length - _head);
                Array.Clear(_array, 0, _tail);
            }

            _size = 0;
        }

        _head = 0;
        _tail = 0;
        _version++;
    }

    // CopyTo copies a collection into an Array, starting at a particular
    // index into the array.
    //
    public void CopyTo(Object array, int index)
    {
        ArgumentNullException.ThrowIfNull(array, "array");

        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        }

        int arrayLen = java.lang.reflect.Array.getLength(array);
        if (arrayLen - index < _size)
            throw new ArgumentException("The given array does not have enough space so that all the list's elements can be copied to it.");

        int numToCopy = _size;
        if (numToCopy == 0)
            return;
        int firstPart = Math.min(arrayLen - _head, numToCopy);
        Array.Copy(_array, _head, (Object[])array, index, firstPart);
        numToCopy -= firstPart;
        if (numToCopy > 0)
            Array.Copy(_array, 0, (Object[])array, index + arrayLen - _head, numToCopy);
    }

    // Adds obj to the tail of the queue.
    //
    public void Enqueue(@AllowNull Object obj)
    {
        if (_size == _array.length)
        {
            int newcapacity = (int)((long)_array.length * (long)_growFactor / 100);
            if (newcapacity < _array.length + MinimumGrow)
            {
                newcapacity = _array.length + MinimumGrow;
            }
            SetCapacity(newcapacity);
        }

        _array[_tail] = obj;
        _tail = (_tail + 1) % _array.length;
        _size++;
        _version++;
    }

    // GetEnumerator returns an IEnumerator over this Queue.  This
    // Enumerator will support removing.
    //
    @Override
    public IEnumerator GetEnumerator() { return new QueueEnumerator(this); }

    // Removes the Object at the head of the queue and returns it. If the queue
    // is empty, this method throws an InvalidOperationException.
    public @AllowNull Object Dequeue()
    {
        if (_size == 0)
            throw new InvalidOperationException("Empty queue.");

        @AllowNull Object removed = _array[_head];
        _array[_head] = null;
        _head = (_head + 1) % _array.length;
        _size--;
        _version++;
        return removed;
    }

    // Returns the Object at the head of the queue. The Object remains in the
    // queue. If the queue is empty, this method throws an
    // InvalidOperationException.
    public @AllowNull Object Peek()
    {
        if (_size == 0)
            throw new InvalidOperationException("Empty queue.");

        return _array[_head];
    }

    // Returns a synchronized Queue.  Returns a synchronized wrapper
    // class around the queue - the caller must not use references to the
    // original queue.
    //
    @NotNull
    public static Queue Synchronized(Queue queue)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(queue, "queue");

        return new SynchronizedQueue(queue);
    }

    // Returns true if the queue contains at least one Object equal to obj.
    // Equality is determined using obj.Equals().
    //
    public boolean Contains(@AllowNull Object obj)
    {
        int index = _head;
        int count = _size;

        while (count-- > 0)
        {
            if (obj == null) {
                if (_array[index] == null)
                    return true;
            } else if (_array[index] != null && _array[index].equals(obj)) {
                return true;
            }
            index = (index + 1) % _array.length;
        }

        return false;
    }

    private Object GetElement(int i) { return _array[(_head + i) % _array.length]; }

    // Iterates over the Objects in the queue, returning an array of the
    // Objects in the Queue, or an empty array if the queue is empty.
    // The order of elements in the array is first in to last in, the same
    // order produced by successive calls to Dequeue.
    @NotNull
    public Object[] ToArray()
    {
        if (_size == 0)
            return new Object[0];

        Object[] arr = new Object[_size];
        if (_head < _tail)
        {
            Array.Copy(_array, _head, arr, 0, _size);
        }
        else
        {
            Array.Copy(_array, _head, arr, 0, _array.length - _head);
            Array.Copy(_array, 0, arr, _array.length - _head, _tail);
        }

        return arr;
    }

    // PRIVATE Grows or shrinks the buffer to hold capacity Objects. Capacity
    // must be >= _size.
    private void SetCapacity(int capacity)
    {
        Object[] newarray = new Object[capacity];
        if (_size > 0)
        {
            if (_head < _tail)
            {
                Array.Copy(_array, _head, newarray, 0, _size);
            }
            else
            {
                Array.Copy(_array, _head, newarray, 0, _array.length - _head);
                Array.Copy(_array, 0, newarray, _array.length - _head, _tail);
            }
        }

        _array = newarray;
        _head = 0;
        _tail = (_size == capacity) ? 0 : _size;
        _version++;
    }

    public void TrimToSize() { SetCapacity(_size); }

    // Implements a synchronization wrapper around a queue.
    private static final class SynchronizedQueue
            extends Queue
    {
        private final Queue _q;
        private final Object _root;

        public SynchronizedQueue(Queue q)
        {
            super();
            _q = q;
            _root = _q.getSyncRoot();
        }

        @Override
        public Object getSyncRoot() { return _root; }

        @Override
        public boolean getIsSynchronized() { return true; }

        @Override
        public void Clear() { synchronized (_root) { _q.Clear(); } }

        @Override
        @MaybeNull
        public Object Peek() { synchronized (_root) { return _q.Peek(); } }

        @Override
        public void TrimToSize() { synchronized (_root) { _q.TrimToSize(); } }

        @Override
        public int getCount() { synchronized (_root) { return _q.getCount(); } }

        @Override
        @MaybeNull
        public Object Dequeue() { synchronized (_root) { return _q.Dequeue(); } }

        @Override
        public Object[] ToArray() { synchronized (_root) { return _q.ToArray(); } }

        @NotNull
        @Override
        public IEnumerator GetEnumerator() { synchronized (_root) { return _q.GetEnumerator(); } }

        @Override
        public void Enqueue(@AllowNull Object value) { synchronized (_root) { _q.Enqueue(value); } }

        @Override
        public Object Clone() { synchronized (_root) { return new SynchronizedQueue((Queue)_q.Clone()); } }

        @Override
        public boolean Contains(@AllowNull Object obj) { synchronized (_root) { return _q.Contains(obj); } }

        @Override
        public void CopyTo(Object array, int arrayIndex) { synchronized (_root) { _q.CopyTo(array, arrayIndex); } }
    }

    // Implements an enumerator for a Queue.  The enumerator uses the
    // internal version number of the list to ensure that no modifications are
    // made to the list while an enumeration is in progress.
    private static final class QueueEnumerator
            implements IEnumerator, ICloneable
    {
        private final Queue _q;
        private int _index;
        private final int _version;
        private @AllowNull Object _currentElement;

        public QueueEnumerator(Queue q)
        {
            _q = q;
            _version = _q._version;
            _index = 0;
            _currentElement = _q._array;
            if (_q._size == 0)
                _index = -1;
        }

        @Override
        public Object Clone() { return new QueueEnumerator(_q); }

        @Override
        public boolean MoveNext()
        {
            if (_version != _q._version)
                throw new InvalidOperationException("The collection was modified after the enumerator was created.");

            if (_index < 0)
            {
                _currentElement = _q._array;
                return false;
            }

            _currentElement = _q.GetElement(_index);
            _index++;

            if (_index == _q._size)
                _index = -1;
            return true;
        }

        @Override
        @MaybeNull
        public Object getCurrent()
        {
            if (_currentElement == _q._array) {
                if (_index == 0)
                    throw new InvalidOperationException("The enumeration has not yet begun.");
                else
                    throw new InvalidOperationException("The enumeration has ended.");
            } else {
                return _currentElement;
            }
        }

        @Override
        public void Reset()
        {
            if (_version != _q._version)
                throw new InvalidOperationException("The collection was modified after the enumerator was created.");
            if (_q._size == 0)
                _index = -1;
            else
                _index = 0;
            _currentElement = _q._array;
        }
    }
}
