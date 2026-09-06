package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * The exception that is thrown when there is an attempt to dereference a null object reference.
 */
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class NullReferenceException
    extends SystemException
{
    private static final String DEFAULT_MESSAGE = "Object reference not set to an instance of an object.";

    /**
     * Initializes a new instance of the {@link NullReferenceException} class, setting the Message property of the new instance
     * to a system-supplied message that describes the error, such as "The value 'null' was found where an instance of an object was required." <br />
     * This message takes into account the current system culture.
     */
    public NullReferenceException()
    {
        super(DEFAULT_MESSAGE);
        // HResult = HResults.E_POINTER;
    }

    /**
     * Initializes a new instance of the {@link NullReferenceException} class with a specified error message.
     * @param message A {@link String} that describes the error.
     *               The content of message is intended to be understood by humans.
     *               The caller of this constructor is required to ensure that this string has been localized for the current system culture.
     */
    public NullReferenceException(@AllowNull String message)
    {
        super(message == null ? DEFAULT_MESSAGE : message);
        // HResult = HResults.E_POINTER;
    }

    /**
     * Initializes a new instance of the NullReferenceException class with a specified error message
     * and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception.
     *                       If the {@code innerException} parameter is not {@code null},
     *                       the current exception is raised in a catch block that handles the inner exception.
     */
    public NullReferenceException(@AllowNull String message, @AllowNull Exception innerException)
    {
        super(message == null ? DEFAULT_MESSAGE : message, innerException);
        // HResult = HResults.E_POINTER;
    }
}
