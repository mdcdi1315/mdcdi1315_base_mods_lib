package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Converter;
import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.EmptyEnumerator;
import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;

/**
 * Provides an {@link IEnumerable} interface implementation by wrapping a single {@link IEnumerator} instance, that can be only enumerated once. <br />
 * It does also implement the {@link ISupportsDirectConversionTo} interface since the enumerator instance can be itself mapped to something else.
 * @param <T> The type of the elements that the enumerator singleton returns.
 * @since 1.0.26
 */
public final class SingletonEnumeratorEnumerable<T>
    extends BaseEnumerable<T>
{
    private final IEnumerator<T> single;

    /**
     * Initializes a new instance of the {@link SingletonEnumeratorEnumerable} class by using the specified {@link IEnumerator} instance.
     * @param wrapping The {@link IEnumerator} instance to wrap.
     * @throws ArgumentNullException {@code wrapping} is {@code null}.
     */
    public SingletonEnumeratorEnumerable(IEnumerator<T> wrapping)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(single = wrapping, "wrapping");
    }

    @Override
    public IEnumerator<T> GetEnumerator() { return single; }

    /**
     * Stores the specified portion of elements of the current singleton enumerator enumerable to a new instance.
     *
     * @param index The starting index to start copying elements from the current enumerable.
     * @param count The number of elements to copy from the current enumerable to the new one.
     * @return A new enumerable containing only the specified portion of elements.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     */
    @Override
    public SingletonEnumeratorEnumerable<T> Slice(int index, int count)
            throws ArgumentOutOfRangeException
    {
        return new SingletonEnumeratorEnumerable<>(
                new SlicingEnumerator<>(single, index, count)
        );
    }

    @Override
    public <TO> SingletonEnumeratorEnumerable<TO> ConvertAll(Converter<T, TO> converter, IEqualityComparer<TO> comparer)
            throws ArgumentNullException
    {
        return new SingletonEnumeratorEnumerable<>(
                new MappingEnumerator<>(single, converter)
        );
    }

    @Override
    public SingletonEnumeratorEnumerable<T> FilterBy(Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return this;
        } else if (FunctionManipulations.IsAlwaysFalse(predicate)) {
            return new SingletonEnumeratorEnumerable<>(new EmptyEnumerator<>());
        } else {
            return new SingletonEnumeratorEnumerable<>(
                    new FilteringEnumerator<>(single, predicate)
            );
        }
    }
}
