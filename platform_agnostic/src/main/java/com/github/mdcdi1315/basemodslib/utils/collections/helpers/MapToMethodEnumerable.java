package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Converter;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.*;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;

import com.github.mdcdi1315.basemodslib.utils.collections.*;
import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;

public class MapToMethodEnumerable<TS, TR>
        extends BaseEnumerable<TR>
{
    protected final IEnumerable<TS> enumerable;
    protected final Converter<TS, TR> converter;

    private static final class Countable<TS, TR>
        extends MapToMethodEnumerable<TS, TR>
        implements ICountableCollection<TR>
    {
        public Countable(ICountableCollection<TS> enumerable, Converter<TS, TR> converter) { super(enumerable, converter); }

        @Override
        public int GetCount() { return ((ICountableCollection<TS>)enumerable).GetCount(); }
    }

    private static final class CountableFromCollection<TS, TR>
        extends MapToMethodEnumerable<TS, TR>
        implements ICountableCollection<TR>
    {
        public CountableFromCollection(ICollection<TS> enumerable, Converter<TS, TR> converter) { super(enumerable, converter); }

        @Override
        public int GetCount() { return ((ICollection<TS>)enumerable).getCount(); }
    }

    private static final class Traversable<TS, TR>
            extends MapToMethodEnumerable<TS, TR>
            implements ITraversableCollection<TR>, IReadOnlyList<TR>
    {
        public Traversable(ITraversableCollection<TS> enumerable, Converter<TS, TR> converter) { super(enumerable, converter); }

        @Override
        public int getCount() { return GetCount(); }

        @Override
        public TR getItem(int index) { return GetItem(index); }

        @Override
        public int GetCount() { return ((ICountableCollection<TS>)enumerable).GetCount(); }

        @Override
        public TR GetItem(int index) { return converter.convert(((ITraversableCollection<TS>)enumerable).GetItem(index)); }

        @Override
        public Traversable<TS, TR> Slice(int index, int count)
                throws ArgumentException
        {
            if (index < 0) {
                throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
            } else if (count < 0) {
                throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
            } else {
                CollectionHelpers.CheckIndexCountInsideCollectionBound(index, count, GetCount());
                return new Traversable<>(TraversableCollectionSlice.Create((ITraversableCollection<TS>)enumerable, index, count), converter);
            }
        }

        @Override
        public Traversable<TS, TR> Slice(int count) throws ArgumentException { return Slice(0, count); }
    }

    private static final class TraversableFromList<TS, TR>
        extends MapToMethodEnumerable<TS, TR>
        implements ITraversableCollection<TR>, IReadOnlyList<TR>
    {
        public TraversableFromList(IList<TS> enumerable, Converter<TS, TR> converter) { super(enumerable, converter); }

        @Override
        public int getCount() { return GetCount(); }

        @Override
        public TR getItem(int index) { return GetItem(index); }

        @Override
        public int GetCount() { return ((IList<TS>)enumerable).getCount(); }

        @Override
        public TR GetItem(int index) throws ArgumentOutOfRangeException { return converter.convert(((IList<TS>)enumerable).getItem(index)); }
    }

    public static <TS, TR> BaseEnumerable<TR> Create(IEnumerable<TS> enumerable, Converter<TS, TR> converter)
    {
        if (CollectionManipulations.IsEmpty(enumerable)) {
            return new EmptyBaseEnumerable<>();
        } else if (enumerable instanceof ICountableCollection<TS> c) {
            return new Countable<>(c, converter);
        } else if (enumerable instanceof ITraversableCollection<TS> t) {
            return new Traversable<>(t, converter);
        } else if (enumerable instanceof IList<TS> list) {
            return new TraversableFromList<>(list, converter);
        } else if (enumerable instanceof ICollection<TS> c) {
            return new CountableFromCollection<>(c, converter);
        } else {
            return new MapToMethodEnumerable<>(enumerable, converter);
        }
    }

    protected MapToMethodEnumerable(IEnumerable<TS> enumerable, Converter<TS, TR> converter)
    {
        this.converter = converter;
        this.enumerable = enumerable;
    }

    @Override
    public <TO> BaseEnumerable<TO> ConvertAll(Converter<TR, TO> converter, IEqualityComparer<TO> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");
        var mapper = FunctionManipulations.ThenMap(this.converter, converter);
        if (enumerable instanceof BaseEnumerable<TS> c) {
            return c.ConvertAll(mapper, comparer);
        } else {
            return Create(this.enumerable, mapper);
        }
    }

    @Override
    public BaseEnumerable<TR> Slice(int index, int count)
            throws ArgumentException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (enumerable instanceof BaseEnumerable<TS> e) {
            if (enumerable instanceof ICountableCollectionBase c)
            {
                CollectionHelpers.CheckIndexCountInsideCollectionBound(index, count, c.GetCount());
            }
            return Create(e.Slice(index, count), converter);
        } else {
            return super.Slice(index, count);
        }
    }

    @Override
    public BaseEnumerable<TR> Slice(int count) throws ArgumentException { return Slice(0, count); }

    @Override
    public IEnumerator<TR> GetEnumerator() { return new MappingEnumerator<>(enumerable.GetEnumerator(), converter); }

    @Override
    public <TO> BaseEnumerable<TO> ConvertAll(Converter<TR, TO> converter) throws ArgumentNullException { return ConvertAll(converter, null); }
}