package com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized;

import com.github.mdcdi1315.DotNetLayer.System.Collections.*;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Implements {@link IDictionary} by using a {@link ListDictionary} while the collection is small, and then switching to a {@link Hashtable} when the collection gets large.
 */
public class HybridDictionary
        implements IDictionary
{
    // These numbers have been carefully tested to be optimal. Please don't change them
    // without doing thorough performance testing.
    private static final int CutoverPoint = 9;
    private static final int InitialHashtableSize = 13;
    private static final int FixedSizeCutoverPoint = 6;

    // Instance variables. This keeps the HybridDictionary very light-weight when empty
    @AllowNull
    private ListDictionary list; // Do not rename (binary serialization)
    @AllowNull
    private Hashtable hashtable; // Do not rename (binary serialization)
    private final boolean caseInsensitive; // Do not rename (binary serialization)

    /**
     * Creates an empty case-sensitive {@link HybridDictionary}.
     */
    public HybridDictionary()
    {
        list = null;
        hashtable = null;
        this.caseInsensitive = false;
    }

    /**
     * Creates a case-sensitive {@link HybridDictionary} with the specified initial size.
     * @param initialSize The approximate number of entries that the {@link HybridDictionary} can initially contain.
     */
    public HybridDictionary(int initialSize)
    {
        this(initialSize, false);
    }

    /**
     * Creates an empty {@link HybridDictionary} with the specified case sensitivity.
     * @param caseInsensitive A Boolean that denotes whether the {@link HybridDictionary} is case-insensitive.
     */
    public HybridDictionary(boolean caseInsensitive)
    {
        list = null;
        hashtable = null;
        this.caseInsensitive = caseInsensitive;
    }

    /**
     * Creates a HybridDictionary with the specified initial size and case sensitivity.
     * @param initialSize The approximate number of entries that the {@link HybridDictionary} can initially contain.
     * @param caseInsensitive A Boolean that denotes whether the {@link HybridDictionary} is case-insensitive.
     */
    public HybridDictionary(int initialSize, boolean caseInsensitive)
    {
        list = null;
        this.caseInsensitive = caseInsensitive;
        if (initialSize >= FixedSizeCutoverPoint)
        {
            if (caseInsensitive)
            {
                hashtable = new Hashtable(initialSize, new OrdinalIgnoreCaseStrComparer());
            }
            else
            {
                hashtable = new Hashtable(initialSize);
            }
        }
    }

    private static final class OrdinalIgnoreCaseStrComparer
        implements IComparer, IEqualityComparer
    {
        @Override
        public int Compare(Object x, Object y)
        {
            if (x instanceof String sa && y instanceof String sb) {
                return String.CASE_INSENSITIVE_ORDER.compare(sa, sb);
            } else {
                return Comparer.Default.Compare(x, y);
            }
        }

        @Override
        public int GetHashCode(Object obj) throws ArgumentNullException { return obj.hashCode(); }

        @Override
        public boolean Equals(Object x, Object y) throws ArgumentException { return Compare(x, y) == 0; }
    }

    @Override
    public Object getSyncRoot() { return this; }

    @Override
    public boolean GetIsReadOnly() { return false; }

    @Override
    public boolean GetIsFixedSize() { return false; }

    @Override
    public boolean getIsSynchronized() { return false; }

    @Override
    public IDictionaryEnumerator GetEnumerator() { return (hashtable == null) ? GetList().GetEnumerator() : hashtable.GetEnumerator(); }

    @Override
    public Object GetItem(Object key)
            throws ArgumentNullException
    {
        // Hashtable supports multiple read, one writer thread safety.
        // Although we never made the same guarantee for HybridDictionary,
        // it is still nice to do the same thing here since we have recommended
        // HybridDictionary as replacement for Hashtable.
        ListDictionary cachedList = list;
        if (hashtable != null)
        {
            return hashtable.GetItem(key);
        }
        else if (cachedList != null)
        {
            return cachedList.GetItem(key);
        }
        // cachedList can be null in too cases:
        //   (1) The dictionary is empty, we will return null in this case
        //   (2) There is writer which is doing ChangeOver. However in that case
        //       we should see the change to hashtable as well.
        //       So it should work just fine.
        else
        {
            ArgumentNullException.ThrowIfNull(key, "key");
        }
        return null;
    }

    @Override
    public void SetItem(Object key, Object value)
            throws ArgumentNullException, NotSupportedException
    {
        if (hashtable != null)
        {
            hashtable.SetItem(key, value);
        }
        else if (list != null)
        {
            if (list.getCount() >= CutoverPoint - 1)
            {
                ChangeOver();
                // Debug.Assert(hashtable != null);
                hashtable.SetItem(key, value);
            }
            else
            {
                list.SetItem(key, value);
            }
        }
        else
        {
            list = new ListDictionary(caseInsensitive ? new OrdinalIgnoreCaseStrComparer() : null);
            list.SetItem(key, value);
        }
    }

    private ListDictionary GetList()
    {
        if (list == null)
        {
            list = new ListDictionary(caseInsensitive ? new OrdinalIgnoreCaseStrComparer() : null);
        }
        return list;
    }

    private void ChangeOver()
    {
        //  Debug.Assert(list != null);
        IDictionaryEnumerator en = list.GetEnumerator();
        Hashtable newTable;
        if (caseInsensitive)
        {
            newTable = new Hashtable(InitialHashtableSize, new OrdinalIgnoreCaseStrComparer());
        }
        else
        {
            newTable = new Hashtable(InitialHashtableSize);
        }
        while (en.MoveNext())
        {
            newTable.Add(en.GetKey(), en.GetValue());
        }

        // Keep the order of writing to hashtable and list.
        // We assume we will see the change in hashtable if list is set to null in
        // this method in another reader thread.
        hashtable = newTable;
        list = null;
    }

    @Override
    public int getCount()
    {
        ListDictionary cachedList = list;
        if (hashtable != null)
        {
            return hashtable.getCount();
        }
        else if (cachedList != null)
        {
            return cachedList.getCount();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public ICollection GetKeys()
    {
        if (hashtable != null)
        {
            return hashtable.GetKeys();
        }
        else
        {
            return GetList().GetKeys();
        }
    }

    @Override
    public ICollection GetValues() {
        if (hashtable != null) {
            return hashtable.GetValues();
        } else {
            return GetList().GetValues();
        }
    }

    @Override
    public void Add(Object key, @AllowNull Object value)
    {
        if (hashtable != null)
        {
            hashtable.Add(key, value);
        }
        else
        {
            if (list == null)
            {
                list = new ListDictionary(caseInsensitive ? new OrdinalIgnoreCaseStrComparer() : null);
                list.Add(key, value);
            }
            else if (list.getCount() + 1 >= CutoverPoint)
            {
                ChangeOver();
                // Debug.Assert(hashtable != null);
                hashtable.Add(key, value);
            }
            else
            {
                list.Add(key, value);
            }
        }
    }

    @Override
    public void Clear()
    {
        if (hashtable != null)
        {
            Hashtable cachedHashtable = hashtable;
            hashtable = null;
            cachedHashtable.Clear();
        }

        if (list != null)
        {
            ListDictionary cachedList = list;
            list = null;
            cachedList.Clear();
        }
    }

    @Override
    public boolean Contains(Object key)
    {
        ListDictionary cachedList = list;
        if (hashtable != null)
        {
            return hashtable.Contains(key);
        }
        else if (cachedList != null)
        {
            return cachedList.Contains(key);
        }
        else
        {
            ArgumentNullException.ThrowIfNull(key, "key");
        }
        return false;
    }

    @Override
    public void CopyTo(Object array, int index)
    {
        if (hashtable != null)
        {
            hashtable.CopyTo(array, index);
        }
        else
        {
            GetList().CopyTo(array, index);
        }
    }

    @Override
    public void Remove(Object key)
    {
        if (hashtable != null)
        {
            hashtable.Remove(key);
        }
        else if (list != null)
        {
            list.Remove(key);
        }
        else
        {
            ArgumentNullException.ThrowIfNull(key, "key");
        }
    }
}
