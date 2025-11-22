package com.github.mdcdi1315.basemodslib.mods.proxy;

import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLibraryException;

/**
 * The exception that is thrown when a proxy implementation class name was not present in the mod,
 * or when the specified class is not an implementation class (that is, is abstract),
 * or when an implementation of that proxy does not exist for the current mod loader.
 */
public final class ProxyImplementationNotFoundException
        extends BaseModsLibraryException
{
    /**
     * Initializes a new and empty instance of the {@link ProxyImplementationNotFoundException} class.
     */
    public ProxyImplementationNotFoundException() { super(); }

    /**
     * Initializes a new instance of the {@link ProxyImplementationNotFoundException} class, providing the message that caused this exception object to be created.
     * @param message The message that describes the error condition that was encountered.
     */
    public ProxyImplementationNotFoundException(@MaybeNull String message) { super(message); }

    /**
     * Initializes a new instance of the {@link ProxyImplementationNotFoundException} class, providing the message that caused this
     * exception object to be created, as well as the exception object that is the actual cause of creating this exception object.
     * @param message The message that describes the error condition that was encountered.
     * @param exception The exception object that is the actual cause of creating this exception object.
     */
    public ProxyImplementationNotFoundException(@MaybeNull String message, @MaybeNull Exception exception) { super(message, exception); }
}
