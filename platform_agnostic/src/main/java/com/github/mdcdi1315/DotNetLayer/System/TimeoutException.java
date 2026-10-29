package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * The exception that is thrown when the time allotted for a process or operation has expired.
 */
public class TimeoutException
    extends SystemException
{
    private static final String DEFAULT_MSG = "The specified operation was timed out.";

    /**
     * Initializes a new instance of the {@link TimeoutException} class.
     */
    public TimeoutException()
    {
        super(DEFAULT_MSG);
    }

    /**
     * Initializes a new instance of the {@link TimeoutException} class with the specified error message.
     * @param message The message that describes the error.
     */
    public TimeoutException(@AllowNull String message)
    {
        super(message == null ? DEFAULT_MSG : message);
    }

    /**
     * Initializes a new instance of the {@link TimeoutException} class with the specified error message and inner exception.
     * @param message The message that describes the error.
     * @param innerException The exception that is the cause of the current exception.
     *                       If the {@code innerException} parameter is not {@code null}, the current exception is raised in a {@code catch} block that handles the inner exception.
     */
    public TimeoutException(@AllowNull String message, @AllowNull Exception innerException)
    {
        super(message == null ? DEFAULT_MSG : message, innerException);
    }
}
