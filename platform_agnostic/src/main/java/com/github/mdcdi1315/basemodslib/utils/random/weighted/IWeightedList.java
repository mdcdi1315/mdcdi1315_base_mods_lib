package com.github.mdcdi1315.basemodslib.utils.random.weighted;

/**
 * Accessor interface for collections that work tightly with the weighted list logic.
 */
public interface IWeightedList
{
    /**
     * Gets the sum of all the weights contained in the collection. <br />
     * Will be zero if the collection is empty.
     * @return The sum of all the weights.
     */
    int GetTotalWeight();
}
