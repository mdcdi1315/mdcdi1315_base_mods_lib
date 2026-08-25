package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.collections.*;
import com.github.mdcdi1315.basemodslib.utils.EmptyEnumerator;

public class TraversableCollectionSlice<T>
    extends BaseEnumerable<T>
    implements ITraversableCollection<T>, ISupportsSlicing<T>, ISupportsCloning<T>
{
    private final int index, count;
    private final ITraversableCollection<T> collection;

    public TraversableCollectionSlice(ITraversableCollection<T> collection, int index, int count)
    {
        this.index = index;
        this.count = count;
        this.collection = collection;
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

    @Override
    public int GetCount() { return count; }

    @Override
    public IEnumerator<T> GetEnumerator() { return new Enumerator<>(collection, index, count); }

    @Override
    public ISupportsCloning<T> Clone() { return new TraversableCollectionSlice<>(collection, index, count); }

    @Override
    public T GetItem(int index)
            throws ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative number.");
        } else if (index >= count) {
            throw new ArgumentOutOfRangeException("index", "Index is outside of the collections's bounds.");
        } else {
            return collection.GetItem(this.index + index);
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
        } else if (count == 0) {
            return new Empty<>();
        } else if (((long)index + count - 1L) >= this.count) {
            throw new ArgumentException("The specified combination of the index and count parameters are outside of the collections's bounds.");
        } else {
            return new TraversableCollectionSlice<>(this, index, count);
        }
    }

    @Override
    public TraversableCollectionSlice<T> Slice(int count) throws ArgumentException { return Slice(0, count); }
}
