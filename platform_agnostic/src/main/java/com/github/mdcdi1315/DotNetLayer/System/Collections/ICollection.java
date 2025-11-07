package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

/**
 * Defines size, enumerators, and synchronization methods for all nongeneric collections.
 */
public interface ICollection
        extends IEnumerable
{
    /**
     * Gets the number of elements contained in the {@link ICollection}.
     * @return The number of elements contained in the {@link ICollection}.
     */
    int getCount();

    /**
     * Gets a value indicating whether access to the {@link ICollection} is synchronized (thread safe).
     * @return true if access to the {@link ICollection} is synchronized (thread safe); otherwise, false.
     */
    boolean getIsSynchronized();

    /**
     * Gets an object that can be used to synchronize access to the {@link ICollection}.
     * @return An object that can be used to synchronize access to the {@link ICollection}.
     */
    Object getSyncRoot();

    /**
     * Copies the elements of the ICollection to an {@link java.lang.reflect.Array}, starting at a particular {@link java.lang.reflect.Array} index.
     * @param array The one-dimensional {@link java.lang.reflect.Array} that is the destination of the elements copied from {@link ICollection}. The {@link java.lang.reflect.Array} must have zero-based indexing.
     * @param index The zero-based index in {@code array} at which copying begins.
     * @throws ArgumentNullException {@code array} is {@code null}.
     * @throws com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException {@code index} is less than zero.
     * @throws ArgumentException {@code array} is multidimensional.<br />
     * -or- <br />
     * The number of elements in the source {@link ICollection} is greater than the available space from index to the end of the destination array. <br />
     * -or- <br />
     * The type of the source {@link ICollection} cannot be cast automatically to the type of the destination array.
     */
    void CopyTo(Object array, int index) throws ArgumentException;
}
