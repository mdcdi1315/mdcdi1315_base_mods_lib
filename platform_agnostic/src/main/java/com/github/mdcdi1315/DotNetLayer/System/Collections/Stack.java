package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

// A simple stack of objects.  Internally it is implemented as an array,
// so Push can be O(n).  Pop is O(1).

@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class Stack
        implements ICollection, ICloneable
{
    private @AllowNull Object[] _array; // Storage for stack elements. Do not rename (binary serialization)
    private int _size; // Number of items in the stack. Do not rename (binary serialization)
    private int _version; // Used to keep enumerator in sync w/ collection. Do not rename (binary serialization)

    private static final int _defaultCapacity = 10;

    public Stack()
    {
        _array = new Object[_defaultCapacity];
        _size = 0;
        _version = 0;
    }

    // Create a stack with a specific initial capacity.  The initial capacity
    // must be a non-negative number.
    public Stack(int initialCapacity)
    {
        if (initialCapacity < 0) {
            throw new ArgumentOutOfRangeException("initialCapacity", "Capacity cannot be a negative value.");
        }

        if (initialCapacity < _defaultCapacity)
            initialCapacity = _defaultCapacity;  // Simplify doubling logic in Push.
        _array = new Object[initialCapacity];
        _size = 0;
        _version = 0;
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

    // Fills a Stack with the contents of a particular collection.  The items are
    // pushed onto the stack in the same order they are read by the enumerator.
    //
    public Stack(ICollection col)
    {
        this(GetCountSafe(col));
        IEnumerator en = col.GetEnumerator();
        while (en.MoveNext())
            Push(en.getCurrent());
    }

    @Override
    public int getCount() { return _size; }

    @Override
    public Object getSyncRoot() { return this; }

    @Override
    public boolean getIsSynchronized() { return false; }

    // Returns an IEnumerator for this Stack.
    @Override
    public IEnumerator GetEnumerator() { return new StackEnumerator(this); }

    // Removes all Objects from the Stack.
    public void Clear()
    {
        Array.Clear(_array, 0, _size); // Don't need to doc this but we clear the elements so that the gc can reclaim the references.
        _size = 0;
        _version++;
    }

    @Override
    public Object Clone()
    {
        Stack s = new Stack(_size);
        s._size = _size;
        Array.Copy(_array, s._array, _size);
        s._version = _version;
        return s;
    }

    public boolean Contains(@AllowNull Object obj)
    {
        int count = _size;

        while (count-- > 0)
        {
            if (obj == null)
            {
                if (_array[count] == null)
                    return true;
            }
            else if (_array[count] != null && _array[count].equals(obj))
            {
                return true;
            }
        }
        return false;
    }

    // Copies the stack into an array.
    public void CopyTo(Object array, int index)
    {
        ArgumentNullException.ThrowIfNull(array, "array");

        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        }

        int arrayLen = java.lang.reflect.Array.getLength(array);
        if (arrayLen - index < _size)
            throw new ArgumentException("The given array does not have enough space so that all the list's elements can be copied to it.");

        int i = 0;
        if (array instanceof Object[] objArray)
        {
            while (i < _size)
            {
                objArray[i + index] = _array[_size - i - 1];
                i++;
            }
        }
        else
        {
            while (i < _size)
            {
                java.lang.reflect.Array.set(array, i + index, _array[_size - i - 1]);
                i++;
            }
        }
    }

    // Returns the top Object on the stack without removing it.  If the stack
    // is empty, Peek throws an InvalidOperationException.
    @MaybeNull
    public Object Peek()
    {
        if (_size == 0)
            throw new InvalidOperationException("Empty stack.");

        return _array[_size - 1];
    }

    // Pops an item from the top of the stack.  If the stack is empty, Pop
    // throws an InvalidOperationException.
    @MaybeNull
    public Object Pop()
    {
        if (_size == 0)
            throw new InvalidOperationException("Empty stack.");

        _version++;
        @AllowNull Object obj = _array[--_size];
        _array[_size] = null;     // Free memory quicker.
        return obj;
    }

    // Pushes an item to the top of the stack.
    //
    public void Push(@AllowNull Object obj)
    {
        if (_size == _array.length)
        {
            Object[] newArray = new Object[2 * _array.length];
            Array.Copy(_array, newArray, _size);
            _array = newArray;
        }
        _array[_size++] = obj;
        _version++;
    }

    // Returns a synchronized Stack.
    //
    @NotNull
    public static Stack Synchronized(Stack stack)
    {
        ArgumentNullException.ThrowIfNull(stack, "stack");

        return new SyncStack(stack);
    }


    // Copies the Stack to an array, in the same order Pop would return the items.
    public Object[] ToArray()
    {
        if (_size == 0)
            return new Object[0];

        @AllowNull Object[] objArray = new Object[_size];
        int i = 0;
        while (i < _size)
        {
            objArray[i] = _array[_size - i - 1];
            i++;
        }
        return objArray;
    }

    private static final class SyncStack
            extends Stack
    {
        private final Stack _s;
        private final Object _root;

        public SyncStack(Stack stack)
        {
            super();
            _s = stack;
            _root = stack.getSyncRoot();
        }

        @Override
        public Object getSyncRoot() { return _root; }

        @Override
        public boolean getIsSynchronized() { return true; }

        @Override
        public void Clear() { synchronized (_root) { _s.Clear(); } }

        @Override
        @MaybeNull
        public Object Pop() { synchronized (_root) { return _s.Pop(); } }

        @Override
        @MaybeNull
        public Object Peek() { synchronized (_root) { return _s.Peek(); } }

        @Override
        public int getCount() { synchronized (_root) { return _s.getCount(); } }

        @Override
        public Object[] ToArray() { synchronized (_root) { return _s.ToArray(); } }

        @Override
        public void Push(@AllowNull Object value) { synchronized (_root) { _s.Push(value); } }

        @Override
        public IEnumerator GetEnumerator() { synchronized (_root) { return _s.GetEnumerator(); } }

        @Override
        public Object Clone() { synchronized (_root) { return new SyncStack((Stack)_s.Clone()); } }

        @Override
        public boolean Contains(@AllowNull Object obj) { synchronized (_root) { return _s.Contains(obj); } }

        @Override
        public void CopyTo(Object array, int arrayIndex) { synchronized (_root) { _s.CopyTo(array, arrayIndex); } }
    }

    private static final class StackEnumerator
            implements IEnumerator, ICloneable
    {
        private final Stack _stack;
        private int _index;
        private final int _version;
        private @AllowNull Object _currentElement;

        private StackEnumerator(Stack stack)
        {
            _stack = stack;
            _version = _stack._version;
            _index = -2;
            _currentElement = null;
        }

        @Override
        public Object Clone() { return new StackEnumerator(_stack); }

        @Override
        public boolean MoveNext()
        {
            boolean retval;
            if (_version != _stack._version) throw new InvalidOperationException("The collection was modified after the enumerator was created.");
            if (_index == -2)
            {  // First call to enumerator.
                _index = _stack._size - 1;
                retval = (_index >= 0);
                if (retval)
                    _currentElement = _stack._array[_index];
                return retval;
            }
            if (_index == -1)
            {  // End of enumeration.
                return false;
            }

            retval = (--_index >= 0);
            if (retval)
                _currentElement = _stack._array[_index];
            else
                _currentElement = null;
            return retval;
        }

        @Override
        @MaybeNull
        public Object getCurrent()
        {
            if (_index == -2) throw new InvalidOperationException("The enumeration has not yet begun.");
            if (_index == -1) throw new InvalidOperationException("The enumeration has ended.");
            return _currentElement;
        }

        @Override
        public void Reset()
        {
            if (_version != _stack._version) throw new InvalidOperationException("The collection was modified after the enumerator was created.");
            _index = -2;
            _currentElement = null;
        }
    }
    
}