package com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * Implements {@link IDictionary} using a singly linked list.
 * Recommended for collections that typically include fewer than 10 items.
 */
public class ListDictionary
    implements IDictionary
{
    @AllowNull
    private DictionaryNode head; // Do not rename (binary serialization)
    private int version; // Do not rename (binary serialization)
    private int count; // Do not rename (binary serialization)
    @AllowNull
    private final IComparer comparer; // Do not rename (binary serialization)

    /**
     * Initializes a new instance of the {@link ListDictionary} class.
     */
    public ListDictionary() { this(null); }

    /**
     * Creates an empty ListDictionary using the specified comparer.
     * @param comparer The {@link IComparer} to use to determine whether two keys are equal. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code null} to use the default comparer, which is each key's implementation of Equals(Object).
     */
    public ListDictionary(@AllowNull IComparer comparer)
    {
        count = 0;
        head = null;
        version = 0;
        this.comparer = comparer;
    }

    @Override
    public int getCount() { return count; }

    @Override
    public Object getSyncRoot() { return this; }

    @Override
    public boolean GetIsReadOnly() { return false; }

    @Override
    public boolean GetIsFixedSize() { return false; }

    @Override
    public boolean getIsSynchronized() { return false; }

    @Override
    public IDictionaryEnumerator GetEnumerator() { return new NodeEnumerator(this); }

    @Override
    public ICollection GetKeys() { return new NodeKeyValueCollection(this, true); }

    @Override
    public ICollection GetValues() { return new NodeKeyValueCollection(this, false); }

    @Override
    public void Add(Object key, Object value)
            throws ArgumentNullException, ArgumentException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        version++;
        DictionaryNode last = null;

        for (DictionaryNode node = head; node != null; node = node.next)
        {
            Object oldKey = node.key;
            if ((comparer == null) ? oldKey.equals(key) : comparer.Compare(oldKey, key) == 0)
            {
                throw new ArgumentException(String.format("Key %s is already existing in the dictionary.", key));
            }
            last = node;
        }
        // Not found, so add a new one
        DictionaryNode newNode = new DictionaryNode();
        newNode.key = key;
        newNode.value = value;
        if (last != null)
        {
            last.next = newNode;
        }
        else
        {
            head = newNode;
        }
        count++;
    }

    @Override
    public void Clear()
            throws NotSupportedException
    {
        count = 0;
        head = null;
        version++;
    }

    @Override
    public boolean Contains(Object key)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        for (DictionaryNode node = head; node != null; node = node.next)
        {
            Object oldKey = node.key;
            if ((comparer == null) ? oldKey.equals(key) : comparer.Compare(oldKey, key) == 0)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void Remove(Object key)
            throws ArgumentNullException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        version++;
        DictionaryNode last = null;
        DictionaryNode node;
        for (node = head; node != null; node = node.next)
        {
            Object oldKey = node.key;
            if ((comparer == null) ? oldKey.equals(key) : comparer.Compare(oldKey, key) == 0)
            {
                break;
            }
            last = node;
        }
        if (node == null)
        {
            return;
        }
        if (node == head)
        {
            head = node.next;
        }
        else
        {
            assert last != null; // Debug.Assert(last != null);
            last.next = node.next;
        }
        count--;
    }

    @Override
    public Object GetItem(Object key)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        DictionaryNode node = head;
        if (comparer == null)
        {
            while (node != null)
            {
                Object oldKey = node.key;
                if (oldKey.equals(key))
                {
                    return node.value;
                }
                node = node.next;
            }
        }
        else
        {
            while (node != null)
            {
                Object oldKey = node.key;
                if (comparer.Compare(oldKey, key) == 0)
                {
                    return node.value;
                }
                node = node.next;
            }
        }
        return null;
    }

    @Override
    public void SetItem(Object key, Object value)
            throws ArgumentNullException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(key);

        version++;
        DictionaryNode last = null;
        DictionaryNode node;
        for (node = head; node != null; node = node.next)
        {
            Object oldKey = node.key;
            if ((comparer == null) ? oldKey.equals(key) : comparer.Compare(oldKey, key) == 0)
            {
                break;
            }
            last = node;
        }
        if (node != null)
        {
            // Found it
            node.value = value;
            return;
        }
        // Not found, so add a new one
        DictionaryNode newNode = new DictionaryNode();
        newNode.key = key;
        newNode.value = value;
        if (last != null)
        {
            last.next = newNode;
        }
        else
        {
            head = newNode;
        }
        count++;
    }

    @Override
    public void CopyTo(Object array, int index)
            throws ArgumentException
    {
        ArgumentNullException.ThrowIfNull(array, "array");

        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (java.lang.reflect.Array.getLength(array) - index < count) {
            throw new ArgumentException("The target array does not have enough space to store all of the collection's elements.");
        }

        for (DictionaryNode node = head; node != null; node = node.next)
        {
            java.lang.reflect.Array.set(array, index++, new DictionaryEntry(node.key, node.value));
        }
    }

    @TypeForwardedFrom(AssemblyFullName = "System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
    public static class DictionaryNode
    {
        public Object key = null; // Do not rename (binary serialization)
        @AllowNull
        public Object value; // Do not rename (binary serialization)
        @AllowNull
        public DictionaryNode next; // Do not rename (binary serialization)
    }

    private static final class NodeEnumerator
            implements IDictionaryEnumerator
    {
        private final ListDictionary _list;
        private DictionaryNode _current;
        private final int _version;
        private boolean _start;

        public NodeEnumerator(ListDictionary list)
        {
            _list = list;
            _version = list.version;
            _start = true;
            _current = null;
        }

        @Override
        public DictionaryEntry getCurrent() { return GetEntry(); }

        @Override
        public DictionaryEntry GetEntry()
                throws InvalidOperationException
        {
            if (_current == null)
            {
                throw new InvalidOperationException("Enumeration has not yet begun.");
            }
            return new DictionaryEntry(_current.key, _current.value);
        }

        @Override
        @MaybeNull
        public Object GetValue()
                throws InvalidOperationException
        {
            if (_current == null)
            {
                throw new InvalidOperationException("Enumeration has not yet begun.");
            }
            return _current.value;
        }

        @Override
        public Object GetKey()
                throws InvalidOperationException
        {
            if (_current == null)
            {
                throw new InvalidOperationException("Enumeration has not yet begun.");
            }
            return _current.key;
        }

        @Override
        public boolean MoveNext()
        {
            if (_version != _list.version)
            {
                throw new InvalidOperationException("The collection was modified after the enumerator was created.");
            }
            if (_start)
            {
                _current = _list.head;
                _start = false;
            }
            else if (_current != null)
            {
                _current = _current.next;
            }
            return (_current != null);
        }

        @Override
        public void Reset()
        {
            if (_version != _list.version)
            {
                throw new InvalidOperationException("The collection was modified after the enumerator was created.");
            }
            _start = true;
            _current = null;
        }
    }

    private static final class NodeKeyValueCollection
            implements ICollection
    {
        private final ListDictionary _list;
        private final boolean _isKeys;

        public NodeKeyValueCollection(ListDictionary list, boolean isKeys)
        {
            _list = list;
            _isKeys = isKeys;
        }

        @Override
        public Object getSyncRoot() { return _list.getSyncRoot(); }

        @Override
        public boolean getIsSynchronized() { return _list.getIsSynchronized(); }

        @Override
        public IEnumerator GetEnumerator() { return new NodeKeyValueEnumerator(_list, _isKeys); }

        @Override
        public int getCount()
        {
            int count = 0;
            for (DictionaryNode node = _list.head; node != null; node = node.next) { count++; }
            return count;
        }

        @Override
        public void CopyTo(Object array, int index)
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
            }

            for (DictionaryNode node = _list.head; node != null; node = node.next)
            {
                java.lang.reflect.Array.set(array, index++, _isKeys ? node.key : node.value);
            }
        }

        private static final class NodeKeyValueEnumerator
                implements IEnumerator
        {
            private final ListDictionary _list;
            private DictionaryNode _current;
            private final int _version;
            private final boolean _isKeys;
            private boolean _start;

            public NodeKeyValueEnumerator(ListDictionary list, boolean isKeys)
            {
                _list = list;
                _isKeys = isKeys;
                _version = list.version;
                _start = true;
                _current = null;
            }

            @Override
            public Object getCurrent()
            {
                if (_current == null)
                {
                    throw new InvalidOperationException("Enumeration has not yet begun.");
                }
                return _isKeys ? _current.key : _current.value;
            }

            @Override
            public boolean MoveNext()
            {
                if (_version != _list.version)
                {
                    throw new InvalidOperationException("The collection was modified after the enumerator was created.");
                }
                if (_start)
                {
                    _current = _list.head;
                    _start = false;
                }
                else if (_current != null)
                {
                    _current = _current.next;
                }
                return (_current != null);
            }

            @Override
            public void Reset()
            {
                if (_version != _list.version)
                {
                    throw new InvalidOperationException("The collection was modified after the enumerator was created.");
                }
                _start = true;
                _current = null;
            }
        }
    }

}
