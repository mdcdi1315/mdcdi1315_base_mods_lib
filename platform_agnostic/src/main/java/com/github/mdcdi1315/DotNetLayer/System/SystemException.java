package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Serves as the base class for system exceptions namespace.
 */
public class SystemException
        extends Exception
{
    /**
     * Initializes a new instance of the {@link SystemException} class.
     */
    public SystemException()
    {
        super();
    }

    /**
     * Initializes a new instance of the {@link SystemException} class with a specified error message.
     * @param message The message that describes the error.
     */
    public SystemException(String message)
    {
        super(message);
    }

    /**
     * Initializes a new instance of the {@link SystemException} class with a specified
     * error message and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception. If the innerException
     *  parameter is not a null reference (Nothing in Visual Basic), the current exception
     *  is raised in a catch block that handles the inner exception.
     */
    public SystemException(String message, @MaybeNull Exception innerException)
    {
        super(message , innerException);
    }
}