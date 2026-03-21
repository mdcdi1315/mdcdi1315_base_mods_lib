package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import java.io.Closeable;

/**
 * Provides a layout over how data streams control the wrapped data stream.
 */
public interface IStreamOwner
    extends Closeable
{
    /**
     * Gets a value whether the current instance has control over the wrapped data stream.
     * @return A value whether the current instance has control over the wrapped data stream.
     */
    boolean GetIsOwner();

    /**
     * Sets a value whether ownership over the wrapped data stream is required.
     * @param value A value whether ownership over the wrapped data stream is required.
     * @throws InvalidOperationException The operation is not permitted at the specified time, or due to the state that the implementing object has.
     */
    void SetIsOwner(boolean value) throws InvalidOperationException;
}
