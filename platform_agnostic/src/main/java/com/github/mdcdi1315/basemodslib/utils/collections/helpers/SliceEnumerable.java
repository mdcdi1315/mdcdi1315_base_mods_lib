package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.SlicingEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;

public final class SliceEnumerable<T>
    extends BaseEnumerable<T>
    implements ITraversableCollection<T>
{
    private final int index, count;
    private final IEnumerable<T> enumerable;

    public SliceEnumerable(IEnumerable<T> enumerable, int index, int count)
    {
        this.enumerable = enumerable;
        this.index = index;
        this.count = count;
    }

    @Override
    public T GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (enumerable instanceof ITraversableCollection<T> tc) {
            return tc.GetItem(index);
        } else {
            try (IEnumerator<T> en = GetEnumerator())
            {
                int I = 0;
                while (I < index && en.MoveNext()) { I++; }
                if (en.MoveNext()) {
                    return en.getCurrent();
                } else {
                    throw new ArgumentOutOfRangeException(String.format("Index out of range: %d", index));
                }
            }
        }
    }

    @Override
    public BaseEnumerable<T> Slice(int index, int count)
            throws ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (count == 0) {
            return new EmptyBaseEnumerable<>();
        } else if (enumerable instanceof ITraversableCollection<T> tc) {
            return new TraversableCollectionSlice<>(tc, index, count);
        } else {
            return new SliceEnumerable<>(enumerable, index, count);
        }
    }

    @Override
    public int GetCount() { return count; }

    @Override
    public IEnumerator<T> GetEnumerator() { return new SlicingEnumerator<>(enumerable.GetEnumerator(), index, count); }
}