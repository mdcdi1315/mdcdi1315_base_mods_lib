package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.utils.ISynchronizedByObject;

import java.util.Arrays;

/**
 * Defines a {@link IDictionary} implementation by using a stable hash table and B-Trees for storing additional values in the dictionary. <br />
 * Keys cannot be explicitly repeated on this implementation. <br />
 * Note: There are better and faster implementations out there for storing key-value pairs, but this is adequately fast.
 * @param <TKey> The type of key.
 * @param <TValue> The type of value.
 * @since 1.0.34
 */
public class BTreeDictionary<TKey, TValue>
    implements IDictionary<TKey, TValue>
{
    private int count;

    @NotNull
    private final IEqualityComparer<TKey> comparer;
    private final BTreeNode<TKey, TValue>[] HashTable;

    private static class BTreeNode<TKey, TValue>
    {
        public TKey Key;
        public TValue Value;
        public int HashCode;

        public BTreeNode<TKey, TValue> Parent;
        public BTreeNode<TKey, TValue> LeftChild;
        public BTreeNode<TKey, TValue> RightChild;

        public BTreeNode(TKey key, TValue value)
        {
            Key = key;
            Value = value;
            Parent = LeftChild = RightChild = null;
        }

        public KeyValuePair<TKey, TValue> ToKeyValuePair() { return new KeyValuePair<>(Key, Value); }
    }

    private static final class DeletedBTreeNode<TKey, TValue>
            extends BTreeNode<TKey, TValue>
    {
        public DeletedBTreeNode(BTreeNode<TKey, TValue> node)
        {
            super(null, null);
            Parent = node.Parent;
            LeftChild = node.LeftChild;
            RightChild = node.RightChild;
            HashCode = node.HashCode;
        }

        public BTreeNode<TKey, TValue> ToNode()
        {
            BTreeNode<TKey, TValue> node = new BTreeNode<>(Key, Value);
            node.Parent = Parent;
            node.LeftChild = LeftChild;
            node.RightChild = RightChild;
            node.HashCode = HashCode;
            return node;
        }

        public String toString() { return String.format("Deleted node: %s %s %s", LeftChild, RightChild, Parent); }
    }

    private static final class Enumerator<TKey, TValue>
        extends BaseEnumerator<KeyValuePair<TKey, TValue>>
    {
        private int hh_index;
        private BTreeNode<TKey, TValue>[] hh;
        private BTreeNode<TKey, TValue> current;
        private SingleLinkedListBasedQueue<BTreeNode<TKey, TValue>> nodes_to_enumerate;

        public Enumerator(BTreeNode<TKey, TValue>[] ht)
        {
            hh = ht;
            hh_index = -1;
            current = null;
            nodes_to_enumerate = new SingleLinkedListBasedQueue<>();
        }

        @SuppressWarnings("StatementWithEmptyBody")
        private boolean MoveToNextBTree()
        {
            BTreeNode<TKey, TValue> temp = null;
            while (++hh_index < hh.length && (temp = hh[hh_index]) == null) ;

            if (temp == null) {
                return false;
            } else {
                nodes_to_enumerate.Enqueue(temp);
                return true;
            }
        }

        private void EnqNodeSafe(BTreeNode<TKey, TValue> node)
        {
            if (node == null) { return; }
            nodes_to_enumerate.Enqueue(node);
        }

        @Override
        public KeyValuePair<TKey, TValue> getCurrent() { return current.ToKeyValuePair(); }

        @Override
        public void Dispose()
        {
            super.Dispose();
            hh = null;
            current = null;
            nodes_to_enumerate = null;
        }

        @Override
        protected boolean MoveNextImpl()
        {
            while (true)
            {
                while ((current = nodes_to_enumerate.TryDequeue()) != null)
                {
                    EnqNodeSafe(current.LeftChild);
                    EnqNodeSafe(current.RightChild);
                    if (!(current instanceof DeletedBTreeNode)) { break; }
                }
                if (current != null) {
                    return true;
                } else if (!MoveToNextBTree()) {
                    return false;
                }
            }
        }

        @Override
        protected void ResetImpl()
        {
            hh_index = -1;
            current = null;
            nodes_to_enumerate.Clear();
        }
    }

    private record KeyCollection<TKey, TValue>(BTreeDictionary<TKey, TValue> dictionary)
            implements ICollection<TKey>
    {
        @Override
        public void Clear() { dictionary.Clear(); }

        @Override
        public int getCount() { return dictionary.getCount(); }

        @Override
        public boolean getIsReadOnly() { return dictionary.getIsReadOnly(); }

        @Override
        public boolean Contains(TKey item) { return dictionary.ContainsKey(item); }

        @Override
        public boolean Remove(TKey item) { return dictionary.Remove_Ordinal2(item); }

        @Override
        public void Add(TKey item) { throw new NotSupportedException("Not supported"); }

        @Override
        public IEnumerator<TKey> GetEnumerator() { return new MappingEnumerator<>(dictionary.GetEnumerator(), (Func2<KeyValuePair<TKey, TValue>, TKey>) KeyValuePair::getKey); }

        @Override
        public void CopyTo(TKey[] array, int arrayIndex)
        {
            ArgumentNullException.ThrowIfNull(array, "array");
            if (arrayIndex < 0) {
                throw new ArgumentOutOfRangeException("arrayIndex", "Array index cannot be a negative value.");
            } else if (arrayIndex + getCount() > array.length) {
                throw new ArgumentException("The array does not have enough space to place all the elements of the current BTreeDictionary object.", "array");
            } else {
                try (IEnumerator<TKey> e = GetEnumerator())
                {
                    for (int I = arrayIndex; e.MoveNext(); I++) { array[I] = e.getCurrent(); }
                }
            }
        }
    }

    private record ValueCollection<TKey, TValue>(BTreeDictionary<TKey, TValue> dictionary)
        implements ICollection<TValue>
    {
        @Override
        public void Clear() { dictionary.Clear(); }

        @Override
        public int getCount() { return dictionary.getCount(); }

        @Override
        public boolean getIsReadOnly() { return dictionary.getIsReadOnly(); }

        @Override
        public boolean Contains(TValue item) { return dictionary.ContainsValue(item); }

        @Override
        public void Add(TValue item) { throw new NotSupportedException("Not supported"); }

        @Override
        public boolean Remove(TValue item) { throw new NotSupportedException("Not supported in a values collection"); }

        @Override
        public IEnumerator<TValue> GetEnumerator() { return new MappingEnumerator<>(dictionary.GetEnumerator(), (Func2<KeyValuePair<TKey,TValue>, TValue>) KeyValuePair::getValue); }

        @Override
        public void CopyTo(TValue[] array, int arrayIndex)
        {
            ArgumentNullException.ThrowIfNull(array, "array");
            if (arrayIndex < 0) {
                throw new ArgumentOutOfRangeException("arrayIndex", "Array index cannot be a negative value.");
            } else if (arrayIndex + getCount() > array.length) {
                throw new ArgumentException("The array does not have enough space to place all the elements of the current BTreeDictionary object.", "array");
            } else {
                try (IEnumerator<TValue> e = GetEnumerator())
                {
                    for (int I = arrayIndex; e.MoveNext(); I++) { array[I] = e.getCurrent(); }
                }
            }
        }
    }

    private static final class Synchronized<TKey, TValue>
        extends BTreeDictionary<TKey, TValue>
        implements ISynchronizedByObject
    {
        private final Object lock;

        public Synchronized() { super(); lock = new Object(); }

        public Synchronized(IEqualityComparer<TKey> comparer) { super(comparer); lock = new Object(); }

        public Synchronized(int hash_table_size) throws ArgumentOutOfRangeException { super(hash_table_size); lock = new Object(); }

        public Synchronized(int hash_table_size, IEqualityComparer<TKey> comparer) { super(hash_table_size, comparer); lock = new Object(); }

        @Override
        public Object GetSyncObject() { return lock; }

        @Override
        public void Clear() { synchronized (lock) { super.Clear(); } }

        @Override
        public int getCount() { synchronized (lock) { return super.getCount(); } }

        @Override
        public ICollection<TKey> getKeys() { synchronized (lock) { return super.getKeys(); } }

        @Override
        public boolean getIsReadOnly() { synchronized (lock) { return super.getIsReadOnly(); } }

        @Override
        public ICollection<TValue> getValues() { synchronized (lock) { return super.getValues(); } }

        @Override
        public void Add(KeyValuePair<TKey, TValue> item) { synchronized (lock) { super.Add(item); } }

        @Override
        public boolean ContainsValue(TValue value) { synchronized (lock) { return super.ContainsValue(value); } }

        @Override
        public boolean Remove(KeyValuePair<TKey, TValue> item) { synchronized (lock) { return super.Remove(item); } }

        @Override
        public boolean Contains(KeyValuePair<TKey, TValue> item) { synchronized (lock) { return super.Contains(item); } }

        @Override
        public IEnumerator<KeyValuePair<TKey, TValue>> GetEnumerator() { synchronized (lock) { return super.GetEnumerator(); } }

        @Override
        public boolean ContainsKey(TKey key) throws ArgumentNullException { synchronized (lock) { return super.ContainsKey(key); } }

        @Override
        public void CopyTo(KeyValuePair<TKey, TValue>[] array, int arrayIndex) { synchronized (lock) { super.CopyTo(array, arrayIndex); } }

        @Override
        public TValue getItem(TKey key) throws ArgumentNullException, KeyNotFoundException { synchronized (lock) { return super.getItem(key); } }

        @Override
        public void Add(TKey key, TValue value) throws ArgumentException, NotSupportedException { synchronized (lock) { super.Add(key, value); } }

        @Override
        public void setItem(TKey key, TValue value) throws ArgumentNullException, NotSupportedException { synchronized (lock) { super.setItem(key, value); } }

        @Override
        public boolean Remove_Ordinal2(TKey key) throws ArgumentNullException, NotSupportedException { synchronized (lock) { return super.Remove_Ordinal2(key); } }

        @Override
        public boolean TryGetValue(TKey key, ByRefParameter<TValue> value) throws ArgumentNullException { synchronized (lock) { return super.TryGetValue(key, value); } }
    }

    /**
     * Constructs a new instance of the {@link BTreeDictionary} class,
     * specifying 10 B-Trees and using the default equality comparer for comparing keys.
     */
    @SuppressWarnings("unchecked")
    public BTreeDictionary()
    {
        count = 0;
        HashTable = new BTreeNode[10];
        comparer = new EqualityComparer.ObjectEqualityComparer<>();
    }

    /**
     * Constructs a new instance of the {@link BTreeDictionary} class and specifying the number of B-Trees that can be internally allocated for this dictionary.
     * @apiNote While it is possible to define a value of 1 for {@code hash_table_size},
     * this means that you are explicitly using a single B-Tree only for managing the entirety of the dictionary
     * and this can have adverse effects on CPU performance. A size of 10 is typically good enough for most applications.
     * @param hash_table_size The internal hash table size. As larger this value is, the more and smaller B-Trees can be stored.
     * @throws ArgumentOutOfRangeException {@code hash_table_size} is a negative value.
     */
    @SuppressWarnings("unchecked")
    public BTreeDictionary(int hash_table_size)
        throws ArgumentOutOfRangeException
    {
        if (hash_table_size < 0) {
            throw new ArgumentOutOfRangeException("hash_table_size", "Hash table size cannot be a negative value.");
        } else {
            count = 0;
            HashTable = new BTreeNode[hash_table_size];
            comparer = new EqualityComparer.ObjectEqualityComparer<>();
        }
    }

    /**
     * Constructs a new instance of the {@link BTreeDictionary} class and specifying the equality comparer to use for comparing the dictionary's keys.
     * @apiNote Generally try to provide an equality comparer that is non-collisible, at least for the use case you need this. <br />
     * This class depends on hash codes in order to properly re-order the keys in the B-Trees.
     * @param comparer An {@link IEqualityComparer} implementation for comparing the keys of this dictionary. Can be {@code null}.
     */
    @SuppressWarnings("unchecked")
    public BTreeDictionary(@AllowNull IEqualityComparer<TKey> comparer)
    {
        count = 0;
        HashTable = new BTreeNode[10];
        this.comparer = comparer == null ? new EqualityComparer.ObjectEqualityComparer<>() : comparer;
    }

    /**
     * Constructs a new instance of the {@link BTreeDictionary} class by specifying the number of B-Trees that can be internally allocated for this dictionary,
     * as well as the equality comparer to use for comparing the dictionary's keys.
     * @param hash_table_size The internal hash table size. As larger this value is, the more and smaller B-Trees can be stored.
     * @param comparer An {@link IEqualityComparer} implementation for comparing the keys of this dictionary. Can be {@code null}.
     * @throws ArgumentOutOfRangeException {@code hash_table_size} is a negative value.
     */
    @SuppressWarnings("unchecked")
    public BTreeDictionary(int hash_table_size, @AllowNull IEqualityComparer<TKey> comparer)
    {
        if (hash_table_size < 0) {
            throw new ArgumentOutOfRangeException("hash_table_size", "Hash table size cannot be a negative value.");
        } else {
            count = 0;
            HashTable = new BTreeNode[hash_table_size];
            this.comparer = comparer == null ? new EqualityComparer.ObjectEqualityComparer<>() : comparer;
        }
    }

    /**
     * Constructs a new instance of the {@link BTreeDictionary} class,
     * specifying 15 B-Trees and using the default equality comparer for comparing keys. <br />
     * The object that is returned from this method is thread-safe.
     * @return A new thread-safe {@link BTreeDictionary} instance.
     * @param <TKey> The type of key.
     * @param <TValue> The type of value.
     */
    @NotNull
    public static <TKey, TValue> BTreeDictionary<TKey, TValue> CreateSynchronized() { return new Synchronized<>(); }

    /**
     * Constructs a new instance of the {@link BTreeDictionary} class and specifying the number of B-Trees that can be internally allocated for this dictionary. <br />
     * The object that is returned from this method is thread-safe.
     * @apiNote While it is possible to define a value of 1 for {@code hash_table_size},
     * this means that you are explicitly using a single B-Tree only for managing the entirety of the dictionary
     * and this can have adverse effects on CPU performance. A size of 10 is typically good enough for most applications.
     * @param hash_table_size The internal hash table size. As larger this value is, the more and smaller B-Trees can be stored.
     * @return A new thread-safe {@link BTreeDictionary} instance.
     * @param <TKey> The type of key.
     * @param <TValue> The type of value.
     * @throws ArgumentOutOfRangeException {@code hash_table_size} is a negative value.
     */
    @NotNull
    public static <TKey, TValue> BTreeDictionary<TKey, TValue> CreateSynchronized(int hash_table_size) throws ArgumentOutOfRangeException { return new Synchronized<>(hash_table_size); }

    /**
     * Constructs a new instance of the {@link BTreeDictionary} class and specifying the equality comparer to use for comparing the dictionary's keys. <br />
     * The object that is returned from this method is thread-safe.
     * @param comparer An {@link IEqualityComparer} implementation for comparing the keys of this dictionary. Can be {@code null}.
     * @apiNote Generally try to provide an equality comparer that is non-collisible, at least for the use case you need this. <br />
     * This class depends on hash codes in order to properly re-order the keys in the B-Trees.
     * @return A new thread-safe {@link BTreeDictionary} instance.
     * @param <TKey> The type of key.
     * @param <TValue> The type of value.
     */
    @NotNull
    public static <TKey, TValue> BTreeDictionary<TKey, TValue> CreateSynchronized(@AllowNull IEqualityComparer<TKey> comparer) { return new Synchronized<>(comparer); }

    /**
     * Constructs a new instance of the {@link BTreeDictionary} class by specifying the number of B-Trees that can be internally allocated for this dictionary,
     * as well as the equality comparer to use for comparing the dictionary's keys. <br />
     * The object that is returned from this method is thread-safe.
     * @param hash_table_size The internal hash table size. As larger this value is, the more and smaller B-Trees can be stored.
     * @param comparer An {@link IEqualityComparer} implementation for comparing the keys of this dictionary. Can be {@code null}.
     * @return A new thread-safe {@link BTreeDictionary} instance.
     * @param <TKey> The type of key.
     * @param <TValue> The type of value.
     * @throws ArgumentOutOfRangeException {@code hash_table_size} is a negative value.
     */
    @NotNull
    public static <TKey, TValue> BTreeDictionary<TKey, TValue> CreateSynchronized(int hash_table_size, @AllowNull IEqualityComparer<TKey> comparer) throws ArgumentOutOfRangeException { return new Synchronized<>(hash_table_size, comparer); }

    private int HashFunction(int hash_code) { return Math.abs(hash_code % HashTable.length); }

    private static <TK, TV> BTreeNode<TK, TV> FindNode(int key_hash_code, BTreeNode<TK, TV> n)
    {
        return n == null ? null :
            (
                    (key_hash_code < n.HashCode) ? (
                            // Search in the left child node
                            FindNode(key_hash_code, n.LeftChild)
                    ) : (
                            (key_hash_code > n.HashCode) ? (
                                    // Search in the right child node
                                    FindNode(key_hash_code, n.RightChild)
                            ) : n
                    )
            );
    }

    // Puts a new node in the B-Tree.
    @StackTraceHidden
    private static <TK, TV> BTreeNode<TK, TV> PutInBTree(BTreeNode<TK, TV> constructed, BTreeNode<TK, TV> root)
    {
        if (root == null) {
            return constructed;
        } else if (constructed.HashCode < root.HashCode) {
            constructed.Parent = root; // Required so that our newly added node points to the correct parent
            root.LeftChild = PutInBTree(constructed, root.LeftChild);
        } else if (constructed.HashCode > root.HashCode) {
            constructed.Parent = root; // Required so that our newly added node points to the correct parent
            root.RightChild = PutInBTree(constructed, root.RightChild);
        } else if (root instanceof DeletedBTreeNode<TK, TV> dn) {
            // constructed.HashCode == root.HashCode will be true
            // Re-convert back to a node
            BTreeNode<TK, TV> ret = dn.ToNode();
            ret.Key = constructed.Key;
            ret.Value = constructed.Value;
            // Return it.
            return ret;
        } else {
            throw new ArgumentException(String.format("The key '%s' has already been added to the dictionary!", constructed.Key));
        }
        // Return the root node.
        return root;
    }

    private BTreeNode<TKey, TValue> FindNodeReference(TKey key)
    {
        int k = comparer.GetHashCode(key);
        return FindNode(k, HashTable[HashFunction(k)]);
    }

    @Override
    public int getCount() { return count; }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    public void Clear() { count = 0; Arrays.fill(HashTable, null); }

    @Override
    public ICollection<TKey> getKeys() { return new KeyCollection<>(this); }

    @Override
    public ICollection<TValue> getValues() { return new ValueCollection<>(this); }

    @Override
    public boolean Contains(KeyValuePair<TKey, TValue> item) { return ContainsKey(item.getKey()); }

    @Override
    public boolean Remove(KeyValuePair<TKey, TValue> item) { return Remove_Ordinal2(item.getKey()); }

    @Override
    public IEnumerator<KeyValuePair<TKey, TValue>> GetEnumerator() { return new Enumerator<>(HashTable); }

    @Override
    public boolean ContainsKey(TKey key) throws ArgumentNullException { return key != null && FindNodeReference(key) != null; }

    /**
     * Determines whether the {@link BTreeDictionary} contains a specific value.
     * @param value The value to locate in the {@link BTreeDictionary}. The value can be null for reference types.
     * @return {@code true} if the {@link BTreeDictionary} contains an element with the specified value; otherwise, {@code false}.
     * @implNote This method determines equality using the default equality comparer for {@link TValue}, the type of values in the dictionary.
     * This method performs a linear search; therefore, the average execution time is proportional to {@link #getCount()}.
     * That is, this method is an O(n) operation, where n is {@link #getCount()}.
     */
    public boolean ContainsValue(@AllowNull TValue value)
    {
        IEqualityComparer<TValue> c = new EqualityComparer.ObjectEqualityComparer<>();

        try (IEnumerator<KeyValuePair<TKey, TValue>> e = GetEnumerator())
        {
            while (e.MoveNext())
            {
                if (c.Equals(value, e.getCurrent().getValue()))
                {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    @MaybeNull
    public TValue getItem(TKey key)
            throws ArgumentNullException, KeyNotFoundException
    {
        ByRefParameter<TValue> by_ref = new ByRefParameter<>();
        if (TryGetValue(key, by_ref)) {
            return by_ref.Value;
        } else {
            throw new KeyNotFoundException(String.format("Key '%s' could not be found.", key));
        }
    }

    @Override
    public void setItem(TKey key, @AllowNull TValue value)
            throws ArgumentNullException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        BTreeNode<TKey, TValue> fd = FindNodeReference(key);
        if (fd == null) {
            Add(key, value);
        } else {
            fd.Value = value;
        }
    }

    @Override
    public boolean Remove_Ordinal2(TKey key)
            throws ArgumentNullException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(key);
        BTreeNode<TKey, TValue> f = FindNodeReference(key);
        if (f == null || f instanceof DeletedBTreeNode) {
            return false;
        } else {
            if (f.Parent == null) {
                HashTable[HashFunction(f.HashCode)] = new DeletedBTreeNode<>(f);
            } else if (f.Parent.LeftChild == f) {
                f.Parent.LeftChild = new DeletedBTreeNode<>(f);
            } else {
                f.Parent.RightChild = new DeletedBTreeNode<>(f);
            }
            count--;
            return true;
        }
    }

    @Override
    public boolean TryGetValue(
            TKey key,
            @DisallowNull
            @DotNetByRefParameter(ByRefParameterType.OUT)
            ByRefParameter<TValue> value
    )
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        ByRefParameter.AssertEOut(value);
        BTreeNode<TKey, TValue> n = FindNodeReference(key);
        if (n == null || n instanceof DeletedBTreeNode) {
            value.Value = null;
            return false;
        } else {
            value.Value = n.Value;
            return true;
        }
    }

    @Override
    public void Add(KeyValuePair<TKey, TValue> item)
    {
        TKey key;

        if ((key = item.getKey()) == null)
        {
            throw new ArgumentException("The Key property of the key-value pair is null.", "item");
        }
        else
        {
            int hc = comparer.GetHashCode(key), h = HashFunction(hc);

            BTreeNode<TKey, TValue> constructed = new BTreeNode<>(key, item.getValue());
            constructed.HashCode = hc;

            HashTable[h] = PutInBTree(constructed, HashTable[h]);
            count++;
        }
    }

    @Override
    @SuppressWarnings("DuplicateThrows")
    public void Add(TKey key, TValue value)
            throws ArgumentNullException, ArgumentException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        int hc = comparer.GetHashCode(key), h = HashFunction(hc);

        BTreeNode<TKey, TValue> constructed = new BTreeNode<>(key, value);
        constructed.HashCode = hc;

        HashTable[h] = PutInBTree(constructed, HashTable[h]);
        count++;
    }

    @Override
    public void CopyTo(KeyValuePair<TKey, TValue>[] array, int arrayIndex)
    {
        ArgumentNullException.ThrowIfNull(array, "array");
        if (arrayIndex < 0) {
            throw new ArgumentOutOfRangeException("arrayIndex", "Array index cannot be a negative value.");
        } else if (arrayIndex + count > array.length) {
            throw new ArgumentException("The array does not have enough space to place all the elements of the current BTreeDictionary object.", "array");
        } else {
            try (Enumerator<TKey, TValue> e = new Enumerator<>(HashTable))
            {
                for (int I = arrayIndex; e.MoveNext(); I++) { array[I] = e.getCurrent(); }
            }
        }
    }

    @NotNull
    @Override
    public final String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("BTreeDictionary<?, ?> (%d) { ", count));
        if (count == 0) {
            sb.append("<EMPTY>");
        } else {
            try (var en = GetEnumerator())
            {
                int cc = 0;
                while (en.MoveNext())
                {
                    sb.append('{');
                    sb.append(en.getCurrent());
                    sb.append('}');
                    if (++cc < count) { sb.append(", "); }
                }
            }
        }
        sb.append(" }");
        return sb.toString();
    }
}
