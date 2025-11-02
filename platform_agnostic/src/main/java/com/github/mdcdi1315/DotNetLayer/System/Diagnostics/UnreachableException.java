package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.DotNetLayer.System.Exception;

/**
 * The exception that is thrown when the program executes an instruction that was thought to be unreachable.
 */
public final class UnreachableException
    extends Exception
{
    /**
     * Initializes a new instance of the {@link UnreachableException} class with the default error message.
     */
    public UnreachableException() { super(); }

    /**
     * Initializes a new instance of the {@link UnreachableException} class with a specified error message.
     * @param message The error message that explains the reason for the exception.
     */
    public UnreachableException(@MaybeNull String message) { super(message); }

    /**
     * Initializes a new instance of the {@link UnreachableException} class with a specified error message and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception.
     */
    public UnreachableException(@MaybeNull String message, @MaybeNull Exception innerException) { super(message, innerException); }
}
