package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * The exception that is thrown for errors in an arithmetic, casting, or conversion operation.
 */
public class ArithmeticException
    extends SystemException
{
    /**
     * Initializes a new instance of the {@link ArithmeticException} class.
     */
    public ArithmeticException() {
        super("The arithmetic operation is not permitted.");
    }

    /**
     * Initializes a new instance of the {@link ArithmeticException} class with a specified error message.
     * @param message A {@link String} that describes the error.
     */
    public ArithmeticException(@MaybeNull String message) {
        super(message);
    }

    /**
     * Initializes a new instance of the {@link ArithmeticException} class with a specified error message and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception. If the innerException parameter is not a null reference, the current exception is raised in a catch block that handles the inner exception.
     */
    public ArithmeticException(@MaybeNull String message, @MaybeNull Exception innerException) {
        super(message, innerException);
    }
}
