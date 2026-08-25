package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * The exception that is thrown for invalid casting or explicit conversion.
 */
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class InvalidCastException
    extends SystemException
{
    private static final String DEFAULT_MESSAGE = "Specified cast is not valid.";

    /**
     * Initializes a new instance of the {@link InvalidCastException} class.
     */
    public InvalidCastException()
    {
        super(DEFAULT_MESSAGE);
        // HResult = HResults.COR_E_INVALIDCAST;
    }

    /**
     * Initializes a new instance of the {@link InvalidCastException} class with a specified error message.
     * @param message The message that describes the error.
     */
    public InvalidCastException(@AllowNull String message)
    {
        super(message == null ? DEFAULT_MESSAGE : message);
        // HResult = HResults.COR_E_INVALIDCAST;
    }

    /**
     * Initializes a new instance of the InvalidCastException class with a specified error message
     * and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception.
     *                       If the {@code innerException} parameter is not {@code null},
     *                       the current exception is raised in a catch block that handles the inner exception.
     */
    public InvalidCastException(@AllowNull String message, @AllowNull Exception innerException)
    {
        super(message == null ? DEFAULT_MESSAGE : message, innerException);
        // HResult = HResults.COR_E_INVALIDCAST;
    }

    /**
     * Initializes a new instance of the {@link InvalidCastException} class with a specified message and error code.
     * @param message The message that indicates the reason the exception occurred.
     * @param errorCode The error code (HRESULT) value associated with the exception.
     */
    public InvalidCastException(@AllowNull String message, int errorCode)
    {
        super(message == null ? DEFAULT_MESSAGE : message);
    }
}
