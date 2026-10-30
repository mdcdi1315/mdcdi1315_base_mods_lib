package com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.ArrayList;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IEqualityComparer;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

public class NameValueCollection
        extends NameObjectCollectionBase
{
    @AllowNull
    private String[] _all; // Do not rename (binary serialization)
    @AllowNull
    private String[] _allKeys; // Do not rename (binary serialization)

    //
    // Constructors
    //

    /// <devdoc>
    /// <para>Creates an empty <see cref='System.Collections.Specialized.NameValueCollection'/> with the default initial capacity
    ///    and using the default case-insensitive hash code provider and the default
    ///    case-insensitive comparer.</para>
    /// </devdoc>
    public NameValueCollection()
    {
        super();
        _all = null;
        _allKeys = null;
    }

    /// <devdoc>
    /// <para>Copies the entries from the specified <see cref='System.Collections.Specialized.NameValueCollection'/> to a new <see cref='System.Collections.Specialized.NameValueCollection'/> with the same initial capacity as
    ///    the number of entries copied and using the default case-insensitive hash code
    ///    provider and the default case-insensitive comparer.</para>
    /// </devdoc>
    public NameValueCollection(NameValueCollection col)
    {
        super((col == null) ? null : col.GetComparer());
        _all = null;
        _allKeys = null;
        Add(col);
    }

    /// <devdoc>
    /// <para>Creates an empty <see cref='System.Collections.Specialized.NameValueCollection'/> with
    ///    the specified initial capacity and using the default case-insensitive hash code
    ///    provider and the default case-insensitive comparer.</para>
    /// </devdoc>
    public NameValueCollection(int capacity)
    {
        super(capacity);
        _all = null;
        _allKeys = null;
    }

    public NameValueCollection(@AllowNull IEqualityComparer equalityComparer)
    {
        super(equalityComparer);
        _all = null;
        _allKeys = null;
    }

    public NameValueCollection(int capacity, @AllowNull IEqualityComparer equalityComparer)
    {
        super(capacity, equalityComparer);
        _all = null;
        _allKeys = null;
    }

    private static IEqualityComparer GetComparerOrFail(NameValueCollection col)
    {
        ArgumentNullException.ThrowIfNull(col, "col");
        return col.GetComparer();
    }

    /// <devdoc>
    /// <para>Copies the entries from the specified <see cref='System.Collections.Specialized.NameValueCollection'/> to a new <see cref='System.Collections.Specialized.NameValueCollection'/> with the specified initial capacity or the
    ///    same initial capacity as the number of entries copied, whichever is greater, and
    ///    using the default case-insensitive hash code provider and the default
    ///    case-insensitive comparer.</para>
    /// </devdoc>
    public NameValueCollection(int capacity, @AllowNull NameValueCollection col)
    {
        super(capacity, GetComparerOrFail(col));
        _all = null;
        _allKeys = null;
        Add(col);
    }

    //
    //  Helper methods
    //

    /// <devdoc>
    /// <para> Resets the cached arrays of the collection to <see langword='null'/>.</para>
    /// </devdoc>
    protected void InvalidateCachedArrays()
    {
        _all = null;
        _allKeys = null;
    }

    private static @AllowNull String GetAsOneString(ArrayList list)
    {
        int n = (list != null) ? list.getCount() : 0;

        if (n == 1)
        {
            // Debug.Assert(list != null);
            return (String)list.GetItem(0);
        }
        else if (n > 1)
        {
            //  Debug.Assert(list != null);
            StringBuilder s = new StringBuilder((String)list.GetItem(0));

            for (int i = 1; i < n; i++)
            {
                s.append(',');
                s.append((String)list.GetItem(i));
            }

            return s.toString();
        }
        else
        {
            return null;
        }
    }

    private static String[] GetAsStringArray(ArrayList list)
    {
        int n = (list != null) ? list.getCount() : 0;
        if (n == 0)
            return null;

        String[] array = new String[n];
        list.CopyTo(array, 0);
        return array;
    }

    //
    // Misc public APIs
    //

    /// <devdoc>
    /// <para>Copies the entries in the specified <see cref='System.Collections.Specialized.NameValueCollection'/> to the current <see cref='System.Collections.Specialized.NameValueCollection'/>.</para>
    /// </devdoc>
    public void Add(NameValueCollection c)
    {
        ArgumentNullException.ThrowIfNull(c, "c");

        InvalidateCachedArrays();

        int n = c.getCount();

        for (int i = 0; i < n; i++)
        {
            @AllowNull String key = c.GetKey(i);
            String[] values = c.GetValues(i);

            if (values != null)
            {
                for (int j = 0; j < values.length; j++)
                    Add(key, values[j]);
            }
            else
            {
                Add(key, null);
            }
        }
    }

    /// <devdoc>
    ///    <para>Invalidates the cached arrays and removes all entries
    ///       from the <see cref='System.Collections.Specialized.NameValueCollection'/>.</para>
    /// </devdoc>
    public void Clear()
    {
        if (GetIsReadOnly())
            throw new NotSupportedException("The collection is read-only.");

        InvalidateCachedArrays();
        BaseClear();
    }

    public void CopyTo(Object dest, int index)
    {
        ArgumentNullException.ThrowIfNull(dest, "dest");

        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        }

        if (java.lang.reflect.Array.getLength(dest) - index < getCount())
        {
            throw new ArgumentException("Not enough space in the array to store all the collection's elements!");
        }

        int n = getCount();
        if (_all == null)
        {
            @AllowNull String[] all = new String[n];
            for (int i = 0; i < n; i++)
            {
                all[i] = Get(i);
                java.lang.reflect.Array.set(dest, i + index, all[i]);
            }
            _all = all; // wait until end of loop to set _all reference in case Get throws
        }
        else
        {
            for (int i = 0; i < n; i++)
            {
                java.lang.reflect.Array.set(dest, i + index, _all[i]);
            }
        }
    }

    /// <devdoc>
    /// <para>Gets a value indicating whether the <see cref='System.Collections.Specialized.NameValueCollection'/> contains entries whose keys are not <see langword='null'/>.</para>
    /// </devdoc>
    public boolean HasKeys()
    {
        return InternalHasKeys();
    }

    /// <devdoc>
    /// <para>Allows derived classes to alter HasKeys().</para>
    /// </devdoc>
    private boolean InternalHasKeys() { return BaseHasKeys(); }

    //
    // Access by name
    //

    /// <devdoc>
    ///    <para>Adds an entry with the specified name and value into the
    ///    <see cref='System.Collections.Specialized.NameValueCollection'/>.</para>
    /// </devdoc>
    public void Add(@AllowNull String name, @AllowNull String value)
    {
        if (GetIsReadOnly())
            throw new NotSupportedException("The collection is read-only.");

        InvalidateCachedArrays();

        ArrayList values = (ArrayList)BaseGet(name);

        if (values == null)
        {
            // new key - add new key with single value
            values = new ArrayList(1);
            if (value != null)
                values.Add(value);
            BaseAdd(name, values);
        }
        else
        {
            // old key -- append value to the list of values
            if (value != null)
                values.Add(value);
        }
    }

    /// <devdoc>
    /// <para> Gets the values associated with the specified key from the <see cref='System.Collections.Specialized.NameValueCollection'/> combined into one comma-separated list.</para>
    /// </devdoc>
    @MaybeNull
    public String Get(@AllowNull String name) { return GetAsOneString((ArrayList)BaseGet(name)); }

    /// <devdoc>
    /// <para>Gets the values associated with the specified key from the <see cref='System.Collections.Specialized.NameValueCollection'/>.</para>
    /// </devdoc>
    @MaybeNull
    public String[] GetValues(@AllowNull String name) { return GetAsStringArray((ArrayList)BaseGet(name)); }

    /// <devdoc>
    /// <para>Adds a value to an entry in the <see cref='System.Collections.Specialized.NameValueCollection'/>.</para>
    /// </devdoc>
    public void Set(@AllowNull String name, @AllowNull String value)
    {
        if (GetIsReadOnly())
            throw new NotSupportedException("The collection is read-only.");

        InvalidateCachedArrays();

        ArrayList values = new ArrayList(1);
        values.Add(value);
        BaseSet(name, values);
    }

    /// <devdoc>
    /// <para>Removes the entries with the specified key from the <see cref='System.Collections.Specialized.NameObjectCollectionBase'/> instance.</para>
    /// </devdoc>
    public void Remove(@AllowNull String name)
    {
        InvalidateCachedArrays();
        BaseRemove(name);
    }

    public String GetItem(@AllowNull String name) { return Get(name); }

    public void SetItem(@AllowNull String name, @AllowNull String value) { Set(name, value); }

    //
    // Indexed access
    //

    /// <devdoc>
    ///    <para>
    ///       Gets the values at the specified index of the <see cref='System.Collections.Specialized.NameValueCollection'/> combined into one
    ///       comma-separated list.</para>
    /// </devdoc>
    @MaybeNull
    public String Get(int index) { return GetAsOneString((ArrayList)BaseGet(index)); }

    /// <devdoc>
    ///    <para> Gets the values at the specified index of the <see cref='System.Collections.Specialized.NameValueCollection'/>.</para>
    /// </devdoc>
    @MaybeNull
    public String[] GetValues(int index) { return GetAsStringArray((ArrayList)BaseGet(index)); }

    /// <devdoc>
    /// <para>Gets the key at the specified index of the <see cref='System.Collections.Specialized.NameValueCollection'/>.</para>
    /// </devdoc>
    @MaybeNull
    public String GetKey(int index) { return BaseGetKey(index); }

    /// <devdoc>
    /// <para>Represents the entry at the specified index of the <see cref='System.Collections.Specialized.NameValueCollection'/>.</para>
    /// </devdoc>
    @MaybeNull
    public String GetItem(int index) { return Get(index); }

    //
    // Access to keys and values as arrays
    //

    /// <devdoc>
    /// <para>Gets all the keys in the <see cref='System.Collections.Specialized.NameValueCollection'/>. </para>
    /// </devdoc>
    @NotNull
    public String[] GetAllKeys()
    {
        if (_allKeys == null)
        {
            _allKeys = BaseGetAllKeys();
        }
        return _allKeys;
    }
}