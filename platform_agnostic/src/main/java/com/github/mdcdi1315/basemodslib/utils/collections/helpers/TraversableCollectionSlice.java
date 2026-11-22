package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.collections.*;
import com.github.mdcdi1315.basemodslib.utils.EmptyEnumerator;

public class TraversableCollectionSlice<T>
    extends SliceEnumerable<T>
    implements ISupportsSlicing<T>, ISupportsCloning<T>
{
    protected TraversableCollectionSlice(ITraversableCollection<T> collection, int index, int count)
    {
        super(collection, index, count);
    }

    public static final class Empty<T>
        extends TraversableCollectionSlice<T>
        implements IEmptyEnumerable
    {
        public Empty() { super(null, 0, 0); }

        @Override
        public T GetItem(int index)
                throws ArgumentOutOfRangeException
        {
            // Throw unconditionally.
            throw new ArgumentOutOfRangeException("index", "Index is outside of the collections's bounds.");
        }

        @Override
        public <TO> Empty<TO> ConvertAll(Converter<T, TO> converter, IEqualityComparer<TO> comparer)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(converter, "converter");
            return new Empty<>();
        }

        @Override
        public ISupportsCloning<T> Clone() { return new Empty<>(); }

        @Override
        public IEnumerator<T> GetEnumerator() { return new EmptyEnumerator<>(); }

        @Override
        public Empty<T> Slice(int count) throws ArgumentException { return new Empty<>(); }

        @Override
        public Empty<T> Slice(int index, int count) throws ArgumentException { return new Empty<>(); }

        @Override
        public Empty<T> FilterBy(Predicate<T> predicate) throws ArgumentNullException { return new Empty<>(); }

        @Override
        public <TO> Empty<TO> ConvertAll(Converter<T, TO> converter) throws ArgumentNullException { return ConvertAll(converter, null); }
    }

    private static final class Enumerator<T>
        extends BaseEnumerator<T>
    {
        private int current;
        private final int index, bound;
        private final ITraversableCollection<T> collection;

        public Enumerator(ITraversableCollection<T> c, int index, int count)
        {
            collection = c;
            this.index = index;
            this.current = index - 1;
            this.bound = index + count;
        }

        @Override
        public T getCurrent() { return collection.GetItem(current); }

        @Override
        protected void ResetImpl() { current = index - 1; }

        @Override
        protected boolean MoveNextImpl()
        {
            int new_current = current + 1;
            if (new_current < bound) {
                current = new_current;
                return true;
            } else {
                return false;
            }
        }
    }

    public static <T> TraversableCollectionSlice<T> Create(ITraversableCollection<T> collection, int index, int count)
    {
        if (collection instanceof IEmptyEnumerable || count == 0 || collection.GetCount() == 0) {
            return new Empty<>();
        } else {
            return new TraversableCollectionSlice<>(collection, index, count);
        }
    }

    @Override
    public int GetCount() { return count; }

    @Override
    public ISupportsCloning<T> Clone() { return Create((ITraversableCollection<T>)enumerable, index, count); }

    @Override
    public IEnumerator<T> GetEnumerator() { return new Enumerator<>((ITraversableCollection<T>)enumerable, index, count); }

    @Override
    public T GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the collections's bounds.");
        } else {
            return ((ITraversableCollection<T>)enumerable).GetItem(this.index + index);
        }
    }

    @Override
    public TraversableCollectionSlice<T> Slice(int index, int count)
            throws ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else {
            CollectionHelpers.CheckIndexCountInsideCollectionBound(index, count, this.count);
            return Create(this, index, count);
        }
    }

    @Override
    public TraversableCollectionSlice<T> Slice(int count) throws ArgumentException { return Slice(0, count); }
}
