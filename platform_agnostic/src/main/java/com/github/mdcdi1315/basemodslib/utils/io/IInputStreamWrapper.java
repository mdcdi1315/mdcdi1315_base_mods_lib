package com.github.mdcdi1315.basemodslib.utils.io;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.io.InputStream;

/**
 * Provides an interface for accessing the input stream from an input stream wrapper class.
 */
public interface IInputStreamWrapper
    extends IStreamOwner
{
    /**
     * Gets the input stream that is wrapped by the implemented object.
     * @return The input stream that is wrapped.
     * @apiNote This should be non-{@code null} when the lifetime of the object implementing this interface is still valid.
     */
    @NotNull
    InputStream GetWrapped();
}
