package com.github.mdcdi1315.DotNetLayer.System;

/**
 * The exception that is thrown when the operating system denies access because of an I/O error or a specific type of security error.
 */
public class UnauthorizedAccessException
        extends SystemException
{
    /**
     * Initializes a new instance of the {@link UnauthorizedAccessException} class.
     */
    public UnauthorizedAccessException() {
        super();
    }

    /**
     * Initializes a new instance of the {@link UnauthorizedAccessException} class with a specified error message.
     * @param message The message that describes the error.
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    /**
     *  Initializes a new instance of the {@link UnauthorizedAccessException} class with a specified error message and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param inner The exception that is the cause of the current exception. If the inner
     * parameter is not a null reference, the current exception is raised in a catch
     * block that handles the inner exception.
     */
    public UnauthorizedAccessException(String message, Exception inner) {
        super(message , inner);
    }
}