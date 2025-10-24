package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * The exception that is thrown when an attempt is made to access an element of
 * an array or collection with an index that is outside its bounds.
 */
public final class IndexOutOfRangeException
        extends SystemException
{
    /**
     * Initializes a new instance of the {@link IndexOutOfRangeException} class.
     */
    public IndexOutOfRangeException()
    {
        super();
    }

    /**
     * Initializes a new instance of the {@link IndexOutOfRangeException} class with a specified error message.
     * @param message The message that describes the error.
     */
    public IndexOutOfRangeException(String message)
    {
        super(message);
    }

    /**
     *  Initializes a new instance of the {@link IndexOutOfRangeException} class with
     *  a specified error message and a reference to the inner exception that is the
     *  cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception. If the innerException
     * parameter is not a null reference (Nothing in Visual Basic), the current exception
     * is raised in a catch block that handles the inner exception.
     */
    public IndexOutOfRangeException(String message, @MaybeNull Exception innerException)
    {
        super(message , innerException);
    }
}