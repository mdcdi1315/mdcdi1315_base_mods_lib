package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.*;
import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.HashHelpers;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImpl;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNullWhen;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImplOptions;

import java.util.Arrays;

public class Dictionary<TKey, TValue>
    implements IDictionary<TKey, TValue>
{
    @AllowNull
    private int[] _buckets;
    @AllowNull
    private Entry<TKey, TValue>[] _entries;

    private int _count;
    private int _freeList;
    private int _freeCount;
    private int _version;
    @AllowNull
    private IEqualityComparer<? super TKey> _comparer;
    private KeyCollection<TKey, TValue> _keyCollection;
    private ValueCollection<TKey, TValue> _valueCollection;

    private static final int StartOfFreeList = -3;

    public Dictionary() { this(15, null); }

    public Dictionary(int capacity) throws ArgumentOutOfRangeException { this(capacity, null); }

    public Dictionary(int capacity, @AllowNull IEqualityComparer<? super TKey> comparer)
            throws ArgumentOutOfRangeException
    {
        if (capacity < 0)
        {
            throw new ArgumentOutOfRangeException("capacity", "Capacity must not be a negative value");
            // ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.capacity);
        }

        if (capacity > 0)
        {
            Initialize(capacity);
        }

        _comparer = comparer == null ? EqualityComparer.GetDefault() : comparer;
    }

    public Dictionary(IEnumerable<KeyValuePair<TKey, TValue>> enumerable)
    {
        this();
        AddRange(enumerable);
    }

    @ClassIsDotNetStruct
    private static final class Entry<TKey, TValue>
        extends ValueType
    {
        public int hashCode;
        /**
         * 0-based index of next entry in chain: -1 means end of chain
         * also encodes whether this entry <em>itself</em> is part of the free list by changing sign and subtracting 3,
         * so -2 means end of free list, -3 means index 0 but on free list, -4 means index 1 but on free list, etc.
         */
        public int next;
        public TKey key;     // Key of entry
        public TValue value; // Value of entry

        public Entry() { hashCode = 0; next = 0; key = null; value = null; }
    }

    private static final class EntryValueReference<TKey, TValue>
        extends ModifiableValueReference<TValue>
    {
        private final Entry<TKey, TValue> entry;

        public EntryValueReference(Entry<TKey, TValue> entry)
        {
            this.entry = entry;
            UpdateValue(entry.value);
        }

        @Override
        public void SetValue(TValue value) { UpdateValue(this.entry.value = value); }
    }

    @SuppressWarnings("unchecked")
    private int Initialize(int capacity)
    {
        int size = HashHelpers.GetPrime(capacity);
        int[] buckets = new int[size];
        Entry<TKey, TValue>[] entries = new Entry[size];

        // Assign member variables after both arrays allocated to guard against corruption from OOM if second fails
        _freeList = -1;
        _buckets = buckets;
        _entries = entries;

        return size;
    }

    private void AddRange(IEnumerable<KeyValuePair<TKey, TValue>> enumerable)
    {
        // It is likely that the passed-in enumerable is Dictionary<TKey,TValue>. When this is the case,
        // avoid the enumerator allocation and overhead by looping through the entries array directly.
        // We only do this when dictionary is Dictionary<TKey,TValue> and not a subclass, to maintain
        // back-compat with subclasses that may have overridden the enumerator behavior.
        if (enumerable.getClass() == Dictionary.class)
        {
            Dictionary<TKey, TValue> source = (Dictionary<TKey, TValue>)enumerable;

            if (source.getCount() == 0)
            {
                // Nothing to copy, all done
                return;
            }

            // This is not currently a true .AddRange as it needs to be an initialized dictionary
            // of the correct size, and also an empty dictionary with no current entities (and no argument checks).
            // Debug.Assert(source._entries is not null);
            // Debug.Assert(_entries is not null);
            // Debug.Assert(_entries.Length >= source.Count);
            // Debug.Assert(_count == 0);

            Entry<TKey, TValue>[] oldEntries = source._entries;
            if (source._comparer == _comparer)
            {
                // If comparers are the same, we can copy _entries without rehashing.
                CopyEntries(oldEntries, source._count);
                return;
            }

            // Comparers differ need to rehash all the entries via Add
            int count = source._count;
            for (int i = 0; i < count; i++)
            {
                // Only copy if an entry
                if (oldEntries[i].next >= -1)
                {
                    Add(oldEntries[i].key, oldEntries[i].value);
                }
            }
            return;
        }

        try (IEnumerator<KeyValuePair<TKey, TValue>> e = enumerable.GetEnumerator())
        {
            while (e.MoveNext())
            {
                var kvp = e.getCurrent();
                Add(kvp.getKey(), kvp.getValue());
            }
        }
    }

    private void CopyEntries(Entry<TKey, TValue>[] entries, int count)
    {
        // Debug.Assert(_entries is not null);

        Entry<TKey, TValue>[] newEntries = _entries;
        int newCount = 0;
        for (int i = 0; i < count; i++)
        {
            int hashCode = entries[i].hashCode;
            if (entries[i].next >= -1)
            {
                // ref Entry entry = ref newEntries[newCount];
                // entry = entries[i];
                newEntries[newCount] = entries[i];
                var bucket = GetBucket(hashCode);
                entries[i].next = bucket.GetValue() - 1; // Value in _buckets is 1-based
                bucket.SetValue(newCount + 1);
                newCount++;
            }
        }

        _count = newCount;
        _freeCount = 0;
    }

    private ModifiableValueReference<TValue> FindValue(TKey key)
    {
        if (key == null)
        {
            throw new ArgumentNullException("key", "Key must not be null");
        }

        Entry<TKey, TValue> entry;

        if (_buckets != null)
        {
            int hashCode = _comparer.GetHashCode(key);
            int i = GetBucket(hashCode).GetValue();
            Entry<TKey, TValue>[] entries = _entries;
            int collisionCount = 0;
            i--; // Value in _buckets is 1-based; subtract 1 from i. We do it here so it fuses with the following conditional.
            do
            {
                // Test in if to drop range check for following array access
                if (i < 0 || i >= entries.length)
                {
                    return null;
                }

                entry = entries[i];
                if (entry.hashCode == hashCode && _comparer.Equals(entry.key, key))
                {
                    return new EntryValueReference<>(entry);
                }

                i = entry.next;

                collisionCount++;
            } while (collisionCount <= entries.length);

            // The chain of entries forms a loop; which means a concurrent update has happened.
            // Break out of the loop and throw, rather than looping forever.
            throw new InvalidOperationException("Concurrent operations are not allowed");
        }

        return null;
    }

    @MethodImpl(GetValue = {MethodImplOptions.AggressiveInlining})
    private ModifiableValueReference<Integer> GetBucket(int hashCode)
    {
        if (_buckets == null) {
            throw new NullPointerException();
        }
        int[] buckets = _buckets;
        return ModifiableValueReference.GetReferenceByArray(buckets, (int)(Integer.toUnsignedLong(hashCode) % buckets.length)); // buckets[(uint)hashCode % buckets.Length];
    }

    private boolean TryInsert(TKey key, TValue value, InsertionBehavior behavior)
    {
        // NOTE: this method is mirrored in CollectionsMarshal.GetValueRefOrAddDefault below.
        // If you make any changes here, make sure to keep that version in sync as well.

        if (key == null)
        {
            throw new ArgumentNullException("key", "Key must not be null");
        }

        if (_buckets == null)
        {
            Initialize(0);
        }
        // Debug.Assert(_buckets != null);

        Entry<TKey, TValue>[] entries = _entries;
        // Debug.Assert(entries != null, "expected entries to be non-null");

        IEqualityComparer<? super TKey> comparer = _comparer;
        // Debug.Assert(comparer is not null || typeof(TKey).IsValueType);
        int hashCode = (comparer == null) ? key.hashCode() : comparer.GetHashCode(key);

        int collisionCount = 0;
        var bucket = GetBucket(hashCode);
        int i = bucket.GetValue() - 1; // Value in _buckets is 1-based

        if (comparer == null) {
            comparer = EqualityComparer.GetDefault();
        }

        // Debug.Assert(comparer is not null);
        while (i > -1 && i < entries.length)
        {
            if (entries[i].hashCode == hashCode && comparer.Equals(entries[i].key, key))
            {
                if (behavior == InsertionBehavior.OverwriteExisting)
                {
                    entries[i].value = value;
                    return true;
                }

                if (behavior == InsertionBehavior.ThrowOnExisting)
                {
                    throw new ArgumentException("Attempted to overwrite existing key\nKey name: " + key);
                    // ThrowHelper.ThrowAddingDuplicateWithKeyArgumentException(key);
                }

                return false;
            }

            i = entries[i].next;

            collisionCount++;
            if (collisionCount > entries.length)
            {
                // The chain of entries forms a loop; which means a concurrent update has happened.
                // Break out of the loop and throw, rather than looping forever.
                throw new InvalidOperationException("Concurrent operations are not allowed");
                // ThrowHelper.ThrowInvalidOperationException_ConcurrentOperationsNotSupported();
            }
        }

        int index;
        if (_freeCount > 0)
        {
            index = _freeList;
            // Debug.Assert((StartOfFreeList - entries[_freeList].next) >= -1, "shouldn't overflow because `next` cannot underflow");
            _freeList = StartOfFreeList - entries[_freeList].next;
            _freeCount--;
        }
        else
        {
            int count = _count;
            if (count == entries.length)
            {
                Resize();
                bucket = GetBucket(hashCode);
            }
            index = count;
            _count = count + 1;
            entries = _entries;
        }

        var entry = entries[index];
        if (entry == null) {
            entry = entries[index] = new Entry<>();
        }
        entry.hashCode = hashCode;
        entry.next = bucket.GetValue() - 1; // Value in _buckets is 1-based
        entry.key = key;
        entry.value = value;
        bucket.SetValue(index + 1); // Value in _buckets is 1-based
        _version++;

        return true;
    }

    private void Resize() { Resize(HashHelpers.ExpandPrime(_count), false); }

    @SuppressWarnings("unchecked")
    private void Resize(int newSize, boolean forceNewHashCodes)
    {
        // Value types never rehash
        // Debug.Assert(!forceNewHashCodes || !typeof(TKey).IsValueType);
        // Debug.Assert(_entries != null, "_entries should be non-null");
        // Debug.Assert(newSize >= _entries.Length);

        Entry<TKey, TValue>[] entries = new Entry[newSize];

        int count = _count;
        Array.Copy(_entries, entries, count);

        if (forceNewHashCodes)
        {
            // Debug.Assert(_comparer is NonRandomizedStringEqualityComparer);
            IEqualityComparer<? super TKey> comparer = _comparer; // = (IEqualityComparer<TKey>)((NonRandomizedStringEqualityComparer)_comparer).GetRandomizedEqualityComparer();

            for (int i = 0; i < count; i++)
            {
                if (entries[i].next >= -1)
                {
                    entries[i].hashCode = comparer.GetHashCode(entries[i].key);
                }
            }
        }

        // Assign member variables after both arrays allocated to guard against corruption from OOM if second fails
        _buckets = new int[newSize];
        for (int i = 0; i < count; i++)
        {
            if (entries[i].next >= -1)
            {
                var bucket = GetBucket(entries[i].hashCode);
                entries[i].next = bucket.GetValue() - 1; // Value in _buckets is 1-based
                bucket.SetValue(i + 1);
            }
        }

        _entries = entries;
    }

    @Override
    public TValue getItem(TKey key)
            throws ArgumentNullException, KeyNotFoundException
    {
        var value = FindValue(key);
        if (value == null)
        {
            throw new KeyNotFoundException("Key with value " + key + " not found");
        } else {
            return value.GetValue();
        }
    }

    @Override
    public boolean getIsReadOnly() { return false; }

    @Override
    public int getCount() { return _count - _freeCount; }

    @Override
    public boolean ContainsKey(TKey key) { return FindValue(key) != null; }

    /**
     * Gets the total numbers of elements the internal data structure can hold without resizing.
     */
    public int GetCapacity() { return _entries == null ? 0 : _entries.length; }

    @Override
    public Enumerator<TKey, TValue> GetEnumerator() { return new Enumerator<>(this); }

    public boolean TryAdd(TKey key, TValue value) { return TryInsert(key, value, InsertionBehavior.None); }

    @NotNull
    public IEqualityComparer<? super TKey> GetComparer() { return _comparer == null ? EqualityComparer.GetDefault() : _comparer; }

    @Override
    public ICollection<TKey> getKeys() { return _keyCollection == null ? _keyCollection = new KeyCollection<>(this) : _keyCollection; }

    @Override
    public ICollection<TValue> getValues() { return _valueCollection == null ? _valueCollection = new ValueCollection<>(this) : _valueCollection; }

    @Override
    public void setItem(TKey key, TValue value) throws ArgumentNullException, NotSupportedException { TryInsert(key, value, InsertionBehavior.OverwriteExisting); }

    @Override
    public void Add(TKey key, TValue value) throws ArgumentNullException, ArgumentException, NotSupportedException { TryInsert(key, value, InsertionBehavior.OverwriteExisting); }

    public boolean ContainsValue(TValue value)
    {
        Entry<TKey, TValue>[] entries = _entries;
        if (value == null)
        {
            for (int i = 0; i < _count; i++)
            {
                if (entries[i].next >= -1 && entries[i].value == null)
                {
                    return true;
                }
            }
        }
        else
        {
            // Object type: Shared Generic, EqualityComparer<TValue>.Default won't devirtualize
            // https://github.com/dotnet/runtime/issues/10050
            // So cache in a local rather than get EqualityComparer per loop iteration
            EqualityComparer<TValue> defaultComparer = EqualityComparer.GetDefault();
            for (int i = 0; i < _count; i++)
            {
                if (entries[i].next >= -1 && defaultComparer.Equals(entries[i].value, value))
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean Remove_Ordinal2(TKey key)
            throws ArgumentNullException, NotSupportedException
    {
        // The overload Remove(TKey key, out TValue value) is a copy of this method with one additional
        // statement to copy the value for entry being removed into the output parameter.
        // Code has been intentionally duplicated for performance reasons.

        if (key == null)
        {
            throw new ArgumentNullException("key", "Key must not be null");
        }

        if (_buckets != null)
        {
            // Debug.Assert(_entries != null, "entries should be non-null");
            int collisionCount = 0;

            IEqualityComparer<? super TKey> comparer = _comparer == null ? EqualityComparer.GetDefault() : _comparer;
            // Debug.Assert(typeof(TKey).IsValueType || comparer is not null);
            int hashCode = comparer.GetHashCode(key);

            var bucket = GetBucket(hashCode);
            Entry<TKey, TValue>[] entries = _entries;
            int last = -1;
            int i = bucket.GetValue() - 1; // Value in buckets is 1-based
            while (i >= 0)
            {
                var entry = entries[i];

                if (entry.hashCode == hashCode && comparer.Equals(entry.key, key))
                {
                    if (last < 0)
                    {
                        bucket.SetValue(entry.next + 1); // Value in buckets is 1-based
                    }
                    else
                    {
                        entries[last].next = entry.next;
                    }

                    // Debug.Assert((StartOfFreeList - _freeList) < 0, "shouldn't underflow because max hashtable length is MaxPrimeArrayLength = 0x7FEFFFFD(2146435069) _freelist underflow threshold 2147483646");
                    entry.next = StartOfFreeList - _freeList;

                    if (!(entry.key instanceof ValueType))
                    {
                        entry.key = null;
                    }

                    if (!(entry.value instanceof ValueType))
                    {
                        entry.value = null;
                    }

                    _freeList = i;
                    _freeCount++;
                    return true;
                }

                last = i;
                i = entry.next;

                collisionCount++;
                if (collisionCount > entries.length)
                {
                    // The chain of entries forms a loop; which means a concurrent update has happened.
                    // Break out of the loop and throw, rather than looping forever.
                    throw new InvalidOperationException("Concurrent operations not allowed");
                    // ThrowHelper.ThrowInvalidOperationException_ConcurrentOperationsNotSupported();
                }
            }
        }
        return false;
    }

    public boolean Remove(TKey key, @DotNetByRefParameter(ByRefParameterType.OUT) @MaybeNullWhen(ReturnValue = false) ByRefParameter<TValue> value)
    {
        ByRefParameter.AssertEOut(value);
        // This overload is a copy of the overload Remove(TKey key) with one additional
        // statement to copy the value for entry being removed into the output parameter.
        // Code has been intentionally duplicated for performance reasons.

        if (key == null)
        {
            throw new ArgumentNullException("key", "Key must not be null");
        }

        if (_buckets != null)
        {
            // Debug.Assert(_entries != null, "entries should be non-null");
            int collisionCount = 0;

            IEqualityComparer<? super TKey> comparer = _comparer == null ? EqualityComparer.GetDefault() : _comparer;
            // Debug.Assert(typeof(TKey).IsValueType || comparer is not null);
            int hashCode = comparer.GetHashCode(key);

            var bucket = GetBucket(hashCode);
            Entry<TKey, TValue>[] entries = _entries;
            int last = -1;
            int i = bucket.GetValue() - 1; // Value in buckets is 1-based
            while (i >= 0)
            {
                var entry = entries[i];

                if (entry.hashCode == hashCode && comparer.Equals(entry.key, key))
                {
                    if (last < 0)
                    {
                        bucket.SetValue(entry.next + 1); // Value in buckets is 1-based
                    }
                    else
                    {
                        entries[last].next = entry.next;
                    }

                    value.Value = entry.value;

                    // Debug.Assert((StartOfFreeList - _freeList) < 0, "shouldn't underflow because max hashtable length is MaxPrimeArrayLength = 0x7FEFFFFD(2146435069) _freelist underflow threshold 2147483646");
                    entry.next = StartOfFreeList - _freeList;

                    if (!(entry.key instanceof ValueType))
                    {
                        entry.key = null;
                    }

                    if (!(entry.value instanceof ValueType))
                    {
                        entry.value = null;
                    }

                    _freeList = i;
                    _freeCount++;
                    return true;
                }

                last = i;
                i = entry.next;

                collisionCount++;
                if (collisionCount > entries.length)
                {
                    // The chain of entries forms a loop; which means a concurrent update has happened.
                    // Break out of the loop and throw, rather than looping forever.
                    throw new InvalidOperationException("Concurrent operations not allowed");
                    // ThrowHelper.ThrowInvalidOperationException_ConcurrentOperationsNotSupported();
                }
            }
        }
        return false;
    }

    @Override
    public boolean TryGetValue(TKey key, @DotNetByRefParameter(ByRefParameterType.OUT) @MaybeNullWhen(ReturnValue = false) ByRefParameter<TValue> value)
            throws ArgumentNullException
    {
        ByRefParameter.AssertEOut(value);
        var valRef = FindValue(key);
        if (valRef == null) {
            value.Value = null;
            return false;
        } else {
            valRef.SetToByReferenceParameter(value);
            return true;
        }
    }

    @Override
    public void Add(KeyValuePair<TKey, TValue> item)
    {
        ValueType.ValidateNonNullStructure(item);
        TryInsert(item.getKey(), item.getValue(), InsertionBehavior.ThrowOnExisting);
    }

    @Override
    public void Clear()
    {
        int count = _count;
        if (count > 0)
        {
            // Debug.Assert(_buckets != null, "_buckets should be non-null");
            // Debug.Assert(_entries != null, "_entries should be non-null");

            // Array.Clear(_buckets);
            Arrays.fill(_buckets, 0);

            _count = 0;
            _freeList = -1;
            _freeCount = 0;
            Array.Clear(_entries, 0, count);
        }
    }

    @Override
    public boolean Contains(KeyValuePair<TKey, TValue> item)
    {
        ValueType.ValidateNonNullStructure(item);
        var entry = FindValue(item.getKey());
        return entry != null && EqualityComparer.GetDefault().Equals(entry.GetValue(), item.getValue());
    }

    @Override
    public void CopyTo(KeyValuePair<TKey, TValue>[] array, int arrayIndex)
    {
        ArgumentNullException.ThrowIfNull(array, "array");

        if (arrayIndex < 0 || arrayIndex > array.length)
        {
            // ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
            throw new ArgumentOutOfRangeException("arrayIndex", arrayIndex, "Array index cannot be greater than array length or less than 0.");
        }

        if (array.length - arrayIndex < getCount())
        {
            // ThrowHelper.ThrowArgumentException(ExceptionResource.Arg_ArrayPlusOffTooSmall);
            throw new ArgumentException("The specified array length, the selected index and the dictionary size is too small to fit all the elements in array.");
        }

        int count = _count;
        Entry<TKey, TValue>[] entries = _entries;
        for (int i = 0; i < count; i++)
        {
            if (entries[i].next >= -1)
            {
                array[arrayIndex++] = new KeyValuePair<>(entries[i].key, entries[i].value);
            }
        }
    }

    @Override
    public boolean Remove(KeyValuePair<TKey, TValue> item)
    {
        ValueType.ValidateNonNullStructure(item);
        var value = FindValue(item.getKey());
        if (value != null && EqualityComparer.GetDefault().Equals(value.GetValue(), item.getValue())) {
            return this.Remove_Ordinal2(item.getKey());
        } else {
            return false;
        }
    }

    /**
     * Ensures that the dictionary can hold up to 'capacity' entries without any further expansion of its backing storage
     * @param capacity The number entries to ensure.
     * @return The actual number of ensured entries, may be more than the specified value.
     */
    public int EnsureCapacity(int capacity)
        throws ArgumentOutOfRangeException
    {
        if (capacity < 0)
        {
            // ThrowHelper.ThrowArgumentOutOfRangeException(ExceptionArgument.capacity);
            throw new ArgumentOutOfRangeException("capacity", capacity, "Capacity cannot be negative.");
        }

        int currentCapacity = _entries == null ? 0 : _entries.length;
        if (currentCapacity >= capacity)
        {
            return currentCapacity;
        }

        _version++;

        if (_buckets == null)
        {
            return Initialize(capacity);
        }

        int newSize = HashHelpers.GetPrime(capacity);
        Resize(newSize, false);
        return newSize;
    }

    /**
     * Sets the capacity of this dictionary to what it would be if it had been originally initialized with all its entries <br />
     * This method can be used to minimize the memory overhead
     * once it is known that no new elements will be added. <br />
     *
     * To allocate minimum size storage array, execute the following statements: <br />
     *
     * <pre>{@code
     * dictionary.Clear();
     * dictionary.TrimExcess();
     * }</pre>
     */
    public void TrimExcess() { TrimExcess(getCount()); }

    /**
     * Sets the capacity of this dictionary to hold up 'capacity' entries without any further expansion of its backing storage <br />
     * This method can be used to minimize the memory overhead
     * once it is known that no new elements will be added.
     * @throws ArgumentOutOfRangeException Passed capacity is lower than entries count.
     */
    public void TrimExcess(int capacity)
        throws ArgumentOutOfRangeException
    {
        if (capacity < getCount())
        {
            throw new ArgumentOutOfRangeException("capacity", capacity, "Capacity cannot be negative.");
        }

        int newSize = HashHelpers.GetPrime(capacity);
        Entry<TKey, TValue>[] oldEntries = _entries;
        int currentCapacity = oldEntries == null ? 0 : oldEntries.length;
        if (newSize >= currentCapacity)
        {
            return;
        }

        int oldCount = _count;
        _version++;
        Initialize(newSize);

        // Debug.Assert(oldEntries is not null);

        CopyEntries(oldEntries, oldCount);
    }

    @ClassIsDotNetStruct
    public static final class Enumerator<TKey, TValue>
        extends ValueType
        implements IEnumerator<KeyValuePair<TKey, TValue>>
    {
        private final int _version;
        private final Dictionary<TKey, TValue> _dictionary;
        private int _index;
        private boolean enum_finished;
        private KeyValuePair<TKey, TValue> _current;

        public Enumerator() { _version = 0; _dictionary = null; _current = new KeyValuePair<>(); _index = 0; enum_finished = false; }

        private Enumerator(Dictionary<TKey, TValue> dictionary)
        {
            _dictionary = dictionary;
            _version = dictionary._version;
            _index = 0;
            enum_finished = false;
            _current = new KeyValuePair<>();
        }

        @Override
        public KeyValuePair<TKey, TValue> getCurrent() { return _current; }

        @Override
        public boolean MoveNext()
                throws InvalidOperationException
        {
            if (_version != _dictionary._version) {
                // ThrowHelper.ThrowInvalidOperationException_InvalidOperation_EnumFailedVersion();
                throw new InvalidOperationException("The collection was modified after the enumeration begun.");
            } else if (enum_finished) {
                return false;
            } else {
                while (_index < _dictionary._count)
                {
                    var entry = _dictionary._entries[_index++];

                    if (entry.next >= -1)
                    {
                        _current = new KeyValuePair<>(entry.key, entry.value);
                        return true;
                    }
                }

                enum_finished = true;
                _current = new KeyValuePair<>();
                return false;
            }
        }

        @Override
        public void Reset()
                throws InvalidOperationException
        {
            if (_version != _dictionary._version) {
                // ThrowHelper.ThrowInvalidOperationException_InvalidOperation_EnumFailedVersion();
                throw new InvalidOperationException("The collection was modified after the enumeration begun.");
            } else {
                _index = 0;
                enum_finished = false;
                _current = new KeyValuePair<>();
            }
        }

        @Override
        public void Dispose() { }
    }

    public static final class KeyCollection<TKey, TValue>
        implements ICollection<TKey>, com.github.mdcdi1315.DotNetLayer.System.Collections.ICollection, IReadOnlyCollection<TKey>
    {
        private final Dictionary<TKey, TValue> _dictionary;

        public KeyCollection(Dictionary<TKey, TValue> dictionary)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(dictionary, "dictionary");
            _dictionary = dictionary;
        }

        @Override
        public boolean getIsReadOnly() { return true; }

        @Override
        public Object getSyncRoot() { return _dictionary; }

        @Override
        public boolean getIsSynchronized() { return false; }

        @Override
        public int getCount() { return _dictionary.getCount(); }

        @Override
        public boolean Contains(TKey item) { return _dictionary.ContainsKey(item); }

        @Override
        public void Clear() { throw new NotSupportedException("Read-only collection."); }

        @Override
        public void Add(TKey item) { throw new NotSupportedException("Read-only collection."); }

        @Override
        public Enumerator<TKey, TValue> GetEnumerator() { return new Enumerator<>(_dictionary); }

        @Override
        public boolean Remove(TKey item) { throw new NotSupportedException("Read-only collection."); }

        @Override
        public void CopyTo(Object array, int index)
                throws ArgumentException
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            int length = java.lang.reflect.Array.getLength(array);

            if (index < 0 || index > length)
            {
                // ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
                throw new ArgumentOutOfRangeException("index", index, "Index out of bounds.");
            }

            if (length - index < _dictionary.getCount())
            {
                // ThrowHelper.ThrowArgumentException(ExceptionResource.Arg_ArrayPlusOffTooSmall);
                throw new ArgumentException("The specified array length, the selected index and the dictionary size is too small to fit all the elements in array.");
            }

            int count = _dictionary._count;
            var entries = _dictionary._entries;
            for (int i = 0; i < count; i++)
            {
                if (entries[i].next >= -1) { java.lang.reflect.Array.set(array,index++, entries[i].key); }
            }
        }

        @Override
        public void CopyTo(TKey[] array, int arrayIndex)
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            if (arrayIndex < 0 || arrayIndex > array.length)
            {
                // ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
                throw new ArgumentOutOfRangeException("index", arrayIndex, "Index out of bounds.");
            }

            if (array.length - arrayIndex < _dictionary.getCount())
            {
                // ThrowHelper.ThrowArgumentException(ExceptionResource.Arg_ArrayPlusOffTooSmall);
                throw new ArgumentException("The specified array length, the selected index and the dictionary size is too small to fit all the elements in array.");
            }

            int count = _dictionary._count;
            var entries = _dictionary._entries;
            for (int i = 0; i < count; i++)
            {
                if (entries[i].next >= -1) array[arrayIndex++] = entries[i].key;
            }
        }

        @ClassIsDotNetStruct
        public static final class Enumerator<TKey, TValue>
            extends ValueType
            implements IEnumerator<TKey>
        {
            private final Dictionary.Enumerator<TKey, TValue> _enumerator;

            public Enumerator() { _enumerator = null; }

            public Enumerator(Dictionary<TKey, TValue> dictionary)
            {
                _enumerator = dictionary.GetEnumerator();
            }

            @Override
            public TKey getCurrent() { return _enumerator.getCurrent().getKey(); }

            @Override
            public void Reset() throws InvalidOperationException { _enumerator.Reset(); }

            @Override
            public boolean MoveNext() throws InvalidOperationException { return _enumerator.MoveNext(); }

            @Override
            public void Dispose() { _enumerator.Dispose(); }
        }
    }

    public static final class ValueCollection<TKey, TValue>
        implements ICollection<TValue>, com.github.mdcdi1315.DotNetLayer.System.Collections.ICollection, IReadOnlyCollection<TValue>
    {
        private final Dictionary<TKey, TValue> _dictionary;

        public ValueCollection(Dictionary<TKey, TValue> dictionary)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(dictionary, "dictionary");
            _dictionary = dictionary;
        }

        @Override
        public boolean getIsReadOnly() { return true; }

        @Override
        public Object getSyncRoot() { return _dictionary; }

        @Override
        public boolean getIsSynchronized() { return false; }

        @Override
        public int getCount() { return _dictionary.getCount(); }

        @Override
        public boolean Contains(TValue item) { return _dictionary.ContainsValue(item); }

        @Override
        public void Clear() { throw new NotSupportedException("Read-only collection."); }

        @Override
        public Enumerator<TKey, TValue> GetEnumerator() { return new Enumerator<>(_dictionary); }

        @Override
        public void Add(TValue item) { throw new NotSupportedException("Read-only collection."); }

        @Override
        public boolean Remove(TValue item) { throw new NotSupportedException("Read-only collection."); }

        @Override
        public void CopyTo(Object array, int index)
                throws ArgumentException
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            int length = java.lang.reflect.Array.getLength(array);

            if (index < 0 || index > length)
            {
                // ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
                throw new ArgumentOutOfRangeException("index", index, "Index out of bounds.");
            }

            if (length - index < _dictionary.getCount())
            {
                // ThrowHelper.ThrowArgumentException(ExceptionResource.Arg_ArrayPlusOffTooSmall);
                throw new ArgumentException("The specified array length, the selected index and the dictionary size is too small to fit all the elements in array.");
            }

            int count = _dictionary._count;
            var entries = _dictionary._entries;
            for (int i = 0; i < count; i++)
            {
                if (entries[i].next >= -1) { java.lang.reflect.Array.set(array,index++, entries[i].value); }
            }
        }

        @Override
        public void CopyTo(TValue[] array, int arrayIndex)
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            if (arrayIndex < 0 || arrayIndex > array.length)
            {
                // ThrowHelper.ThrowIndexArgumentOutOfRange_NeedNonNegNumException();
                throw new ArgumentOutOfRangeException("index", arrayIndex, "Index out of bounds.");
            }

            if (array.length - arrayIndex < _dictionary.getCount())
            {
                // ThrowHelper.ThrowArgumentException(ExceptionResource.Arg_ArrayPlusOffTooSmall);
                throw new ArgumentException("The specified array length, the selected index and the dictionary size is too small to fit all the elements in array.");
            }

            int count = _dictionary._count;
            var entries = _dictionary._entries;
            for (int i = 0; i < count; i++)
            {
                if (entries[i].next >= -1) array[arrayIndex++] = entries[i].value;
            }
        }

        @ClassIsDotNetStruct
        public static final class Enumerator<TKey, TValue>
                extends ValueType
                implements IEnumerator<TValue>
        {
            private final Dictionary.Enumerator<TKey, TValue> _enumerator;

            public Enumerator() { _enumerator = null; }

            public Enumerator(Dictionary<TKey, TValue> dictionary)
            {
                _enumerator = dictionary.GetEnumerator();
            }

            @Override
            public TValue getCurrent() { return _enumerator.getCurrent().getValue(); }

            @Override
            public void Reset() throws InvalidOperationException { _enumerator.Reset(); }

            @Override
            public boolean MoveNext() throws InvalidOperationException { return _enumerator.MoveNext(); }

            @Override
            public void Dispose() { _enumerator.Dispose(); }
        }
    }

    @NotNull
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder()
                .append(getClass().getSimpleName())
                .append("<?, ?> { Count = ")
                .append(getCount())
                .append(", Capacity = ")
                .append(GetCapacity())
                .append(", Items = [ ");

        int _index = 0;
        while (_index < _count)
        {
            var entry = _entries[_index++];

            if (entry.next >= -1)
            {
                builder.append("{")
                     .append(entry.key)
                     .append(", ")
                     .append(entry.value)
                     .append("}, ");
            }
        }

        builder.append("] }");
        return builder.toString();
    }
}
