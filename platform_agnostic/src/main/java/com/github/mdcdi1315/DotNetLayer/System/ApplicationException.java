package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Serves as the base class for application-defined exceptions.
 */
public class ApplicationException
        extends Exception
{
    /**
     * Initializes a new instance of the {@link ApplicationException} class.
     */
    public ApplicationException()
    {
        super();
    }

    /**
     * Initializes a new instance of the {@link ApplicationException} class with a specified error message.
     * @param message A message that describes the error.
     */
    public ApplicationException(String message)
    {
        super(message);
    }

    /**
     * Initializes a new instance of the {@link ApplicationException} class with a specified
     * error message and a reference to the inner exception that is the cause of this
     * exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception. If the innerException
     * parameter is not a null reference, the current exception is raised in a catch
     * block that handles the inner exception.
     */
    public ApplicationException(String message, @MaybeNull Exception innerException)
    {
        super(message , innerException);
    }
}