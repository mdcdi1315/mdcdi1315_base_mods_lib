package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.io.OutputStream;

/**
 * Provides an interface for accessing the output stream from an output stream wrapper class.
 */
public interface IOutputStreamWrapper
    extends IStreamOwner
{
    /**
     * Gets the output stream that is wrapped by the implemented object.
     * @return The output stream that is wrapped.
     * @apiNote This should be non-{@code null} when the lifetime of the object implementing this interface is still valid.
     */
    @NotNull
    OutputStream GetWrapped();
}
