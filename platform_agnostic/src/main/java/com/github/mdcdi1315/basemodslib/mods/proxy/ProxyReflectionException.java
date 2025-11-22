package com.github.mdcdi1315.basemodslib.mods.proxy;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLibraryException;

/**
 * Provides the base exception class for reflection exceptions regarding the entire proxy object instantiation process.
 */
public class ProxyReflectionException
    extends BaseModsLibraryException
{
    private final Exception exception;

    /**
     * Initializes a new instance of the {@link ProxyReflectionException} class.
     * @param message The message that describes the error condition that was encountered.
     */
    public ProxyReflectionException(@MaybeNull String message) { super(message); exception = null; }

    /**
     * Initializes a new instance of the {@link ProxyReflectionException} class.
     * @param message The message that describes the error condition that was encountered.
     * @param inner The exception that is the cause of this exception object to be created.
     */
    public ProxyReflectionException(@MaybeNull String message, @MaybeNull Exception inner) { super(message); exception = inner; }

    public Exception GetException() { return exception; }

    @Override
    public String getMessage() {
        if (exception == null) {
            return super.getMessage();
        } else {
            return super.getMessage() + "\nUnderlying exception: " + exception;
        }
    }
}