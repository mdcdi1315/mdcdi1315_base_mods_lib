package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

import com.github.mdcdi1315.basemodslib.utils.collections.helpers.*;
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

    /**
     * Filters the elements of the current collection and returns all the items that pass the predicate.
     * @param predicate The {@link Predicate} to filter the elements of the current collection by.
     * @return A new collection containing only the elements that pass the {@link Predicate}.
     * @throws ArgumentNullException {@code predicate} is {@code null}.
     */
    @Override
    public BaseEnumerable<T> FilterBy(Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return this;
        } else if (CollectionManipulations.IsEmpty(this) || FunctionManipulations.IsAlwaysFalse(predicate)) {
            return new EmptyBaseEnumerable<>();
        } else {
            return new FilterByEnumerable<>(this, predicate);
        }
    }

    /**
     * Converts all the elements in the current collection by using the specified
     * {@code converter} and returns the converted elements in a new collection of the same type.
     * @param converter The {@link Converter} to use for converting an element of type {@link T} to {@link TO}.
     * @param comparer Optional. If the current collection supports equality comparison and a new equality
     *                 comparer is needed, this can specify the comparer to use in the new instance.
     * @return A new collection of the same type as this one, but with all of its elements mapped to type {@link TO}.
     * @param <TO> The type of the elements that the newly returned collection will have.
     * @throws ArgumentNullException {@code converter} is {@code null}.
     */
    @Override
    public <TO> BaseEnumerable<TO> ConvertAll(Converter<T, TO> converter, IEqualityComparer<TO> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");
        return CollectionManipulations.IsEmpty(this) ?
                new EmptyBaseEnumerable<>() :
                new ConvertAllEnumerable<>(this, converter);
    }

    /**
     * Converts all the elements in the current collection by using the specified
     * {@code converter} and returns the converted elements in a new collection of the same type.
     * @param converter The {@link Converter} to use for converting an element of type {@link T} to {@link TO}.
     * @return A new collection of the same type as this one, but with all of its elements mapped to type {@link TO}.
     * @param <TO> The type of the elements that the newly returned collection will have.
     * @throws ArgumentNullException {@code converter} is {@code null}.
     * @implNote This method calls {@link #ConvertAll(Converter, IEqualityComparer)} with {@code comparer} specified to {@code null}.
     */
    @Override
    public <TO> BaseEnumerable<TO> ConvertAll(Converter<T, TO> converter) throws ArgumentNullException { return ConvertAll(converter, null); }

    /**
     * Stores the specified portion of elements of the current collection to a new instance.
     * @param index The starting index to start copying elements from the current collection.
     * @param count The number of elements to copy from the current collection to the new one.
     * @return A new collection containing only the specified portion of elements.
     * @throws ArgumentException {@code index} + {@code count} was exceeding the collection's bounds (Optional, if the number of elements are unknown)
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     * @apiNote -&gt; Typically, an implementer of this API should return an instance behaviorally the same as the current one
     * (i.e. having the same equality comparer, having the same API contract), apart from the slicing properties. <br />
     * -&gt; Note, if the current collection is a thread-safe collection, it is NOT necessary that the returned collection will be thread-safe as well.
     * Check the returned object against {@link com.github.mdcdi1315.basemodslib.utils.ISynchronized} to verify that it will be a synchronized collection.
     */
    @Override
    public BaseEnumerable<T> Slice(int index, int count)
            throws ArgumentException, ArgumentOutOfRangeException
    {
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (count == 0) {
            return new EmptyBaseEnumerable<>();
        } else {
            return new SliceEnumerable<>(this, index, count);
        }
    }

    /**
     * Stores the specified portion of elements of the current collection to a new instance.
     * @param count The number of elements to copy from the current collection to the new one.
     * @return A new collection containing only the specified portion of elements.
     * @throws ArgumentException {@code count} was exceeding the collection's bounds (Optional if the number of elements are unknown)
     * @throws ArgumentOutOfRangeException {@code count} is a negative value.
     * @apiNote -&gt; Typically, an implementer of this API return an instance behaviorally the same as the current one
     * (i.e. having the same equality comparer, having the same API contract), apart from the slicing properties. <br />
     * -&gt; Note, if the current collection is a thread-safe collection, it is NOT necessary that the returned collection will be thread-safe as well.
     * Check the returned object against {@link com.github.mdcdi1315.basemodslib.utils.ISynchronized} to verify that it will be a synchronized collection.
     */
    @Override
    public BaseEnumerable<T> Slice(int count) throws ArgumentException { return Slice(0, count); }
}
