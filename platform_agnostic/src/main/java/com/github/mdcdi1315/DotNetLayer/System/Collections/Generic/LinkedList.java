package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.JavaObjectEqualsEqualityComparer;

/**
 * Represents a doubly linked list.
 * @param <T> Specifies the element type of the linked list.
 */
public class LinkedList<T>
    implements ICollection<T>, IReadOnlyCollection<T>
{
    // This LinkedList is a doubly-Linked circular list.
    @AllowNull
    LinkedListNode<T> head;
    int count;
    int version;

    /**
     * Initializes a new instance of the {@link LinkedList} class that is empty.
     */
    public LinkedList()
    {
    }

    /**
     * Initializes a new instance of the {@link LinkedList} class that contains elements copied from
     * the specified {@link IEnumerable} and has sufficient capacity to accommodate the number of elements copied.
     * @param collection The {@link IEnumerable} whose elements are copied to the new {@link LinkedList}.
     * @throws ArgumentNullException {@code collection} is {@code null}.
     */
    public LinkedList(IEnumerable<T> collection)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(collection, "collection");

        IEnumerator<T> e = collection.GetEnumerator();
        try {
            while (e.MoveNext()) { AddLast(e.getCurrent()); }
        } finally {
            e.Dispose();
        }
        /*
        foreach (T item in collection)
        {
            AddLast(item);
        }
         */
    }

    @Override
    public int getCount() { return count; }

    @Override
    public boolean getIsReadOnly() { return false; }

    /**
     * Gets the first node of the {@link LinkedList}.
     * @return The first {@link LinkedListNode} of the {@link LinkedList}.
     */
    @MaybeNull
    public LinkedListNode<T> GetFirst() { return head; }

    @Override
    public void Add(@AllowNull T item) { AddLast(item); }

    /**
     * Determines whether a value is in the {@link LinkedList}.
     * @param value The value to locate in the {@link LinkedList}. The value can be {@code null} for reference types.
     * @return {@code true} if {@code value} is found in the {@link LinkedList}; otherwise, {@code false}.
     */
    @Override
    public boolean Contains(@AllowNull T value) { return Find(value) != null; }

    @Override
    public Enumerator<T> GetEnumerator() { return new Enumerator<>(this); }

    /**
     * Gets the last node of the {@link LinkedList}.
     * @return The last {@link LinkedListNode} of the {@link LinkedList}.
     */
    @MaybeNull
    public LinkedListNode<T> GetLast() { return head == null ? null : head.prev; }

    /**
     * Adds a new node containing the specified value after the specified existing node in the {@link LinkedList}.
     * @param node The {@link LinkedListNode} after which to insert a new {@link LinkedListNode} containing {@code value}.
     * @param value The value to add to the {@link LinkedList}.
     * @return The new {@link LinkedListNode} containing {@code value}.
     * @throws ArgumentNullException {@code node} is {@code null}.
     * @throws InvalidOperationException {@code node} is not in the current {@link LinkedList}.
     */
    @NotNull
    public LinkedListNode<T> AddAfter(LinkedListNode<T> node, T value)
        throws ArgumentNullException, InvalidOperationException
    {
        ValidateNode(node);
        LinkedListNode<T> result = new LinkedListNode<T>(node.list, value);
        InternalInsertNodeBefore(node.next, result);
        return result;
    }

    /**
     * Adds the specified new node after the specified existing node in the {@link LinkedList}.
     * @param node The {@link LinkedListNode} after which to insert newNode.
     * @param newNode The new {@link LinkedListNode} to add to the {@link LinkedList}.
     * @throws ArgumentNullException {@code node} and/or {@code newNode} are {@code null}.
     * @throws InvalidOperationException {@code node} is not in the current {@link LinkedList}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code newNode} belongs to another {@link LinkedList}.
     */
    public void AddAfter(LinkedListNode<T> node, LinkedListNode<T> newNode)
            throws ArgumentNullException, InvalidOperationException
    {
        ValidateNode(node);
        ValidateNewNode(newNode);
        InternalInsertNodeBefore(node.next, newNode);
        newNode.list = this;
    }

    /**
     * Adds a new node containing the specified value before the specified existing node in the {@link LinkedList}.
     * @param node The {@link LinkedListNode} before which to insert a new {@link LinkedListNode} containing {@code value}.
     * @param value The value to add to the {@link LinkedList}.
     * @return The new {@link LinkedListNode} containing {@code value}.
     * @throws ArgumentNullException {@code node} is {@code null}.
     * @throws InvalidOperationException {@code node} is not in the current {@link LinkedList}.
     */
    @NotNull
    public LinkedListNode<T> AddBefore(LinkedListNode<T> node, @AllowNull T value)
            throws ArgumentNullException, InvalidOperationException
    {
        ValidateNode(node);
        LinkedListNode<T> result = new LinkedListNode<>(node.list, value);
        InternalInsertNodeBefore(node, result);
        if (node == head) { head = result; }
        return result;
    }

    /**
     * Adds the specified new node before the specified existing node in the {@link LinkedList}.
     * @param node The {@link LinkedListNode} before which to insert {@code newNode}.
     * @param newNode The new {@link LinkedListNode} to add to the {@link LinkedList}.
     * @throws ArgumentNullException {@code node} and/or {@code newNode} are {@code null}.
     * @throws InvalidOperationException {@code node} is not in the current {@link LinkedList}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code newNode} belongs to another {@link LinkedList}.
     */
    public void AddBefore(LinkedListNode<T> node, LinkedListNode<T> newNode)
            throws ArgumentNullException, InvalidOperationException
    {
        ValidateNode(node);
        ValidateNewNode(newNode);
        InternalInsertNodeBefore(node, newNode);
        newNode.list = this;
        if (node == head)
        {
            head = newNode;
        }
    }

    /**
     * Adds a new node containing the specified value at the start of the {@link LinkedList}.
     * @param value The value to add at the start of the {@link LinkedList}.
     * @return The new {@link LinkedListNode} containing value.
     */
    @NotNull
    public LinkedListNode<T> AddFirst(@AllowNull T value)
    {
        LinkedListNode<T> result = new LinkedListNode<>(this, value);
        if (head == null) {
            InternalInsertNodeToEmptyList(result);
        } else {
            InternalInsertNodeBefore(head, result);
            head = result;
        }
        return result;
    }

    /**
     * Adds the specified new node at the start of the {@link LinkedList}.
     * @param node The new {@link LinkedListNode} to add at the start of the {@link LinkedList}.
     * @throws ArgumentNullException {@code node} is {@code null}.
     * @throws InvalidOperationException {@code node} belongs to another {@link LinkedList}.
     */
    public void AddFirst(LinkedListNode<T> node)
            throws ArgumentNullException, InvalidOperationException
    {
        ValidateNewNode(node);

        if (head == null)
        {
            InternalInsertNodeToEmptyList(node);
        }
        else
        {
            InternalInsertNodeBefore(head, node);
            head = node;
        }
        node.list = this;
    }

    /**
     * Adds a new node containing the specified value at the end of the {@link LinkedList}.
     * @param value The value to add at the end of the {@link LinkedList}.
     * @return The new {@link LinkedListNode} containing {@code value}.
     */
    @NotNull
    public LinkedListNode<T> AddLast(@AllowNull T value)
    {
        LinkedListNode<T> result = new LinkedListNode<>(this, value);
        if (head == null) {
            InternalInsertNodeToEmptyList(result);
        } else {
            InternalInsertNodeBefore(head, result);
        }
        return result;
    }

    /**
     * Adds the specified new node at the end of the {@link LinkedList}.
     * @param node The new {@link LinkedListNode} to add at the end of the {@link LinkedList}.
     * @throws ArgumentNullException {@code node} is {@code null}.
     * @throws InvalidOperationException {@code node} belongs to another {@link LinkedList}.
     */
    public void AddLast(LinkedListNode<T> node)
            throws ArgumentNullException, InvalidOperationException
    {
        ValidateNewNode(node);

        if (head == null) {
            InternalInsertNodeToEmptyList(node);
        } else {
            InternalInsertNodeBefore(head, node);
        }
        node.list = this;
    }

    /**
     * Removes all nodes from the {@link LinkedList}.
     */
    public void Clear()
    {
        LinkedListNode<T> current = head;
        while (current != null)
        {
            LinkedListNode<T> temp = current;
            current = current.GetNext();
            temp.Invalidate();
        }

        head = null;
        count = 0;
        version++;
    }

    /**
     * Copies the entire {@link LinkedList} to a compatible one-dimensional {@link java.lang.reflect.Array}, starting at the specified index of the target array.
     * @param array The one-dimensional {@link java.lang.reflect.Array} that is the destination of the elements copied from {@link LinkedList}. The {@link java.lang.reflect.Array} must have zero-based indexing.
     * @param index The zero-based index in {@code array} at which copying begins.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws ArgumentException {@code index} is less than 0.
     * @throws ArgumentOutOfRangeException The number of elements in the source {@link LinkedList} is greater than the available space from {@code index} to the end of the destination {@code array}.
     */
    public void CopyTo(T[] array, int index)
            throws ArgumentNullException, ArgumentException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(array, "array");

        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        }

        if (index > array.length)
        {
            throw new ArgumentOutOfRangeException("index", index, "Array index must be less or equal than the array's length.");
        }

        if (array.length - index < count)
        {
            throw new ArgumentException("Invalid offset and length.");
        }

        LinkedListNode<T> node = head;
        if (node != null)
        {
            do {
                array[index++] = node.GetValue();
                node = node.next;
            } while (node != head);
        }
    }

    /**
     * Finds the first node that contains the specified value.
     * @param value The value to locate in the {@link LinkedList}.
     * @return The first {@link LinkedListNode} that contains the specified value, if found; otherwise, {@code null}.
     */
    @MaybeNull
    public LinkedListNode<T> Find(@AllowNull T value)
    {
        LinkedListNode<T> node = head;
        IEqualityComparer<T> c = new JavaObjectEqualsEqualityComparer<>(); // EqualityComparer<T>.Default;
        if (node != null)
        {
            if (value != null) {
                do {
                    if (c.Equals(node.GetValue(), value)) { return node; }
                    node = node.next;
                } while (node != head);
            } else {
                do {
                    if (node.GetValue() == null) { return node; }
                    node = node.next;
                } while (node != head);
            }
        }
        return null;
    }

    /**
     * Finds the last node that contains the specified value.
     * @param value The value to locate in the {@link LinkedList}.
     * @return The last {@link LinkedListNode} that contains the specified value, if found; otherwise, {@code null}.
     */
    @MaybeNull
    public LinkedListNode<T> FindLast(@AllowNull T value)
    {
        if (head == null) { return null; }

        LinkedListNode<T> last = head.prev;
        LinkedListNode<T> node = last;
        IEqualityComparer<T> c = new JavaObjectEqualsEqualityComparer<>(); // EqualityComparer<T>.Default;
        if (node != null)
        {
            if (value != null) {
                do {
                    if (c.Equals(node.GetValue(), value)) { return node; }
                    node = node.prev;
                } while (node != last);
            } else {
                do {
                    if (node.GetValue() == null) { return node; }
                    node = node.prev;
                } while (node != last);
            }
        }
        return null;
    }

    /**
     * Removes the first occurrence of the specified value from the {@link LinkedList}.
     * @param value The value to remove from the {@link LinkedList}.
     * @return {@code true} if the element containing {@code value} is successfully removed; otherwise, {@code false}. This method also returns {@code false} if {@code value} was not found in the original {@link LinkedList}.
     * @implNote The default implementation calls in the {@link #Find(Object)} method to retrieve the node, then an internal procedure deletes the node.
     */
    public boolean Remove(@AllowNull T value)
    {
        LinkedListNode<T> node = Find(value);
        if (node == null) {
            return false;
        } else {
            InternalRemoveNode(node);
            return true;
        }
    }

    /**
     * Removes the specified node from the {@link LinkedList}.
     * @param node The {@link LinkedListNode} to remove from the {@link LinkedList}.
     * @throws ArgumentNullException {@code node} is {@code null}.
     * @throws InvalidOperationException {@code node} is not in the current {@link LinkedList}.
     */
    public void Remove(LinkedListNode<T> node)
            throws ArgumentNullException, InvalidOperationException
    {
        ValidateNode(node);
        InternalRemoveNode(node);
    }

    /**
     * Removes the node at the start of the {@link LinkedList}.
     * @throws InvalidOperationException The {@link LinkedList} is empty.
     */
    public void RemoveFirst()
            throws InvalidOperationException
    {
        if (head == null) { throw new InvalidOperationException("The linked list is empty."); }
        InternalRemoveNode(head);
    }

    /**
     * Removes the node at the end of the {@link LinkedList}.
     * @throws InvalidOperationException The {@link LinkedList} is empty.
     */
    public void RemoveLast()
            throws InvalidOperationException
    {
        if (head == null) { throw new InvalidOperationException("The linked list is empty."); }
        InternalRemoveNode(head.prev);
    }

    private void InternalInsertNodeBefore(LinkedListNode<T> node, LinkedListNode<T> newNode)
    {
        newNode.next = node;
        newNode.prev = node.prev;
        if (node.prev != null) { node.prev.next = newNode; } // node.prev!.next = newNode;
        node.prev = newNode;
        version++;
        count++;
    }

    private void InternalInsertNodeToEmptyList(LinkedListNode<T> newNode)
    {
        // Debug.Assert(head == null && count == 0, "LinkedList must be empty when this method is called!");
        newNode.next = newNode;
        newNode.prev = newNode;
        head = newNode;
        version++;
        count++;
    }

    private void InternalRemoveNode(LinkedListNode<T> node)
    {
        // Debug.Assert(node.list == this, "Deleting the node from another list!");
        // Debug.Assert(head != null, "This method shouldn't be called on empty list!");
        if (node.next == node) {
            // Debug.Assert(count == 1 && head == node, "this should only be true for a list with only one node");
            head = null;
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
            if (head == node) { head = node.next; }
        }
        node.Invalidate();
        count--;
        version++;
    }

    private static <T> void ValidateNewNode(LinkedListNode<T> node)
    {
        ArgumentNullException.ThrowIfNull(node);

        if (node.list != null)
        {
            throw new InvalidOperationException("The specified node is already attached to a linked list.");
        }
    }

    private void ValidateNode(LinkedListNode<T> node)
    {
        ArgumentNullException.ThrowIfNull(node, "node");

        if (node.list != this)
        {
            throw new InvalidOperationException("The specified node does not belong to this LinkedList instance.");
        }
    }

    /**
     * Enumerates the elements of a {@link LinkedList}.
     */
    @ClassIsDotNetStruct
    public static final class Enumerator<T>
        extends ValueType
        implements IEnumerator<T>
    {
        private final LinkedList<T> _list;
        @AllowNull
        private LinkedListNode<T> _node;
        private final int _version;
        @AllowNull
        private T _current;

        private Enumerator(LinkedList<T> list)
        {
            _list = list;
            _version = list.version;
            _node = list.head;
            _current = null;
        }

        @Override
        @MaybeNull
        public T getCurrent() { return _current; }

        /*
        public T Current =>_current !;

        object ? IEnumerator.Current
        {
            get
            {
                if (_index == 0 || (_index == _list.Count + 1)) {
                    throw new InvalidOperationException(SR.InvalidOperation_EnumOpCantHappen);
                }

                return Current;
            }
        }*/

        public boolean MoveNext()
        {
            if (_version != _list.version) {
                throw new InvalidOperationException("Enumeration failed because the collection is modified.");
            } else if (_node == null) {
                return false;
            } else {
                _current = _node.GetValue();
                _node = _node.next;
                if (_node == _list.head) {
                    _node = null;
                }
                return true;
            }
        }

        public void Reset()
        {
            if (_version != _list.version) {
                throw new InvalidOperationException("Enumeration failed because the collection is modified.");
            } else {
                _current = null;
                _node = _list.head;
            }
        }

        public void Dispose()
        {
        }
    }

}
