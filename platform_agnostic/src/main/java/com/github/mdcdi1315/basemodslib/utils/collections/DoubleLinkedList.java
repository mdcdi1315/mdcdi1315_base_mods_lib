package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;
import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;
import com.github.mdcdi1315.basemodslib.utils.JavaObjectEqualsEqualityComparer;
import com.github.mdcdi1315.basemodslib.utils.collections.linkednodes.NodeWithNextAndPrevPointer;
import com.github.mdcdi1315.basemodslib.utils.collections.linkednodes.NodeWithNextAndPrevPointerEnumerator;

/**
 * Provides a default implementation of a double-linked list.
 * @param <T> The type of the elements to be retained by the double-linked list.
 * @since 1.0.31
 */
public class DoubleLinkedList<T>
    extends BaseEnumerable<T>
    implements IDoubleLinkedList<T>, ISupportsCloning<T>
{
    private int count;
    @NotNull
    private final IEqualityComparer<T> comparer;
    private NodeWithNextAndPrevPointer<T> root, current;

    /**
     * Initializes a new and empty instance of the {@link DoubleLinkedList} class, using the default equality comparer for comparing elements.
     */
    public DoubleLinkedList()
    {
        count = 0;
        root = current = null;
        comparer = new JavaObjectEqualsEqualityComparer<>();
    }

    /**
     * Initializes a new and empty instance of the {@link DoubleLinkedList} class,
     * that will use the specified equality comparer for determining equality of its objects
     * during search operations.
     * @param comparer The {@link IEqualityComparer} to use. Can be {@code null}.
     */
    public DoubleLinkedList(@AllowNull IEqualityComparer<T> comparer)
    {
        count = 0;
        root = current = null;
        this.comparer = comparer == null ? new JavaObjectEqualsEqualityComparer<>() : comparer;
    }

    /**
     * Creates an empty thread-safe double-linked list, using the default equality comparer for comparing elements.
     * @return An object extending the {@link DoubleLinkedList} class and is thread-safe.
     * @see DoubleLinkedList#DoubleLinkedList()
     */
    public static <T> DoubleLinkedList<T> CreateSynchronized() { return new Synchronized<>(); }

    /**
     * Creates an empty thread-safe double-linked list, using the specified equality comparer for comparing elements.
     * @param comparer The {@link IEqualityComparer} to use for comparing elements. Can be {@code null}.
     * @return An object extending the {@link DoubleLinkedList} class and is thread-safe.
     * @see DoubleLinkedList#DoubleLinkedList(IEqualityComparer)
     */
    public static <T> DoubleLinkedList<T> CreateSynchronized(@AllowNull IEqualityComparer<T> comparer) { return new Synchronized<>(comparer); }

    private static final class Synchronized<T>
        extends DoubleLinkedList<T>
        implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized()
        {
            super();
            this.lock = new Object();
        }

        public Synchronized(IEqualityComparer<T> comparer)
        {
            super(comparer);
            this.lock = new Object();
        }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public void Clear() { synchronized (lock) { super.Clear(); } }

        @Override
        public void Add(T item) { synchronized (lock) { super.Add(item); } }

        @Override
        public int getCount() { synchronized (lock) { return super.getCount(); } }

        @Override
        public int GetCount() { synchronized (lock) { return super.GetCount(); } }

        @Override
        public void RemoveAt(int index) { synchronized (lock) { super.RemoveAt(index); } }

        @Override
        public int IndexOf(T item) { synchronized (lock) { return super.IndexOf(item); } }

        @Override
        public boolean Remove(T item) { synchronized (lock) { return super.Remove(item); } }

        @Override
        public T getItem(int index) { synchronized (lock) { return super.getItem(index); } }

        @Override
        public boolean getIsReadOnly() { synchronized (lock) { return super.getIsReadOnly(); } }

        @Override
        public boolean Contains(T item) { synchronized (lock) { return super.Contains(item); } }

        @Override
        public void Insert(int index, T item) { synchronized (lock) { super.Insert(index, item); } }

        @Override
        public void setItem(int index, T item) { synchronized (lock) { super.setItem(index, item); } }

        @Override
        public IEnumerator<T> GetEnumerator() { synchronized (lock) { return super.GetEnumerator(); } }

        @Override
        public void CopyTo(T[] array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public T GetItem(int index) throws ArgumentOutOfRangeException { synchronized (lock) { return super.GetItem(index); } }

        @Override
        public DoublyLinkedListNodeReference<T> GetNode(int index) throws ArgumentOutOfRangeException { synchronized (lock) { return super.GetNode(index); } }

        @Override
        public void ApplyNode(DoublyLinkedListNodeReference<T> node) throws ArgumentNullException, InvalidDoublyLinkedListNodeApplicationException { synchronized (lock) { super.ApplyNode(node); } }

        @Override
        public DoubleLinkedList<T> Clone() { synchronized (lock) { return super.Clone(); } }

        @Override
        public DoubleLinkedList<T> Slice(int count) throws ArgumentException { synchronized (lock) { return super.Slice(count); } }

        @Override
        public DoubleLinkedList<T> Slice(int index, int count) throws ArgumentException { synchronized (lock) { return super.Slice(index, count); } }

        @Override
        public DoubleLinkedList<T> FilterBy(Predicate<T> predicate) throws ArgumentNullException { synchronized (lock) { return super.FilterBy(predicate); } }

        @Override
        public <TO> DoubleLinkedList<TO> ConvertAll(Converter<T, TO> converter) throws ArgumentNullException { synchronized (lock) { return super.ConvertAll(converter); } }

        @Override
        public <TO> DoubleLinkedList<TO> ConvertAll(Converter<T, TO> converter, IEqualityComparer<TO> comparer) throws ArgumentNullException { synchronized (lock) { return super.ConvertAll(converter, comparer); } }
    }

    private static final class DLLNodeReference<T>
        extends DoublyLinkedListNodeReference<T>
    {
        private final NodeWithNextAndPrevPointer<T> actual_node;

        public DLLNodeReference(DoubleLinkedList<T> list, NodeWithNextAndPrevPointer<T> node)
                throws ArgumentNullException
        {
            super(
                  list,
                  node.Value,
                  node.Previous == null ? null : node.Previous.Value,
                  node.Next == null ? null : node.Next.Value
            );
            this.actual_node = node;
        }

        @Override
        public DoubleLinkedList<T> GetList() { return (DoubleLinkedList<T>) super.GetList(); }

        public void Apply()
        {
            DoubleLinkedList<T> list_ref = GetList();
            boolean is_to_be_deleted = this.IsToBeRemoved();
            boolean prev_modified = this.PreviousValueModified();
            if (actual_node.Previous == null) {
                // First check for being invalid, then for invalid modification
                if (actual_node != list_ref.root) {
                    throw new InvalidDoublyLinkedListNodeApplicationException("This node is invalid for the current list. It has been possibly removed.");
                } else if (prev_modified) {
                    throw new InvalidDoublyLinkedListNodeApplicationException("No previous nodes can exist for root nodes.");
                }
            } else if (actual_node.Previous.Next != actual_node) {
                throw new InvalidDoublyLinkedListNodeApplicationException("This node is invalid for the current list. It has been possibly removed.");
            }
            if (NextValueModified())
            {
                if (actual_node.Next == null) {
                    list_ref.Add(GetNextNodeValue());
                    actual_node.Next = list_ref.current;
                } else {
                    actual_node.Next.Value = GetNextNodeValue();
                }
            }
            if (prev_modified) {
                // A previous value will always be non-null reference, except on root nodes, which we validated that above!
                actual_node.Previous.Value = GetPreviousNodeValue();
            }
            if (is_to_be_deleted) {
                list_ref.InternalRemoveNode(actual_node);
            } else if (CurrentValueModified()) {
                actual_node.Value = GetValue();
            }
        }
    }

    @Override
    public DoublyLinkedListNodeReference<T> GetNode(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            return new DLLNodeReference<>(this, InternalGetNode(index));
        }
    }

    @Override
    public void ApplyNode(DoublyLinkedListNodeReference<T> node)
            throws ArgumentNullException, InvalidDoublyLinkedListNodeApplicationException
    {
        ArgumentNullException.ThrowIfNull(node, "node");
        InvalidDoublyLinkedListNodeApplicationException.ThrowIfForeignList(this, node);
        if (node instanceof DLLNodeReference<T> reference) {
            reference.Apply();
        } else {
            // Possibly unreachable code path, but let's ensure it
            throw new InvalidDoublyLinkedListNodeApplicationException("This node cannot be applied to this list!");
        }
    }

    @Override
    public int getCount() { return count; }

    @Override
    public int GetCount() { return count; }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    public IEnumerator<T> GetEnumerator() { return new NodeWithNextAndPrevPointerEnumerator<>(root); }

    @Override
    public T getItem(int index)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            return InternalGetNode(index).Value;
        }
    }

    @Override
    public T GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            return InternalGetNode(index).Value;
        }
    }

    @Override
    public void setItem(int index, T item)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            InternalGetNode(index).Value = item;
        }
    }

    @Override
    public int IndexOf(@AllowNull T item)
    {
        int index = 0;
        NodeWithNextAndPrevPointer<T> p = root;
        while (p != null)
        {
            if (comparer.Equals(p.Value, item)) { return index; }
            index++;
            p = p.Next;
        }
        return -1;
    }

    @Override
    public boolean Contains(T item)
    {
        NodeWithNextAndPrevPointer<T> p = root;
        while (p != null)
        {
            if (comparer.Equals(p.Value, item)) { return true; }
            p = p.Next;
        }
        return false;
    }

    @Override
    public boolean Remove(@AllowNull T item)
    {
        NodeWithNextAndPrevPointer<T> p = root;
        while (p != null)
        {
            if (comparer.Equals(p.Value, item))
            {
                InternalRemoveNode(p);
                return true;
            }
            p = p.Next;
        }
        return false;
    }

    @Override
    public void Insert(int index, @AllowNull T item)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index > count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else if (index == count) {
            // When index == count, it is like adding an item, so most appropriate here is to call the Add method.
            Add(item);
        } else {
            NodeWithNextAndPrevPointer<T> p = InternalGetNode(index);
            if (p.Previous == null) {
                NodeWithNextAndPrevPointer<T> old = root;
                root = new NodeWithNextAndPrevPointer<>(item, root, null);
                old.Previous = root;
            } else {
                // Keep the current previous node
                NodeWithNextAndPrevPointer<T> old_prev = p.Previous;
                // Construct the insertion node
                NodeWithNextAndPrevPointer<T> created = new NodeWithNextAndPrevPointer<>(item, p, old_prev);
                // Replace previous node of the indexed one with the constructed one
                p.Previous = created;
                // Old previous node merged in 'created' node, must now it's 'Next' pointer point to the 'created' node.
                old_prev.Next = created;
            }
            // Increase count.
            count++;
        }
    }

    @Override
    public void RemoveAt(int index)
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the list's bounds.");
        } else {
            InternalRemoveNode(InternalGetNode(index));
        }
    }

    private void InternalRemoveNode(NodeWithNextAndPrevPointer<T> p)
    {
        if (p.Previous == null) {
            // We are removing the root node.
            // In that case, we fetch the next element.
            // In the rare case, if that next element is null (meaning that the
            // root was the only one element), we must also assign the 'current' reference to null.
            root = root.Next;
            if (root == null) {
                current = null;
            } else {
                // Otherwise, we have to set Previous = null otherwise we will have a cycle.
                root.Previous = null;
            }
        } else {
            NodeWithNextAndPrevPointer<T> node = p.Next;
            boolean last = node == null;
            if (last)
            {
                // We are removing the last element from the list.
                // We need to also update the 'current' node reference.
                current = p.Previous;
            }
            p.Previous.Next = node;
            // We detached the node from the list from the Next pointer,
            // we also need to do that for the Previous pointer as well.
            if (!last) { node.Previous = p.Previous; }
        }
        // Decrement count and we have completed the removal.
        count--;
    }

    // Improved node finding by index in DoubleLinkedList:
    // Due to the fact that we allocate two pointers, one back and one ahead,
    // we can take this advantage so that:
    // -> In index values close to the end (e.g. 100 elements and index of 66),
    // we search backwards by taking advantage of the 'current' node reference.
    // -> In index values close to the beginning (e.g. 100 elements and index of 10), then we search normally.
    // -> We can also have edge cases in the middle if the index is such.
    // In such case, the node is searched normally.
    private NodeWithNextAndPrevPointer<T> InternalGetNode(int index)
    {
        return (index >= (count / 2)) ? InternalGetNodeReversed(index) : InternalGetNodeNormal(index);
    }

    private NodeWithNextAndPrevPointer<T> InternalGetNodeReversed(int index)
    {
        NodeWithNextAndPrevPointer<T> p = current;
        int I = count - 1;
        while (I > index) { p = p.Previous; I--; }
        return p;
    }

    private NodeWithNextAndPrevPointer<T> InternalGetNodeNormal(int index)
    {
        NodeWithNextAndPrevPointer<T> p = root;
        int I = 0;
        while (I < index) { p = p.Next; I++; }
        return p;
    }

    @Override
    public void Add(T item)
    {
        NodeWithNextAndPrevPointer<T> new_node = new NodeWithNextAndPrevPointer<>(item);
        new_node.Previous = current;
        if (count == 0) {
            root = current = new_node;
        } else {
            current.Next = new_node;
            current = new_node;
        }
        count++;
    }

    @Override
    public void Clear()
    {
        count = 0;
        root = current = null;
    }

    @Override
    public void CopyTo(T[] array, int arrayIndex)
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (arrayIndex < 0) {
            throw new ArgumentOutOfRangeException("arrayIndex", "Array index cannot be a negative value.");
        } else if (arrayIndex + count > array.length) {
            throw new ArgumentException("The array does not have enough space to place all the elements of the current SingleLinkedList object.", "array");
        } else {
            NodeWithNextAndPrevPointer<T> p = root;
            for (int I = arrayIndex; p != null; p = p.Next) { array[I++] = p.Value; }
        }
    }

    @Override
    public DoubleLinkedList<T> FilterBy(Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return this;
        } else if (FunctionManipulations.IsAlwaysFalse(predicate)) {
            return new DoubleLinkedList<>(comparer);
        } else {
            DoubleLinkedList<T> ret = new DoubleLinkedList<>(comparer);

            IEnumerator<T> en = GetEnumerator();
            try {
                T element;
                while (en.MoveNext()) {
                    if (predicate.predicate(element = en.getCurrent())) { ret.Add(element); }
                }
            } finally {
                en.Dispose();
            }

            return ret;
        }
    }

    @Override
    public DoubleLinkedList<T> Slice(int index, int count)
            throws ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else {
            int total = index + count;
            if (total > this.count || total < 0) {
                throw new ArgumentException("The specified combination of index and count parameters exceed the list's bounds.");
            } else {
                DoubleLinkedList<T> ret = new DoubleLinkedList<>(comparer);
                NodeWithNextAndPrevPointer<T> nd = InternalGetNode(index);
                for (int I = 0; I < count; I++, nd = nd.Next) { ret.Add(nd.Value); }
                return ret;
            }
        }
    }

    @Override
    public <TO> DoubleLinkedList<TO> ConvertAll(Converter<T, TO> converter, @AllowNull IEqualityComparer<TO> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");

        DoubleLinkedList<TO> ret = new DoubleLinkedList<>(comparer);

        IEnumerator<T> en = GetEnumerator();
        try {
            while (en.MoveNext()) {
                ret.Add(converter.convert(en.getCurrent()));
            }
        } finally {
            en.Dispose();
        }

        return ret;
    }

    @Override
    public DoubleLinkedList<T> Clone()
    {
        DoubleLinkedList<T> ret = new DoubleLinkedList<>(comparer);

        IEnumerator<T> en = GetEnumerator();
        try {
            while (en.MoveNext()) { ret.Add(en.getCurrent()); }
        } finally {
            en.Dispose();
        }

        return ret;
    }

    @Override
    public DoubleLinkedList<T> Slice(int count) throws ArgumentException { return Slice(0, count); }

    @Override
    public <TO> DoubleLinkedList<TO> ConvertAll(Converter<T, TO> converter) throws ArgumentNullException { return ConvertAll(converter, null); }

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
        sb.append(String.format("DoubleLinkedList<?> (%d) { ", count));
        if (count == 0) {
            sb.append("<EMPTY>");
        } else {
            NodeWithNextAndPrevPointer<T> p = root, next;
            while (p != null)
            {
                sb.append(p.Value);
                if ((next = p.Next) != null) { sb.append(", "); }
                p = next;
            }
        }
        sb.append(" }");
        return sb.toString();
    }
}
