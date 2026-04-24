package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

/**
 * A class similar to the {@link SingletonEnumeratorEnumerable} class, but it does instead accept a 'factory' method reference in order to create the enumerator instance each time. <br />
 * It is also extendable.
 * @param <T> The type of the elements to be enumerated.
 * @since 1.0.29
 */
public class FromEnumeratorFactoryEnumerable<T>
    implements IEnumerable<T>, ISupportsFiltering<T>, ISupportsSlicing<T>, ISupportsDirectConversionTo<T>
{
    @NotNull
    private final Func1<IEnumerator<T>> factory;

    /**
     * Initializes a new instance of the {@link FromEnumeratorFactoryEnumerable} class.
     * @param factory The factory method reference that creates {@link IEnumerator} instances to return through the {@link #GetEnumerator()} method.
     * @throws ArgumentNullException {@code factory} is {@code null}.
     */
    public FromEnumeratorFactoryEnumerable(Func1<IEnumerator<T>> factory)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.factory = factory, "factory");
    }

    /**
     * Gets a new {@link IEnumerator} that contains the elements to iterate.
     * @return A new {@link IEnumerator} instance.
     */
    @Override
    public final IEnumerator<T> GetEnumerator() { return factory.function(); }

    private record FilterMapping<T>(Func1<IEnumerator<T>> original, Predicate<T> filter)
        implements Func1<IEnumerator<T>>
    {
        @Override
        public IEnumerator<T> get() { return new FilteringEnumerator<>(original.function(), filter); }

        @Override
        public IEnumerator<T> function() { return new FilteringEnumerator<>(original.function(), filter); }
    }

    private record SliceMapping<T>(Func1<IEnumerator<T>> original, int index, int count)
        implements Func1<IEnumerator<T>>
    {
        @Override
        public IEnumerator<T> get() { return new SlicingEnumerator<>(original.function(), index, count); }

        @Override
        public IEnumerator<T> function() { return new SlicingEnumerator<>(original.function(), index, count); }
    }

    private record TypeMapping<T, TO>(Func1<IEnumerator<T>> original, Converter<T, TO> converter)
        implements Func1<IEnumerator<TO>>
    {
        @Override
        public IEnumerator<TO> get() { return new MappingEnumerator<>(original.function(), converter); }

        @Override
        public IEnumerator<TO> function() { return new MappingEnumerator<>(original.function(), converter); }
    }

    @Override
    public FromEnumeratorFactoryEnumerable<T> FilterBy(Predicate<T> predicate) throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        return new FromEnumeratorFactoryEnumerable<>(new FilterMapping<>(this.factory, predicate));
    }

    @Override
    public FromEnumeratorFactoryEnumerable<T> Slice(int index, int count)
            throws ArgumentException, ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else {
            return new FromEnumeratorFactoryEnumerable<>(new SliceMapping<>(this.factory, index, count));
        }
    }

    @Override
    public FromEnumeratorFactoryEnumerable<T> Slice(int count)
            throws ArgumentException, ArgumentOutOfRangeException
    {
        if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else {
            return new FromEnumeratorFactoryEnumerable<>(new SliceMapping<>(this.factory, 0, count));
        }
    }

    @Override
    public <TO> FromEnumeratorFactoryEnumerable<TO> ConvertAll(Converter<T, TO> converter, IEqualityComparer<TO> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");
        return new FromEnumeratorFactoryEnumerable<>(new TypeMapping<>(factory, converter));
    }

    @Override
    public <TO> FromEnumeratorFactoryEnumerable<TO> ConvertAll(Converter<T, TO> converter) throws ArgumentNullException { return ConvertAll(converter, null); }
}
