package com.github.mdcdi1315.basemodslib.utils.random.weighted;

/**
 * Implementers of the interface define that their instances are weighted entries. <br />
 * Thus, random weight operations can be performed on such objects.
 */
public interface IWeightedEntry
{
    /**
     * Gets the weight value associated with the instance.
     * @return The weight value.
     */
    int GetWeight();
}
