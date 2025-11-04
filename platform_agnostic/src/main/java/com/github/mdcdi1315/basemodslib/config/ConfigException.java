package com.github.mdcdi1315.basemodslib.config;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLibraryException;

/**
 * Defines the base class for exceptions related to this configuration package API's. <br />
 * This class should be inherited.
 */
public class ConfigException
    extends BaseModsLibraryException
{
    /**
     * Gets the cause of the exception that caused this object to be created. <br />
     * Can be null if no such exception object is found.
     */
    @AllowNull
    protected final Exception cause;

    /**
     * Creates a new instance of the {@link ConfigException} class,  <br />
     * providing the error message describing the cause of this exception, <br />
     * and the inner exception that is the cause of this exception.
     * @param message An error message describing the exception's cause.
     * @param inner The inner exception that is the cause of this exception.
     */
    protected ConfigException(@MaybeNull String message, @MaybeNull Exception inner)
    {
        super(message);
        cause = inner;
    }

    /**
     * Gets the cause of this exception, if any.
     * @return The {@link Exception} instance that caused this {@link ConfigException} object to be created.
     */
    @MaybeNull
    public Exception GetCause() {
        return cause;
    }

    @Override
    public String getMessage() {
        if (cause != null) {
            return String.format("%s\nException Data: %s", super.getMessage(), cause);
        } else {
            return super.getMessage();
        }
    }
}
