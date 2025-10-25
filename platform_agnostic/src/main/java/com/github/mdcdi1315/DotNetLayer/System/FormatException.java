package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * The exception that is thrown when the format of an argument is invalid, or when a composite format string is not well formed.
 */
public class FormatException
    extends SystemException
{
    /**
     * Initializes a new instance of the {@link FormatException} class.
     */
    public FormatException() {
        super("Invalid format.");
    }

    /**
     * Initializes a new instance of the {@link FormatException} class with a specified error message.
     * @param message The message that describes the error.
     */
    public FormatException(@MaybeNull String message) {
        super(message);
    }

    /**
     * Initializes a new instance of the {@link FormatException} class with a specified error message and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception.
     *                       If the {@code innerException} parameter is not a null reference ({@code Nothing} in Visual Basic), the current exception is raised in a {@code catch} block that handles the inner exception.
     */
    public FormatException(@MaybeNull String message, @MaybeNull Exception innerException) {
        super(message, innerException);
    }
}
