package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Provides a way to add once items and access them many times. <br />
 * Items added to objects implementing this interface cannot be removed.
 * @param <T> The type of the items to be held by this register object.
 */
public interface IRegister<T>
    extends IEnumerable<T>
{
    /**
     * Registers an item to this instance.
     * @param item The item to be saved to this instance.
     * @throws ArgumentException The class implementing the interface is enforcing object singularity and {@code item} already exists in the current register object.
     */
    void Register(@AllowNull T item) throws ArgumentException;

    /**
     * Registers a multiple of items to this instance.
     * @param items The item(s) to be saved to this instance.
     * @throws ArgumentNullException {@code items} is {@code null}.
     * @throws ArgumentException The class implementing the interface is enforcing object singularity and {@code items} already exist in the current register object.
     */
    default void RegisterRange(IEnumerable<T> items)
            throws ArgumentNullException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(items);
        try (IEnumerator<T> en = items.GetEnumerator())
        {
            while (en.MoveNext()) { Register(en.getCurrent()); }
        }
    }
}
