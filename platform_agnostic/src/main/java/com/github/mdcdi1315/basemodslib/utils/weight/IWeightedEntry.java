package com.github.mdcdi1315.basemodslib.utils.weight;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Implementers of the interface define their instances can be weighted entries. <br />
 * Thus, random weight operations can be performed on such objects.
 */
public interface IWeightedEntry
{
    /**
     * Gets the {@link Weight} associated with this instance.
     * @return The weighted value of this entry.
     */
    @NotNull
    Weight GetWeight();
}
