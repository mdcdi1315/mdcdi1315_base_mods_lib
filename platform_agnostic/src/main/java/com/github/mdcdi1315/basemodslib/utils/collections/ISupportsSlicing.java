package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a declaration for collections that return an instance of the same type,
 * but they are able to return a portion of their elements only.
 * @param <T> The type of the elements in the implemented collection.
 * @since 1.0.26
 */
public interface ISupportsSlicing<T>
    extends IEnumerable<T>
{
    /**
     * Stores the specified portion of elements of the current collection to a new instance.
     * @param index The starting index to start copying elements from the current collection.
     * @param count The number of elements to copy from the current collection to the new one.
     * @return A new collection containing only the specified portion of elements.
     * @throws ArgumentException {@code index} + {@code count} was exceeding the collection's bounds (Optional if the number of elements are unknown)
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     * @apiNote -&gt; Typically, an implementer of this API should return an instance behaviorally the same as the current one
     * (i.e. having the same equality comparer, having the same API contract), apart from the slicing properties. <br />
     * -&gt; Note, if the current collection is a thread-safe collection, it is NOT necessary that the returned collection will be thread-safe as well.
     * Check the returned object against {@link com.github.mdcdi1315.basemodslib.utils.ISynchronized} to verify that it will be a synchronized collection.
     */
    @NotNull
    ISupportsSlicing<T> Slice(int index, int count) throws ArgumentException, ArgumentOutOfRangeException;

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
    @NotNull
    default ISupportsSlicing<T> Slice(int count) throws ArgumentException, ArgumentOutOfRangeException { return Slice(0, count); }
}
