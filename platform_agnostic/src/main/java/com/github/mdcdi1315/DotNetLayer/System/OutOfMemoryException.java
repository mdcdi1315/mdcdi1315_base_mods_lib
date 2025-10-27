package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * The exception that is thrown when there is not enough memory to continue the execution of a program.
 */
public class OutOfMemoryException
    extends SystemException
{
    /**
     * Initializes a new instance of the {@link OutOfMemoryException} class.
     */
    public OutOfMemoryException() {}

    /**
     * Initializes a new instance of the {@link OutOfMemoryException} class with a specified error message.
     * @param message The message that describes the error.
     */
    public OutOfMemoryException(@MaybeNull String message) {
        super(message);
    }

    /**
     * Initializes a new instance of the {@link OutOfMemoryException} class with a specified error message and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception.
     *                      If the {@code inner} parameter is not {@code null}, the current exception is raised in a {@code catch} block that handles the inner exception.
     */
    public OutOfMemoryException(@MaybeNull String message, @MaybeNull Exception innerException) {
        super(message, innerException);
    }
}
