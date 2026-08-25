package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ICloneable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a base interface for collections whose
 * elements can be snapshot to a new enumerable instance.
 * <h4>The difference between this interface and the {@link ISupportsCloning} interface</h4>
 * The {@link ISupportsCloning} interface is an extension to the {@link ISupportsSlicing} interface,
 * and is specialized in {@link ITraversableCollection} instances. <br />
 * Rather, this interface can be also applied to collections that cannot specify the
 * {@link ISupportsSlicing} interface, which is a requirement for the {@link ISupportsCloning}
 * interface, or you want to be ensured that you will have a collection instance
 * returned from {@link #Clone()}. <br />
 * This interface does also sit as an attachment to the {@link ISupportsCloning} interface.
 * @param <T> The type of elements to enumerate.
 * @since 1.0.37
 */
public interface ICloneableEnumerable<T>
    extends ICloneable, IEnumerable<T>
{
    /**
     * Creates a new {@link ICloneableEnumerable} instance
     * created from this instance, that is a copy of this
     * instance. It is required that the same elements
     * are returned by the new instance.
     * @return The new instance of the {@link ICloneableEnumerable} interface
     * that contains the same sequence of elements as this one, and with the
     * same order.
     * @apiNote Implementers of this API can choose to implement
     * this with one of the two approaches provided below:
     * <ol>
     *     <li>
     *         Create a new instance of the current enumerable, but any changes happening to
     *         {@code this} are being reflected to the returned instance as well.
     *     </li>
     *     <li>
     *         Create a new instance of the current enumerable, and copy all the collection's
     *         elements to the new instance. As such, when the {@code this} collection is mutated,
     *         the returned collection is not modified.
     *     </li>
     * </ol>
     * However, it would be useful to explicitly state which
     * approach was selected and why that decision was made.
     */
    @NotNull
    ICloneableEnumerable<T> Clone();
}
