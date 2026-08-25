package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IReadOnlyList;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;

public final class ReadOnlyListToTraversableCollection<T>
    extends BaseEnumerable<T>
    implements ITraversableCollection<T>, IReadOnlyList<T>
{
    private final IReadOnlyList<T> list;

    public ReadOnlyListToTraversableCollection(IReadOnlyList<T> list) { this.list = list; }

    @Override
    public int getCount() { return list.getCount(); }

    @Override
    public int GetCount() { return list.getCount(); }

    @Override
    public T getItem(int index) { return list.getItem(index); }

    @Override
    public IEnumerator<T> GetEnumerator() { return list.GetEnumerator(); }

    @Override
    public T GetItem(int index) throws ArgumentOutOfRangeException { return list.getItem(index); }
}
