package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerable;

/**
 * Base interface for collections whose number of elements is known. <br />
 * This interface is even more low-level from the {@link ICountableCollection},
 * and this does not utilize generics.
 * @since 1.0.40
 */
public interface ICountableCollectionBase
    extends IEnumerable
{
    /**
     * Gets the number of elements contained in the current countable collection.
     * @return The number of elements contained in the current countable collection.
     */
    int GetCount();
}
