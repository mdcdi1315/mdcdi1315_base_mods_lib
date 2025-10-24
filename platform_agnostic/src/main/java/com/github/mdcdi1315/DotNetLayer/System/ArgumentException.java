package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.CallerArgumentExpression;

/**
 * The exception that is thrown when one of the arguments provided to a method is not valid.
 */
public class ArgumentException
    extends SystemException
{
    private static final String DEFAULT_MSG = "Invalid argument.";
    private final String msg, parametername;

    /**
     * Throws an exception if argument is null or empty.
     * @param argument The string argument to validate as non-null and non-empty.
     * @param paramName The name of the parameter with which argument corresponds.
     * @exception ArgumentNullException argument is null.
     * @exception ArgumentException argument is empty.
     */
    public static void ThrowIfNullOrEmpty(@NotNull String argument, @CallerArgumentExpression(ParameterName = "argument") String paramName)
        throws ArgumentException
    {
        if (argument == null) {
            throw new ArgumentNullException(paramName);
        }
        if (argument.isEmpty()) {
            throw new ArgumentException("The string argument is empty." , paramName);
        }
    }

    /**
     * Throws an exception if argument is null or empty.
     * @param argument The string argument to validate as non-null and non-empty.
     * @exception ArgumentNullException argument is null.
     * @exception ArgumentException argument is empty.
     */
    public static void ThrowIfNullOrEmpty(@NotNull String argument)
            throws ArgumentException
    {
        if (argument == null) {
            throw new ArgumentNullException(null);
        }
        if (argument.isEmpty()) {
            throw new ArgumentException("The string argument is empty.");
        }
    }

    /**
     * Initializes a new instance of the {@code ArgumentException} class.
     */
    public ArgumentException()
    {
        msg = DEFAULT_MSG;
        parametername = null;
    }

    /**
     * Initializes a new instance of the {@code ArgumentException} class with a specified error message.
     * @param message The error message that explains the reason for the exception.
     */
    public ArgumentException(String message)
    {
        super(message);
        msg = message == null ? DEFAULT_MSG : message;
        this.parametername = null;
    }

    /**
     * Initializes a new instance of the {@code ArgumentException} class with a specified
     * error message and a reference to the inner exception that is the cause of this
     * exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception. If the innerException
     * parameter is not a null reference, the current exception is raised in a catch
     * block that handles the inner exception.
     */
    public ArgumentException(String message, Exception innerException)
    {
        super(message , innerException);
        msg = message == null ? DEFAULT_MSG : message;
        parametername = null;
    }

    /**
     * Initializes a new instance of the {@link ArgumentException} class with a specified
     * error message and the name of the parameter that causes this exception.
     * @param message The error message that explains the reason for the exception.
     * @param paramName The name of the parameter that caused the current exception.
     */
    public ArgumentException(String message, String paramName)
    {
        msg = message == null ? DEFAULT_MSG : message;
        parametername = paramName;
    }

    /**
     * Gets the error message and the parameter name, or only the error message if no parameter name is set.
     * @return A text string describing the details of the exception.
     */
    @Override
    public String getMessage()
    {
        if (parametername == null) {
            return msg;
        } else {
            return String.format("%s\nParameter Name: %s" , msg , parametername);
        }
    }

    /**
     * Gets the name of the parameter that causes this exception. May be null.
     * @return The parameter name.
     */
    @MaybeNull
    public String getParamName()
    {
        return parametername;
    }
}
