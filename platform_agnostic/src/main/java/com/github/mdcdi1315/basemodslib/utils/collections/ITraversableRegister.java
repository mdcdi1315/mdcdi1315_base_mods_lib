package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Specialization of the {@link IRegister} interface for collections that can be traversed by an index value.
 * @param <T> The type of the items to be held by this register object.
 * @since 1.0.18
 */
public interface ITraversableRegister<T>
        extends IRegister<T>, ITraversableCollection<T>
{
    /**
     * Determines the index of a specific item in the {@link ITraversableRegister}.
     * @param item The object to locate in the {@link ITraversableRegister}.
     * @return The index of {@code item} if found in the list; otherwise, -1.
     */
    int IndexOf(@AllowNull T item);
}
