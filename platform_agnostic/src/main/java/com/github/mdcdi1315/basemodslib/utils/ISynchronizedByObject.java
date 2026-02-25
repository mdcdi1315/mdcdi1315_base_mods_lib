package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Specialization for the {@link ISynchronized} interface for classes that use synchronization (namely lock objects) objects to perform thread safety.
 * @since 1.0.18
 */
public interface ISynchronizedByObject
    extends ISynchronized
{
    /**
     * Gets the object that is used to ensure thread safety.
     * @return The object that is used to ensure thread safety.
     */
    @NotNull
    Object GetSyncObject();
}
