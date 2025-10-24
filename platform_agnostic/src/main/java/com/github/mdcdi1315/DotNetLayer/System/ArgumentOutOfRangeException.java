package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * The exception that is thrown when the value of an argument is outside the allowable
 * range of values as defined by the invoked method.
 */
public class ArgumentOutOfRangeException
    extends ArgumentException
{
    private Object actualvalue;

    /**
     * Initializes a new instance of the {@link ArgumentOutOfRangeException} class.
     */
    public ArgumentOutOfRangeException() { super(); }

    /**
     * Initializes a new instance of the {@link ArgumentOutOfRangeException} class with
     *     //     the name of the parameter that causes this exception.
     * @param paramName The name of the parameter that causes this exception.
     */
    public ArgumentOutOfRangeException(String paramName)
    {
        super("The specified argument was out of the range of valid values." , paramName);
    }

    /**
     * Initializes a new instance of the {@link ArgumentOutOfRangeException} class with
     * a specified error message and the exception that is the cause of this exception.
     * @param message The error message that explains the reason for this exception.
     * @param innerException The exception that is the cause of the current exception, or a null reference
     * (Nothing in Visual Basic) if no inner exception is specified.
     */
    public ArgumentOutOfRangeException(String message, Exception innerException)
    {
        super(message , innerException);
    }

    /**
     *  Initializes a new instance of the {@link ArgumentOutOfRangeException} class with
     *  the name of the parameter that causes this exception and a specified error message.
     * @param paramName The name of the parameter that caused the exception.
     * @param message The message that describes the error.
     */
    public ArgumentOutOfRangeException(String paramName, String message)
    {
        super(message , paramName);
    }

    /**
     * Initializes a new instance of the {@link ArgumentOutOfRangeException} class with
     * the parameter name, the value of the argument, and a specified error message.
     * @param paramName The name of the parameter that caused the exception.
     * @param actualValue The value of the argument that causes this exception.
     * @param message The message that describes the error.
     */
    public ArgumentOutOfRangeException(String paramName, Object actualValue, String message)
    {
        super(message , paramName);
        actualvalue = actualValue;
    }

    /**
     * Gets the argument value that causes this exception.
     * @return The value of the parameter that caused the current {@link Exception}.
     */
    @MaybeNull
    public Object getActualValue()
    {
        return actualvalue;
    }
}
