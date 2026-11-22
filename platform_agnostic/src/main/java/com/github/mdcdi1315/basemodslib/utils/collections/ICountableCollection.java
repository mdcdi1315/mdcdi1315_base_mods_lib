package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IReadOnlyCollection;

/**
 * Base interface for collections whose number of elements is known. <br />
 * Since BML 1.0.42, this does also implicitly implement {@link IReadOnlyCollection},
 * so you no longer need to convert between both.
 * @since 1.0.38
 */
public interface ICountableCollection<T>
    extends IReadOnlyCollection<T>, ICountableCollectionBase
{
    /**
     * Gets the number of elements contained in the current countable collection.
     * @return The number of elements contained in the current countable collection.
     */
    @Override
    int GetCount();

    @Override
    default int getCount() { return GetCount(); }
}
