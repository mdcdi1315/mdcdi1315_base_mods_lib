package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class Hashtable
    implements IDictionary, ICloneable
{
    /*
          This Hashtable uses double hashing.  There are hashsize buckets in the
          table, and each bucket can contain 0 or 1 element.  We use a bit to mark
          whether there's been a collision when we inserted multiple elements
          (ie, an inserted item was hashed at least a second time and we probed
          this bucket, but it was already in use).  Using the collision bit, we
          can terminate lookups & removes for elements that aren't in the hash
          table more quickly.  We steal the most significant bit from the hash code
          to store the collision bit.

          Our hash function is of the following form:

          h(key, n) = h1(key) + n*h2(key)

          where n is the number of times we've hit a collided bucket and rehashed
          (on this particular lookup).  Here are our hash functions:

          h1(key) = GetHash(key);  // default implementation calls key.GetHashCode();
          h2(key) = 1 + (((h1(key) >> 5) + 1) % (hashsize - 1));

          The h1 can return any number.  h2 must return a number between 1 and
          hashsize - 1 that is relatively prime to hashsize (not a problem if
          hashsize is prime).  (Knuth's Art of Computer Programming, Vol. 3, p. 528-9)
          If this is true, then we are guaranteed to visit every bucket in exactly
          hashsize probes, since the least common multiple of hashsize and h2(key)
          will be hashsize * h2(key).  (This is the first number where adding h2 to
          h1 mod hashsize will be 0 and we will search the same bucket twice).

          We previously used a different h2(key, n) that was not constant.  That is a
          horrifically bad idea, unless you can prove that series will never produce
          any identical numbers that overlap when you mod them by hashsize, for all
          subranges from i to i+hashsize, for all i.  It's not worth investigating,
          since there was no clear benefit from using that hash function, and it was
          broken.

          For efficiency reasons, we've implemented this by storing h1 and h2 in a
          temporary, and setting a variable called seed equal to h1.  We do a probe,
          and if we collided, we simply add h2 to seed each time through the loop.

          A good test for h2() is to subclass Hashtable, provide your own implementation
          of GetHash() that returns a constant, then add many items to the hash table.
          Make sure Count equals the number of items you inserted.

          Note that when we remove an item from the hash table, we set the key
          equal to buckets, if there was a collision in this bucket.  Otherwise
          we'd either wipe out the collision bit, or we'd still have an item in
          the hash table.

           --
        */

    private static final int InitialSize = 3;

    // Deleted entries have their key set to buckets

    // The hash table data.
    // This cannot be serialized
    @ClassIsDotNetStruct
    private static final class Bucket
        extends ValueType
    {
        public Bucket() { }

        @AllowNull
        public Object key;
        @AllowNull
        public Object val;
        public int hash_coll;   // Store hash code; sign bit means there was a collision.
    }

    private Bucket[] _buckets = null;

    // The total number of entries in the hash table.
    private int _count;

    // The total number of collision bits set in the hashtable
    private int _occupancy;

    private int _loadsize;
    private float _loadFactor;

    private volatile int _version;
    private volatile boolean _isWriterInProgress;

    @AllowNull
    private ICollection _keys;
    @AllowNull
    private ICollection _values;

    @AllowNull
    private IEqualityComparer _keycomparer;

    @AllowNull
    protected IEqualityComparer GetEqualityComparer() { return _keycomparer; }

    // Internal method to get the hash code for an Object.  This will call
    // GetHashCode() on each object if you haven't provided an IHashCodeProvider
    // instance.  Otherwise, it calls hcp.GetHashCode(obj).
    protected int GetHash(Object key)
    {
        if (_keycomparer != null)
            return _keycomparer.GetHashCode(key);
        return key.hashCode();
    }

    // Internal method to compare two keys.  If you have provided an IComparer
    // instance in the constructor, this method will call comparer.Compare(item, key).
    // Otherwise, it will call item.Equals(key).
    //
    protected boolean KeyEquals(Object item, Object key)
    {
        // Debug.Assert(key != null, "key can't be null here!");
        if (_buckets == item) {
            return false;
        } else if (item == key) {
            return true;
        } else if (_keycomparer != null) {
            return _keycomparer.Equals(item, key);
        } else {
            return item != null && item.equals(key);
        }
    }

    // Note: this constructor is a bogus constructor that does nothing
    // and is for use only with SyncHashtable.
    private Hashtable(@SuppressWarnings("unused") boolean _bogus)
    {
    }

    // Constructs a new hashtable. The hashtable is created with an initial
    // capacity of zero and a load factor of 1.0.
    public Hashtable()
    {
        this(0, 1.0f);
    }

    // Constructs a new hashtable with the given initial capacity and a load
    // factor of 1.0. The capacity argument serves as an indication of
    // the number of entries the hashtable will contain. When this number (or
    // an approximation) is known, specifying it in the constructor can
    // eliminate a number of resizing operations that would otherwise be
    // performed when elements are added to the hashtable.
    //
    public Hashtable(int capacity)
    {
        this(capacity, 1.0f);
    }

    // Constructs a new hashtable with the given initial capacity and load
    // factor. The capacity argument serves as an indication of the
    // number of entries the hashtable will contain. When this number (or an
    // approximation) is known, specifying it in the constructor can eliminate
    // a number of resizing operations that would otherwise be performed when
    // elements are added to the hashtable. The loadFactor argument
    // indicates the maximum ratio of hashtable entries to hashtable buckets.
    // Smaller load factors cause faster average lookup times at the cost of
    // increased memory consumption. A load factor of 1.0 generally provides
    // the best balance between speed and size.
    //
    public Hashtable(int capacity, float loadFactor)
    {
        // ArgumentOutOfRangeException.ThrowIfNegative(capacity);
        if (capacity < 0) {
            throw new ArgumentOutOfRangeException("capacity", "Capacity cannot be a negative value");
        }
        if (!(loadFactor >= 0.1f && loadFactor <= 1.0f))
            throw new ArgumentOutOfRangeException("loadFactor", "Hash table load factor must be in range [0.1, 1.0]");

        // Based on perf work, .72 is the optimal load factor for this table.
        _loadFactor = 0.72f * loadFactor;

        double rawsize = capacity / _loadFactor;
        if (rawsize > Integer.MAX_VALUE)
            throw new ArgumentException("The Hash table capacity overflows.", "capacity");

        // Avoid awfully small sizes
        int hashsize = (rawsize > InitialSize) ? HashHelpers.GetPrime((int)rawsize) : InitialSize;
        _buckets = new Bucket[hashsize];
        for (int I = 0; I < _buckets.length; I++) { _buckets[I] = new Bucket(); }

        _loadsize = (int)(_loadFactor * hashsize);
        _isWriterInProgress = false;
        // Based on the current algorithm, loadsize must be less than hashsize.
        // Debug.Assert(_loadsize < hashsize, "Invalid hashtable loadsize!");
    }

    public Hashtable(int capacity, float loadFactor, @AllowNull IEqualityComparer equalityComparer)
    {
        this(capacity, loadFactor);
        _keycomparer = equalityComparer;
    }

    public Hashtable(@AllowNull IEqualityComparer equalityComparer)
    {
        this(0, 1.0f, equalityComparer);
    }

    public Hashtable(int capacity, @AllowNull IEqualityComparer equalityComparer)
    {
        this(capacity, 1.0f, equalityComparer);
    }

    // Constructs a new hashtable containing a copy of the entries in the given
    // dictionary. The hashtable is created with a load factor of 1.0.
    //
    public Hashtable(IDictionary d)
    {
        this(d, 1.0f);
    }

    // Constructs a new hashtable containing a copy of the entries in the given
    // dictionary. The hashtable is created with the given load factor.
    //
    public Hashtable(IDictionary d, float loadFactor)
    {
        this(d, loadFactor, null);
    }

    public Hashtable(IDictionary d, @AllowNull IEqualityComparer equalityComparer)
    {
        this(d, 1.0f, equalityComparer);
    }


    public Hashtable(IDictionary d, float loadFactor, @AllowNull IEqualityComparer equalityComparer)
    {
        this(d == null ? 0 : d.getCount(), loadFactor, equalityComparer);
        ArgumentNullException.ThrowIfNull(d);

        IDictionaryEnumerator e = d.GetEnumerator();
        while (e.MoveNext())
            Add(e.GetKey(), e.GetValue());
    }

    // ?InitHash? is basically an implementation of classic DoubleHashing (see http://en.wikipedia.org/wiki/Double_hashing)
    //
    // 1) The only ?correctness? requirement is that the ?increment? used to probe
    //    a. Be non-zero
    //    b. Be relatively prime to the table size ?hashSize?. (This is needed to ensure you probe all entries in the table before you ?wrap? and visit entries already probed)
    // 2) Because we choose table sizes to be primes, we just need to ensure that the increment is 0 < incr < hashSize
    //
    // Thus this function would work: Incr = 1 + (seed % (hashSize-1))
    //
    // While this works well for ?uniformly distributed? keys, in practice, non-uniformity is common.
    // In particular in practice we can see ?mostly sequential? where you get long clusters of keys that ?pack?.
    // To avoid bad behavior you want it to be the case that the increment is ?large? even for ?small? values (because small
    // values tend to happen more in practice). Thus we multiply ?seed? by a number that will make these small values
    // bigger (and not hurt large values). We picked HashPrime (101) because it was prime, and if ?hashSize-1? is not a multiple of HashPrime
    // (enforced in GetPrime), then incr has the potential of being every value from 1 to hashSize-1. The choice was largely arbitrary.
    //
    // Computes the hash function:  H(key, i) = h1(key) + i*h2(key, hashSize).
    // The out parameter seed is h1(key), while the out parameter
    // incr is h2(key, hashSize).  Callers of this function should
    // add incr each time through a loop.
    private long InitHash(
            Object key, int hashsize,
            @DotNetByRefParameter(ByRefParameterType.OUT) ByRefParameter<Long> seed,
            @DotNetByRefParameter(ByRefParameterType.OUT) ByRefParameter<Long> incr
    )
    {
        ByRefParameter.AssertEOut(seed);
        ByRefParameter.AssertEOut(incr);
        // Hashcode must be positive.  Also, we must not use the sign bit, since
        // that is used for the collision bit.
        long hashcode = GetHash(key) & 0x7FFFFFFF;
        seed.Value = hashcode;
        // Restriction: incr MUST be between 1 and hashsize - 1, inclusive for
        // the modular arithmetic to work correctly.  This guarantees you'll
        // visit every bucket in the table exactly once within hashsize
        // iterations.  Violate this and it'll cause obscure bugs forever.
        // If you change this calculation for h2(key), update putEntry too!
        incr.Value = (1 + ((seed.Value * HashHelpers.HashPrime) % (hashsize - 1)));
        return hashcode;
    }

    @Override
    public int getCount() { return _count; }

    @Override
    public Object getSyncRoot() { return this; }

    @Override
    public boolean GetIsReadOnly() { return false; }

    @Override
    public boolean GetIsFixedSize() { return false; }

    @Override
    public boolean getIsSynchronized() { return false; }

    @Override
    public IDictionaryEnumerator GetEnumerator() { return new HashtableEnumerator(this); }

    // Adds an entry with the given key and value to this hashtable. An
    // ArgumentException is thrown if the key is null or if the key is already
    // present in the hashtable.
    //
    @Override
    public void Add(Object key, @AllowNull Object value) { Insert(key, value, true); }

    @Override
    public boolean Contains(Object key) throws ArgumentNullException { return ContainsKey(key); }

    @Override
    public ICollection GetKeys()
    {
        if (_keys == null)
        {
            _keys = new KeyCollection(this);
        }
        return _keys;
    }

    @Override
    public ICollection GetValues()
    {
        if (_values == null)
        {
            _values = new ValueCollection(this);
        }
        return _values;
    }

    // Removes all entries from this hashtable.
    public void Clear()
    {
        // Debug.Assert(!_isWriterInProgress, "Race condition detected in usages of Hashtable - multiple threads appear to be writing to a Hashtable instance simultaneously!  Don't do that - use Hashtable.Synchronized.");

        if (_count == 0 && _occupancy == 0)
            return;

        _isWriterInProgress = true;
        for (Bucket bucket : _buckets)
        {
            bucket.hash_coll = 0;
            bucket.key = null;
            bucket.val = null;
        }

        _count = 0;
        _occupancy = 0;
        UpdateVersion();
        _isWriterInProgress = false;
    }

    // Clone returns a virtually identical copy of this hash table.  This does
    // a shallow copy - the Objects in the table aren't cloned, only the references
    // to those Objects.
    @NotNull
    public Hashtable Clone()
    {
        Bucket[] lbuckets = _buckets;
        Hashtable ht = new Hashtable(_count, _keycomparer);
        ht._version = _version;
        ht._loadFactor = _loadFactor;
        ht._count = 0;

        int bucket = lbuckets.length;
        while (bucket > 0)
        {
            bucket--;
            Object keyv = lbuckets[bucket].key;
            if ((keyv != null) && (keyv != lbuckets))
            {
                SetItem(keyv, lbuckets[bucket].val);
            }
        }

        return ht;
    }

    // Checks if this hashtable contains an entry with the given key.  This is
    // an O(1) operation.
    //
    public boolean ContainsKey(Object key)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        // Take a snapshot of buckets, in case another thread resizes table
        Bucket[] lbuckets = _buckets;
        ByRefParameter<Long>
                out_uint_seed = new ByRefParameter<>(),
                out_uint_incr = new ByRefParameter<>();
        long hashcode = InitHash(key, lbuckets.length, out_uint_seed, out_uint_incr);
        int ntry = 0;

        Bucket b;
        int bucketNumber = (int)(out_uint_seed.Value % lbuckets.length);
        do
        {
            b = lbuckets[bucketNumber];
            if (b.key == null)
            {
                return false;
            }
            if (((b.hash_coll & 0x7FFFFFFF) == hashcode) &&
                    KeyEquals(b.key, key))
                return true;
            bucketNumber = (int)(((long)bucketNumber + out_uint_incr.Value) % lbuckets.length);
        } while (b.hash_coll < 0 && ++ntry < lbuckets.length);
        return false;
    }

    // Copies the keys of this hashtable to a given array starting at a given
    // index. This method is used by the implementation of the CopyTo method in
    // the KeyCollection class.
    private void CopyKeys(Object[] array, int arrayIndex)
    {
        // Debug.Assert(array != null);
        // Debug.Assert(array.Rank == 1);

        Bucket[] lbuckets = _buckets;
        for (int i = lbuckets.length; --i >= 0;)
        {
            Object keyv = lbuckets[i].key;
            if ((keyv != null) && (keyv != _buckets))
            {
                array[arrayIndex++] = keyv;
            }
        }
    }

    // Copies the keys of this hashtable to a given array starting at a given
    // index. This method is used by the implementation of the CopyTo method in
    // the KeyCollection class.
    private void CopyEntries(Object[] array, int arrayIndex)
    {
        // Debug.Assert(array != null);
        // Debug.Assert(array.Rank == 1);

        Bucket[] lbuckets = _buckets;
        for (int i = lbuckets.length; --i >= 0;)
        {
            Object keyv = lbuckets[i].key;
            if ((keyv != null) && (keyv != _buckets))
            {
                DictionaryEntry entry = new DictionaryEntry(keyv, lbuckets[i].val);
                array[arrayIndex++] = entry;
            }
        }
    }

    // Copies the values in this hash table to an array at
    // a given index.  Note that this only copies values, and not keys.
    public void CopyTo(Object array, int arrayIndex)
    {
        ArgumentNullException.ThrowIfNull(array);

        if (arrayIndex < 0) {
            throw new ArgumentOutOfRangeException("arrayIndex", "arrayIndex cannot be a negative value.");
        }

        Object[] array_target = (Object[])array;

        if (array_target.length - arrayIndex < getCount())
            throw new ArgumentException("The given array is too small to accommodate all the Hashtable elements.");

        CopyEntries(array_target, arrayIndex);
    }

    // Copies the values of this hashtable to a given array starting at a given
    // index. This method is used by the implementation of the CopyTo method in
    // the ValueCollection class.
    private void CopyValues(Object[] array, int arrayIndex)
    {
        // Debug.Assert(array != null);
        // Debug.Assert(array.Rank == 1);

        Bucket[] lbuckets = _buckets;
        for (int i = lbuckets.length; --i >= 0;)
        {
            Object keyv = lbuckets[i].key;
            if ((keyv != null) && (keyv != _buckets))
            {
                array[arrayIndex++] = lbuckets[i].val;
            }
        }
    }

    @MaybeNull
    public Object GetItem(Object key)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        // Take a snapshot of buckets, in case another thread does a resize
        Bucket[] lbuckets = _buckets;
        ByRefParameter<Long>
                out_uint_seed = new ByRefParameter<>(),
                out_uint_incr = new ByRefParameter<>();
        long hashcode = InitHash(key, lbuckets.length, out_uint_seed, out_uint_incr);
        int ntry = 0;

        Bucket b;
        int bucketNumber = (int)(out_uint_seed.Value % lbuckets.length);
        do
        {
            int currentversion;

            // A read operation on hashtable has three steps:
            //        (1) calculate the hash and find the slot number.
            //        (2) compare the hashcode, if equal, go to step 3. Otherwise end.
            //        (3) compare the key, if equal, go to step 4. Otherwise end.
            //        (4) return the value contained in the bucket.
            //     After step 3 and before step 4. A writer can kick in a remove the old item and add a new one
            //     in the same bucket. So in the reader we need to check if the hash table is modified during above steps.
            //
            // Writers (Insert, Remove, Clear) will set 'isWriterInProgress' flag before it starts modifying
            // the hashtable and will clear the flag when it is done.  When the flag is cleared, the 'version'
            // will be increased.  We will repeat the reading if a writer is in progress or done with the modification
            // during the read.
            //
            // Our memory model guarantee if we pick up the change in bucket from another processor,
            // we will see the 'isWriterProgress' flag to be true or 'version' is changed in the reader.
            //
            while (true)
            {
                // this is volatile read, following memory accesses can not be moved ahead of it.
                currentversion = _version;
                b = lbuckets[bucketNumber];

                if (!_isWriterInProgress && (currentversion == _version))
                    break;

                Thread.onSpinWait();
            }

            if (b.key == null) { return null; }

            if (((b.hash_coll & 0x7FFFFFFF) == hashcode) &&
                    KeyEquals(b.key, key))
                return b.val;
            bucketNumber = (int)(((long)bucketNumber + out_uint_incr.Value) % lbuckets.length);
        } while (b.hash_coll < 0 && ++ntry < lbuckets.length);

        return null;
    }

    public void SetItem(Object key, @AllowNull Object value) { Insert(key, value, false); }

    // Increases the bucket count of this hashtable. This method is called from
    // the Insert method when the actual load factor of the hashtable reaches
    // the upper limit specified when the hashtable was constructed. The number
    // of buckets in the hashtable is increased to the smallest prime number
    // that is larger than twice the current number of buckets, and the entries
    // in the hashtable are redistributed into the new buckets using the cached
    // hashcodes.
    private void expand()
    {
        int rawsize = HashHelpers.ExpandPrime(_buckets.length);
        rehash(rawsize);
    }

    // We occasionally need to rehash the table to clean up the collision bits.
    private void rehash() { rehash(_buckets.length); }

    private void UpdateVersion()
    {
        // Version might become negative when version is int.MaxValue, but the oddity will be still be correct.
        // So we don't need to special case this.
        synchronized (this) { _version++; }
    }

    private void rehash(int newsize)
    {
        // reset occupancy
        _occupancy = 0;

        // Don't replace any internal state until we've finished adding to the
        // new Bucket[].  This serves two purposes:
        //   1) Allow concurrent readers to see valid hashtable contents
        //      at all times
        //   2) Protect against an OutOfMemoryException while allocating this
        //      new Bucket[].
        Bucket[] newBuckets = new Bucket[newsize];
        for (int I = 0; I < _buckets.length; I++) { newBuckets[I] = new Bucket(); }

        // rehash table into new buckets
        int nb;
        for (nb = 0; nb < _buckets.length; nb++)
        {
            Bucket oldb = _buckets[nb];
            if ((oldb.key != null) && (oldb.key != _buckets))
            {
                int hashcode = oldb.hash_coll & 0x7FFFFFFF;
                putEntry(newBuckets, oldb.key, oldb.val, hashcode);
            }
        }

        // New Bucket[] is good to go - replace buckets and other internal state.
        _isWriterInProgress = true;
        _buckets = newBuckets;
        _loadsize = (int)(_loadFactor * newsize);
        UpdateVersion();
        _isWriterInProgress = false;
        // minimum size of hashtable is 3 now and maximum loadFactor is 0.72 now.
        // Debug.Assert(_loadsize < newsize, "Our current implementation means this is not possible.");
    }

    private void putEntry(Bucket[] newBuckets, Object key, @AllowNull Object nvalue, int hashcode)
    {
        // Debug.Assert(hashcode >= 0);  // make sure collision bit (sign bit) wasn't set.

        int incr = (1 + ((hashcode * HashHelpers.HashPrime) % (newBuckets.length - 1)));
        int bucketNumber = (hashcode % newBuckets.length);
        while (true)
        {
            if ((newBuckets[bucketNumber].key == null) || (newBuckets[bucketNumber].key == _buckets))
            {
                newBuckets[bucketNumber].val = nvalue;
                newBuckets[bucketNumber].key = key;
                newBuckets[bucketNumber].hash_coll |= hashcode;
                return;
            }

            if (newBuckets[bucketNumber].hash_coll >= 0)
            {
                newBuckets[bucketNumber].hash_coll |= 0x80000000;
                _occupancy++;
            }
            bucketNumber = (int)(((long)bucketNumber + incr) % newBuckets.length);
        }
    }

    // Removes an entry from this hashtable. If an entry with the specified
    // key exists in the hashtable, it is removed. An ArgumentException is
    // thrown if the key is null.
    //
    public void Remove(Object key)
    {
        ArgumentNullException.ThrowIfNull(key, "key");

        // Debug.Assert(!_isWriterInProgress, "Race condition detected in usages of Hashtable - multiple threads appear to be writing to a Hashtable instance simultaneously!  Don't do that - use Hashtable.Synchronized.");

        // Assuming only one concurrent writer, write directly into buckets.
        ByRefParameter<Long>
                out_uint_seed = new ByRefParameter<>(),
                out_uint_incr = new ByRefParameter<>();
        long hashcode = InitHash(key, _buckets.length, out_uint_seed, out_uint_incr);
        int ntry = 0;

        Bucket b;
        int bn = (int)(out_uint_seed.Value % _buckets.length);  // bucketNumber
        do
        {
            b = _buckets[bn];
            if (((b.hash_coll & 0x7FFFFFFF) == hashcode) &&
                    KeyEquals(b.key, key))
            {
                _isWriterInProgress = true;
                // Clear hash_coll field, then key, then value
                _buckets[bn].hash_coll &= 0x80000000;
                if (_buckets[bn].hash_coll != 0)
                {
                    _buckets[bn].key = _buckets;
                }
                else
                {
                    _buckets[bn].key = null;
                }
                _buckets[bn].val = null;  // Free object references sooner & simplify ContainsValue.
                _count--;
                UpdateVersion();
                _isWriterInProgress = false;
                return;
            }
            bn = (int)(((long)bn + out_uint_incr.Value) % _buckets.length);
        } while (b.hash_coll < 0 && ++ntry < _buckets.length);
    }

    // Returns a thread-safe wrapper for a Hashtable.
    //
    public static Hashtable Synchronized(Hashtable table)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(table, "table");

        return new SyncHashtable(table);
    }

    // Inserts an entry into this hashtable. This method is called from the Set
    // and Add methods. If the add parameter is true and the given key already
    // exists in the hashtable, an exception is thrown.
    private void Insert(Object key, @AllowNull Object nvalue, boolean add)
    {
        ArgumentNullException.ThrowIfNull(key);

        if (_count >= _loadsize)
        {
            expand();
        }
        else if (_occupancy > _loadsize && _count > 100)
        {
            rehash();
        }

        // Assume we only have one thread writing concurrently.  Modify
        // buckets to contain new data, as long as we insert in the right order.

        ByRefParameter<Long>
                out_uint_seed = new ByRefParameter<>(),
                out_uint_incr = new ByRefParameter<>();
        long hashcode = InitHash(key, _buckets.length, out_uint_seed, out_uint_incr);
        int ntry = 0;
        int emptySlotNumber = -1; // We use the empty slot number to cache the first empty slot. We chose to reuse slots
        // create by remove that have the collision bit set over using up new slots.
        int bucketNumber = (int)(out_uint_seed.Value % _buckets.length);
        do
        {
            // Set emptySlot number to current bucket if it is the first available bucket that we have seen
            // that once contained an entry and also has had a collision.
            // We need to search this entire collision chain because we have to ensure that there are no
            // duplicate entries in the table.
            if (emptySlotNumber == -1 && (_buckets[bucketNumber].key == _buckets) && (_buckets[bucketNumber].hash_coll < 0))// (((buckets[bucketNumber].hash_coll & unchecked(0x80000000))!=0)))
                emptySlotNumber = bucketNumber;

            // Insert the key/value pair into this bucket if this bucket is empty and has never contained an entry
            // OR
            // This bucket once contained an entry but there has never been a collision
            if ((_buckets[bucketNumber].key == null) ||
                    (_buckets[bucketNumber].key == _buckets && ((_buckets[bucketNumber].hash_coll & 0x80000000) == 0)))
            {
                // If we have found an available bucket that has never had a collision, but we've seen an available
                // bucket in the past that has the collision bit set, use the previous bucket instead
                if (emptySlotNumber != -1) // Reuse slot
                    bucketNumber = emptySlotNumber;

                // We pretty much have to insert in this order.  Don't set hash
                // code until the value & key are set appropriately.
                _isWriterInProgress = true;
                _buckets[bucketNumber].val = nvalue;
                _buckets[bucketNumber].key = key;
                _buckets[bucketNumber].hash_coll |= (int)hashcode;
                _count++;
                UpdateVersion();
                _isWriterInProgress = false;

                return;
            }

            // The current bucket is in use
            // OR
            // it is available, but has had the collision bit set and we have already found an available bucket
            if (((_buckets[bucketNumber].hash_coll & 0x7FFFFFFF) == hashcode) &&
                    KeyEquals(_buckets[bucketNumber].key, key))
            {
                if (add)
                {
                    throw new ArgumentException(StringUtils.Format("Attempted to add a new entry of the same name.\nName In Dictionary: {0}\nName of the new entry: {1}", _buckets[bucketNumber].key, key));
                }
                _isWriterInProgress = true;
                _buckets[bucketNumber].val = nvalue;
                UpdateVersion();
                _isWriterInProgress = false;

                return;
            }

            // The current bucket is full, and we have therefore collided.  We need to set the collision bit
            // unless we have remembered an available slot previously.
            if (emptySlotNumber == -1)
            {// We don't need to set the collision bit here since we already have an empty slot
                if (_buckets[bucketNumber].hash_coll >= 0)
                {
                    _buckets[bucketNumber].hash_coll |= 0x80000000;
                    _occupancy++;
                }
            }

            bucketNumber = (int)(((long)bucketNumber + out_uint_incr.Value) % _buckets.length);
        } while (++ntry < _buckets.length);

        // This code is here if and only if there were no buckets without a collision bit set in the entire table
        if (emptySlotNumber != -1)
        {
            // We pretty much have to insert in this order.  Don't set hash
            // code until the value & key are set appropriately.
            _isWriterInProgress = true;
            _buckets[emptySlotNumber].val = nvalue;
            _buckets[emptySlotNumber].key = key;
            _buckets[emptySlotNumber].hash_coll |= (int)hashcode;
            _count++;
            UpdateVersion();
            _isWriterInProgress = false;

            return;
        }

        // If you see this assert, make sure load factor & count are reasonable.
        // Then verify that our double hash function (h2, described at top of file)
        // meets the requirements described above. You should never see this assert.
        // Debug.Fail("hash table insert failed!  Load factor too high, or our double hashing function is incorrect.");
        throw new InvalidOperationException("Hash insertion failed");
    }

    // Synchronized wrapper for hashtable
    private static final class SyncHashtable
            extends Hashtable
    {
        private final Hashtable _table;

        private SyncHashtable(Hashtable table)
        {
            super(false);
            _table = table;
        }

        @Override
        public int getCount() { return _table.getCount(); }

        @Override
        public boolean getIsSynchronized() { return true; }

        @Override
        public Object getSyncRoot() { return _table.getSyncRoot(); }

        @Override
        public boolean GetIsReadOnly() { return _table.GetIsReadOnly(); }

        @Override
        public boolean GetIsFixedSize() { return _table.GetIsFixedSize(); }

        @Override
        public boolean Contains(Object key) { return _table.Contains(key); }

        @Override
        public void Clear() { synchronized (_table.getSyncRoot()) { _table.Clear(); } }

        @Override
        public IDictionaryEnumerator GetEnumerator() { return _table.GetEnumerator(); }

        @Override
        public void Remove(Object key) { synchronized (_table.getSyncRoot()) { _table.Remove(key); } }

        @Override
        public Object GetItem(Object key) throws ArgumentNullException { return _table.GetItem(key); }

        @Override
        public ICollection GetKeys() { synchronized (_table.getSyncRoot()) { return _table.GetKeys(); } }

        @Override
        public ICollection GetValues() { synchronized (_table.getSyncRoot()) { return _table.GetValues(); } }

        @Override
        public Hashtable Clone() { synchronized (_table.getSyncRoot()) { return Synchronized(_table.Clone()); } }

        @Override
        public void SetItem(Object key, Object value) { synchronized (_table.getSyncRoot()) { super.SetItem(key, value); } }

        @Override
        public void Add(Object key, @AllowNull Object value) { synchronized (_table.getSyncRoot()) { _table.Add(key, value); } }

        @Override
        public void CopyTo(Object array, int arrayIndex) { synchronized (_table.getSyncRoot()) { _table.CopyTo(array, arrayIndex); } }

        @Override
        public boolean ContainsKey(Object key)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(key, "key");
            return _table.ContainsKey(key);
        }
    }

    // Implements a Collection for the keys of a hashtable. An instance of this
    // class is created by the GetKeys method of a hashtable.
    private record KeyCollection(Hashtable _hashtable)
                implements ICollection
    {
        private static final class Enumerator
                implements IEnumerator
        {
            private final HashtableEnumerator en;

            private Enumerator(HashtableEnumerator en) { this.en = en; }

            @Override
            public Object getCurrent() { return en.GetKey(); }

            @Override
            public void Reset() throws InvalidOperationException { en.Reset(); }

            @Override
            public boolean MoveNext() throws InvalidOperationException { return en.MoveNext(); }
        }

        @Override
        public void CopyTo(Object array, int arrayIndex)
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            if (arrayIndex < 0) {
                throw new ArgumentOutOfRangeException("arrayIndex", "arrayIndex cannot be a negative value.");
            }

            Object[] array_target = (Object[]) array;

            if (array_target.length - arrayIndex < _hashtable._count)
                throw new ArgumentException("The given array is too small to accommodate all the Hashtable elements.");


            _hashtable.CopyKeys((Object[]) array, arrayIndex);
        }

        @Override
        public int getCount() { return _hashtable._count; }

        @Override
        public Object getSyncRoot() { return _hashtable.getSyncRoot(); }

        @Override
        public boolean getIsSynchronized() { return _hashtable.getIsSynchronized(); }

        @Override
        public IEnumerator GetEnumerator() { return new Enumerator(new HashtableEnumerator(_hashtable)); }
    }

    // Implements a Collection for the keys of a hashtable. An instance of this
    // class is created by the GetKeys method of a hashtable.
    private record ValueCollection(Hashtable _hashtable)
            implements ICollection
    {
        private static final class Enumerator
                implements IEnumerator
        {
            private final HashtableEnumerator en;

            private Enumerator(HashtableEnumerator en) { this.en = en; }

            @Override
            public Object getCurrent() { return en.GetValue(); }

            @Override
            public void Reset() throws InvalidOperationException { en.Reset(); }

            @Override
            public boolean MoveNext() throws InvalidOperationException { return en.MoveNext(); }
        }

        @Override
        public void CopyTo(Object array, int arrayIndex)
        {
            ArgumentNullException.ThrowIfNull(array, "array");

            if (arrayIndex < 0) {
                throw new ArgumentOutOfRangeException("arrayIndex", "arrayIndex cannot be a negative value.");
            }

            Object[] array_target = (Object[]) array;

            if (array_target.length - arrayIndex < _hashtable._count)
                throw new ArgumentException("The given array is too small to accommodate all the Hashtable elements.");

            _hashtable.CopyValues((Object[]) array, arrayIndex);
        }

        @Override
        public int getCount() { return _hashtable._count; }

        @Override
        public Object getSyncRoot() { return _hashtable.getSyncRoot(); }

        @Override
        public boolean getIsSynchronized() { return _hashtable.getIsSynchronized(); }

        @Override
        public IEnumerator GetEnumerator() { return new Enumerator(new HashtableEnumerator(_hashtable)); }
    }

    // Implements an enumerator for a hashtable. The enumerator uses the
    // internal version number of the hashtable to ensure that no modifications
    // are made to the hashtable while an enumeration is in progress.
    private static final class HashtableEnumerator
            implements IDictionaryEnumerator
    {
        private final Hashtable _hashtable;
        private int _bucket;
        private final int _version;
        private boolean _current;
        @AllowNull
        private Object _currentKey;
        @AllowNull
        private Object _currentValue;

        public HashtableEnumerator(Hashtable hashtable)
        {
            _hashtable = hashtable;
            _bucket = hashtable._buckets.length;
            _version = hashtable._version;
            _current = false;
        }

        @Override
        public Object GetValue()
                throws InvalidOperationException
        {
            if (!_current)
                throw new InvalidOperationException("Enumeration has not begun yet.");
            return _currentValue;
        }

        @Override
        public Object GetKey()
                throws InvalidOperationException
        {
            if (!_current)
                throw new InvalidOperationException("Enumeration has not begun yet.");
            return _currentKey;
        }

        @Override
        public DictionaryEntry getCurrent()
        {
            if (!_current)
                throw new InvalidOperationException("Enumeration has not begun yet.");
            return new DictionaryEntry(_currentKey, _currentValue);
        }

        public boolean MoveNext()
        {
            if (_version != _hashtable._version)
                throw new InvalidOperationException("Enumeration failed.");
            while (_bucket > 0)
            {
                _bucket--;
                Object keyv = _hashtable._buckets[_bucket].key;
                if ((keyv != null) && (keyv != _hashtable._buckets))
                {
                    _currentKey = keyv;
                    _currentValue = _hashtable._buckets[_bucket].val;
                    _current = true;
                    return true;
                }
            }
            _current = false;
            return false;
        }

        public void Reset()
        {
            if (_version != _hashtable._version)
                throw new InvalidOperationException("Enumeration failed.");
            _current = false;
            _bucket = _hashtable._buckets.length;
            _currentKey = null;
            _currentValue = null;
        }
    }
}
