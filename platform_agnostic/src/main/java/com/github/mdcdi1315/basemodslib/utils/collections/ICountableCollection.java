package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

/**
 * Base interface for collections whose number of elements is known.
 * @since 1.0.38
 */
public interface ICountableCollection<T>
    extends IEnumerable<T>
{
    /**
     * Gets the number of elements contained in the current countable collection.
     * @return The number of elements contained in the current countable collection.
     */
    int GetCount();
}
