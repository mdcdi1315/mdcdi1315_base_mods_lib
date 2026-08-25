package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * The exception that is thrown when there is an attempt to divide an integral or Decimal value by zero.
 */
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class DivideByZeroException
    extends ArithmeticException
{
    private static final String MESSAGE = "Attempted to divide by zero.";

    /**
     * Initializes a new instance of the {@link DivideByZeroException} class.
     */
    public DivideByZeroException() { super(MESSAGE); }

    /**
     * Initializes a new instance of the {@link DivideByZeroException} class with a specified error message.
     * @param message A {@link String} that describes the error.
     */
    public DivideByZeroException(@AllowNull String message) { super(message == null ? MESSAGE : message); }

    /**
     * Initializes a new instance of the {@link DivideByZeroException} class with a specified
     * error message and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception. <br />
     *                       If the {@code innerException} parameter is not {@code null},
     *                       the current exception is raised in a catch block that handles the inner exception.
     */
    public DivideByZeroException(@AllowNull String message, @AllowNull Exception innerException) { super(message == null ? MESSAGE : message, innerException); }
}
