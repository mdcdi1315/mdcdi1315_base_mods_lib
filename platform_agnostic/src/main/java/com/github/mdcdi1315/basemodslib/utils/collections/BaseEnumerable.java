package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;

/**
 * Provides the base class and enumeration services for all the basic collection implementations. <br />
 * This is typically used by classes for fast-implementing common usage cases. <br />
 * Any class providing a better, reliable and faster method will still continue to provide it normally.
 * @param <T> The type of the elements to be enumerated.
 * @since 1.0.31
 */
public abstract class BaseEnumerable<T>
    implements IEnumerable<T>, ISupportsDirectConversionTo<T>, ISupportsSlicing<T>, ISupportsFiltering<T>
{
    @Override
    public abstract IEnumerator<T> GetEnumerator();

    private static final class FilterByEnumerable<T>
        extends BaseEnumerable<T>
    {
        private final Predicate<T> filter;
        private final BaseEnumerable<T> enumerable;

        public FilterByEnumerable(BaseEnumerable<T> enumerable, Predicate<T> filter)
        {
            this.filter = filter;
            this.enumerable = enumerable;
        }

        @Override
        public IEnumerator<T> GetEnumerator() {
            return new FilteringEnumerator<>(enumerable.GetEnumerator(), filter);
        }
    }

    private static final class ConvertAllEnumerable<TI, TO>
        extends BaseEnumerable<TO>
    {
        private final Converter<TI, TO> converter;
        private final BaseEnumerable<TI> enumerable;

        public ConvertAllEnumerable(BaseEnumerable<TI> enumerable, Converter<TI, TO> converter)
        {
            this.converter = converter;
            this.enumerable = enumerable;
        }

        @Override
        public IEnumerator<TO> GetEnumerator() {
            return new MappingEnumerator<>(enumerable.GetEnumerator(), converter);
        }
    }

    private static final class SlicingEnumerable<T>
        extends BaseEnumerable<T>
    {
        private final int index, count;
        private final BaseEnumerable<T> enumerable;

        public SlicingEnumerable(BaseEnumerable<T> enumerable, int index, int count)
        {
            this.index = index;
            this.count = count;
            this.enumerable = enumerable;
        }

        @Override
        public IEnumerator<T> GetEnumerator() {
            return new SlicingEnumerator<>(enumerable.GetEnumerator(), index, count);
        }
    }

    @Override
    public BaseEnumerable<T> FilterBy(Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return this;
        } else {
            return new FilterByEnumerable<>(this, predicate);
        }
    }

    @Override
    public <TO> BaseEnumerable<TO> ConvertAll(Converter<T, TO> converter, IEqualityComparer<TO> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");
        return new ConvertAllEnumerable<>(this, converter);
    }

    @Override
    public BaseEnumerable<T> Slice(int index, int count)
            throws ArgumentException, ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else {
            return new SlicingEnumerable<>(this, index, count);
        }
    }

    @Override
    public BaseEnumerable<T> Slice(int count) throws ArgumentException { return Slice(0, count); }

    @Override
    public <TO> BaseEnumerable<TO> ConvertAll(Converter<T, TO> converter) throws ArgumentNullException { return ConvertAll(converter, null); }
}
