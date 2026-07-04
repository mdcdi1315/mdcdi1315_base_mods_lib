package com.github.mdcdi1315.DotNetLayer.System;

/**
 * Provides a mechanism for releasing unmanaged resources.
 */
public interface IDisposable
    extends AutoCloseable
{
    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting unmanaged resources.
     */
    void Dispose();

    /**
     * .NET Layer override - provides the default implementation that calls in the {@link #Dispose()} method.
     */
    @Override
    default void close() { Dispose(); }
}
