package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * The exception that is thrown when a requested method or operation is not implemented.
 */
public class NotImplementedException
    extends SystemException
{
    /**
     * Initializes a new instance of the {@link NotImplementedException} class with default properties.
     */
    public NotImplementedException() { super(StringUtils.Empty , null); }

    /**
     * Initializes a new instance of the {@link NotImplementedException} class with a specified error message.
     * @param message The error message that explains the reason for the exception.
     */
    public NotImplementedException(@MaybeNull String message) { super(message); }

    /**
     * Initializes a new instance of the {@link NotImplementedException} class with a specified error message and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerexception The exception that is the cause of the current exception. If the {@code innerexception} parameter is not {@code null}, the current exception is raised in a {@code catch} block that handles the inner exception.
     */
    public NotImplementedException(@MaybeNull String message , @MaybeNull Exception innerexception) { super(message , innerexception); }
}
