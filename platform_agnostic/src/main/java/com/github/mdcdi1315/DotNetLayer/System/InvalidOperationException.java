package com.github.mdcdi1315.DotNetLayer.System;

/**
 * The exception that is thrown when a method call is invalid for the object's current state.
 */
public class InvalidOperationException
        extends SystemException
{
    /**
     * Initializes a new instance of the {@link InvalidOperationException} class.
     */
    public InvalidOperationException() { super(); }

    /**
     * Initializes a new instance of the {@link InvalidOperationException} class with a specified error message.
     * @param message The message that describes the error.
     */
    public InvalidOperationException(String message) { super(message); }

    /**
     * Initializes a new instance of the {@link InvalidOperationException} class with
     * a specified error message and a reference to the inner exception that is the
     * cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception. If the innerException
     * parameter is not a null reference (Nothing in Visual Basic), the current exception
     * is raised in a catch block that handles the inner exception.
     */
    public InvalidOperationException(String message, Exception innerException) { super(message , innerException); }
}
