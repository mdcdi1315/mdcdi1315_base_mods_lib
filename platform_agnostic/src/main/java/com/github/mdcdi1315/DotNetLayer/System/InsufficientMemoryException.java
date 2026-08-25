package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * The exception that is thrown when a check for sufficient available memory fails. This class cannot be inherited.
 */
public final class InsufficientMemoryException
    extends OutOfMemoryException
{
    /**
     * Initializes a new instance of the {@link InsufficientMemoryException} class with a system-supplied message that describes the error.
     */
    public InsufficientMemoryException() { super(); }

    /**
     * Initializes a new instance of the {@link InsufficientMemoryException} class with a specified message that describes the error.
     * @param message The message that describes the exception. The caller of this constructor is required to ensure that this string has been localized for the current system culture.
     */
    public InsufficientMemoryException(@AllowNull String message) { super(message); }

    /**
     * Initializes a new instance of the {@link InsufficientMemoryException} class with a specified error message and a reference to the inner exception that is the cause of this exception.
     * @param message The message that describes the exception. The caller of this constructor is required to ensure that this string has been localized for the current system culture.
     * @param innerException The exception that is the cause of the current exception.
     *                       If the {@code inner} parameter is not {@code null}, the current exception is raised in a {@code catch} block that handles the inner exception.
     */
    public InsufficientMemoryException(@AllowNull String message, @AllowNull Exception innerException) { super(message, innerException); }
}
