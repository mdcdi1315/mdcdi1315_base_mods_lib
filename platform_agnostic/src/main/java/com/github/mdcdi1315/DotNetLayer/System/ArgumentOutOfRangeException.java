package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * The exception that is thrown when the value of an argument is outside the allowable
 * range of values as defined by the invoked method.
 */
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class ArgumentOutOfRangeException
    extends ArgumentException
{
    private static final String DEFAULT_MESSAGE = "The specified argument was out of the range of valid values.";

    @AllowNull
    private final Object actual_value;

    /**
     * Initializes a new instance of the {@link ArgumentOutOfRangeException} class.
     */
    public ArgumentOutOfRangeException() { super(); actual_value = null; }

    /**
     * Initializes a new instance of the {@link ArgumentOutOfRangeException} class with
     * the name of the parameter that causes this exception.
     * @param paramName The name of the parameter that causes this exception.
     */
    public ArgumentOutOfRangeException(@AllowNull String paramName)
    {
        super(DEFAULT_MESSAGE, paramName);
        actual_value = null;
    }

    /**
     * Initializes a new instance of the {@link ArgumentOutOfRangeException} class with
     * a specified error message and the exception that is the cause of this exception.
     * @param message The error message that explains the reason for this exception.
     * @param innerException The exception that is the cause of the current exception, or a null reference
     * (Nothing in Visual Basic) if no inner exception is specified.
     */
    public ArgumentOutOfRangeException(@AllowNull String message, @AllowNull Exception innerException)
    {
        super(message == null ? DEFAULT_MESSAGE : message, innerException);
        actual_value = null;
    }

    /**
     * Initializes a new instance of the {@link ArgumentOutOfRangeException} class with
     * the name of the parameter that causes this exception and a specified error message.
     * @param paramName The name of the parameter that caused the exception.
     * @param message The message that describes the error.
     */
    public ArgumentOutOfRangeException(@AllowNull String paramName, @AllowNull String message)
    {
        super(message == null ? DEFAULT_MESSAGE : message, paramName);
        actual_value = null;
    }

    /**
     * Initializes a new instance of the {@link ArgumentOutOfRangeException} class with
     * the parameter name, the value of the argument, and a specified error message.
     * @param paramName The name of the parameter that caused the exception.
     * @param actualValue The value of the argument that causes this exception.
     * @param message The message that describes the error.
     */
    public ArgumentOutOfRangeException(@AllowNull String paramName, @AllowNull Object actualValue, @AllowNull String message)
    {
        super(message == null ? DEFAULT_MESSAGE : message, paramName);
        actual_value = actualValue;
    }

    /**
     * Gets the argument value that causes this exception.
     * @return The value of the parameter that caused the current {@link Exception}.
     */
    @MaybeNull
    public Object getActualValue() { return actual_value; }

    @Override
    public String getMessage()
    {
        String s = super.getMessage();
        if (actual_value == null) {
            return s;
        } else {
            String valueMessage = StringUtils.Concat("Actual value: ", actual_value);
            return (s == null) ? valueMessage : StringUtils.Concat(s, "\n", valueMessage);
        }
    }
}
