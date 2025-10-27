package com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices;

import com.github.mdcdi1315.DotNetLayer.IndexerPropertyEmulator;

/**
 * Defines a general-purpose Tuple implementation that allows access to Tuple instance members without knowing the underlying Tuple type.
 */
public interface ITuple
    extends IndexerPropertyEmulator<Integer, Object>
{
    /**
     * Gets the number of elements in this {@code Tuple} instance.
     * @return The number of elements in this {@code Tuple} instance.
     */
    int GetLength();
}
