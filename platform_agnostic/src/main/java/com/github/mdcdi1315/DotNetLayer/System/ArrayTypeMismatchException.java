package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;


/**
 * The exception that is thrown when an attempt is made to store an element of the wrong type within an array.
 */
public class ArrayTypeMismatchException
        extends SystemException
{
    /**
     * Initializes a new instance of the {@link ArrayTypeMismatchException} class.
     */
    public ArrayTypeMismatchException()
    {
        super();
    }

    /**
     * Initializes a new instance of the {@link ArrayTypeMismatchException} class with a specified error message.
     * @param message A {@link String} that describes the error.
     */
    public ArrayTypeMismatchException(String message)
    {
        super(message);
    }

    /**
     * Initializes a new instance of the {@link ArrayTypeMismatchException} class with
     * a specified error message and a reference to the inner exception that is the
     * cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception. If the innerException
     * parameter is not a null reference, the current exception is raised in a catch
     * block that handles the inner exception.
     */
    public ArrayTypeMismatchException(String message, @MaybeNull Exception innerException)
    {
        super(message , innerException);
    }
}