package com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized;

import com.github.mdcdi1315.DotNetLayer.System.Collections.IList;
import com.github.mdcdi1315.DotNetLayer.System.Collections.ArrayList;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

@TypeForwardedFrom(AssemblyFullName = "System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class StringCollection
        implements IList
{
    private final ArrayList data = new ArrayList(); // Do not rename (binary serialization)

    @Override
    public void Clear() { data.Clear(); }

    @Override
    public boolean GetIsReadOnly() { return false; }

    @Override
    public boolean GetIsFixedSize() { return false; }

    @Override
    public int getCount() { return data.getCount(); }

    @Override
    public boolean getIsSynchronized() { return false; }

    @Override
    public void RemoveAt(int index) { data.RemoveAt(index); }

    @Override
    public Object getSyncRoot() { return data.getSyncRoot(); }

    public void Remove(@AllowNull String value) { data.Remove(value); }

    public int Add(@AllowNull String value) { return data.Add(value); }

    @Override
    @MaybeNull
    public String GetItem(int index) { return (String) data.GetItem(index); }

    @Override
    public void CopyTo(Object array, int index) { data.CopyTo(array, index); }

    public int IndexOf(@AllowNull String value) { return data.IndexOf(value); }

    public boolean Contains(@AllowNull String value) { return data.Contains(value); }

    public void Insert(int index, @AllowNull String value) { data.Insert(index, value); }

    public void SetItem(int index, @AllowNull String value) { data.SetItem(index, value); }

    @Override
    public StringEnumerator GetEnumerator() { return new StringEnumerator(data.GetEnumerator()); }

    @Override
    public void Remove(Object value) { if (value == null || value instanceof String) { Remove((String) value); } }

    @Override
    public boolean Contains(Object value) { return (value == null || value instanceof String) && Contains((String) value); }

    @Override
    public void SetItem(int index, @AllowNull Object value) { if (value == null || value instanceof String) { data.SetItem(index, value); } }

    @Override
    public void Insert(int index, Object value) throws ArgumentOutOfRangeException { if (value == null || value instanceof String) { Insert(index, (String) value); } }

    @Override
    public int Add(@AllowNull Object value)
    {
        if (value instanceof String s) { return data.Add(s); }
        throw new NotSupportedException();
    }

    @Override
    public int IndexOf(Object value)
    {
        if (value == null || value instanceof String) { return IndexOf((String) value); }
        return -1;
    }

    /// <devdoc>
    /// <para>Copies the elements of a string array to the end of the <see cref='System.Collections.Specialized.StringCollection'/>.</para>
    /// </devdoc>
    public void AddRange(String[] value)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(value, "value");

        data.SetCapacity(data.getCount() + value.length);
        for (String item : value) { data.Add(item); }
    }
}
