package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a declaration for collections that return a new instance of the same type,
 * but they do have the same number of elements and behavioral properties as the original collection. <br />
 * By default, it is applied transitively on classes that implement the {@link ISupportsSlicing} interface, because if they can support cloning only a portion of them,
 * that means that the entire collection data could be cloned as well. <br />
 * Can be only applied to traversable collections.
 * @param <T> The type of the elements that the current collection holds.
 * @since 1.0.31
 */
public interface ISupportsCloning<T>
    extends ISupportsSlicing<T>, ITraversableCollection<T>
{
    /**
     * Stores the specified portion of elements of the current collection to a new instance.
     * @param index The starting index to start copying elements from the current collection.
     * @param count The number of elements to copy from the current collection to the new one.
     * @return A new collection containing only the specified portion of elements.
     * @throws ArgumentException {@code index} + {@code count} was exceeding the collection's bounds (Optional if the number of elements are unknown)
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     * @apiNote -&gt; Typically, an implementer of this API should be behaviorally the same as the current one
     * (i.e. having the same equality comparer, having the same API contract), apart from the slicing properties. <br />
     * -&gt; Note, if the current collection is a thread-safe collection, it is NOT necessary that the returned collection will be thread-safe as well.
     * Check the returned object against {@link com.github.mdcdi1315.basemodslib.utils.ISynchronized} to verify that it will be a synchronized collection.
     */
    @NotNull
    @Override
    ISupportsCloning<T> Slice(int index, int count) throws ArgumentException;

    /**
     * Stores the specified portion of elements of the current collection to a new instance.
     * @param count The number of elements to copy from the current collection to the new one.
     * @return A new collection containing only the specified portion of elements.
     * @throws ArgumentException {@code count} was exceeding the collection's bounds (Optional if the number of elements are unknown)
     * @throws ArgumentOutOfRangeException {@code count} is a negative value.
     * @apiNote -&gt; Typically, an implementer of this API should be behaviorally the same as the current one
     * (i.e. having the same equality comparer, having the same API contract), apart from the slicing properties. <br />
     * -&gt; Note, if the current collection is a thread-safe collection, it is NOT necessary that the returned collection will be thread-safe as well.
     * Check the returned object against {@link com.github.mdcdi1315.basemodslib.utils.ISynchronized} to verify that it will be a synchronized collection.
     */
    @NotNull
    @Override
    default ISupportsCloning<T> Slice(int count) throws ArgumentException { return Slice(0, count); }

    /**
     * Clones the collection to a new instance of the same collection type. <br />
     * The returned instance has the same elements and the same behavioral characteristics of the current one, but the expression {@code this != Clone()} stands {@code true}.
     * @return A new instance of the current collection instance, containing the same elements and the same behavioral characteristics as this instance.
     * @apiNote Note, if the current collection is a thread-safe collection, it is NOT necessary that the returned collection will be thread-safe as well.
     * Check the returned object against {@link com.github.mdcdi1315.basemodslib.utils.ISynchronized} to verify that it will be a synchronized collection.
     */
    @NotNull
    default ISupportsCloning<T> Clone() { return Slice(0, GetCount()); }
}
