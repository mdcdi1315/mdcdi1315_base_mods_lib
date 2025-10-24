package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.CallerArgumentExpression;

/**
 * The {@code ArgumentNullException} class is a convenience exception for indicating null-passed arguments or parameters to a method call.
 * 
 * <p>It does also contain some static methods for directly creating and throwing such instances.</p>
 */
public class ArgumentNullException
        extends ArgumentException
{
    /**
     * Throws an {@code ArgumentNullException} if argument is null.
     * @param any The reference type argument to validate as non-null.
     * @param pname The name of the parameter with which 'any' corresponds.
     * @throws ArgumentNullException Thrown if <em>any</em> is 'null'.
     */
    public static void ThrowIfNull(Object any , @CallerArgumentExpression(ParameterName = "any") String pname)
            throws ArgumentNullException
    {
        if (any == null) {
            throw new ArgumentNullException(pname);
        }
    }

    /**
     * Throws an {@code ArgumentNullException} if argument is null.
     * @param any The reference type argument to validate as non-null.
     * @throws ArgumentNullException Thrown if <em>any</em> is 'null'.
     */
    public static void ThrowIfNull(Object any)
            throws ArgumentNullException
    {
        ThrowIfNull(any , null);
    }

    /**
     * Initializes a new instance of the {@code ArgumentNullException} class with the specified name of the parameter that caused this exception to be thrown.
     * @param paramname The name of the parameter that was null.
     */
    public ArgumentNullException(String paramname)
    {
        super("The specified parameter was null." , paramname);
    }

    /**
     * Initializes a new instance of the {@code ArgumentNullException} class with the specified name of
     * the parameter that causes this exception and the message that further describes why this exception was thrown.
     * @param paramname The name of the parameter that was null.
     * @param message A more detailed message why this exception was thrown
     */
    public ArgumentNullException(String paramname , String message)
    {
        super(message , paramname);
    }

    /**
     * Initializes a new instance of the {@code ArgumentNullException} class with a specified
     * error message and the exception that is the cause of this exception.
     * @param message The error message that explains the reason for this exception.
     * @param innerException The exception that is the cause of the current exception, or a null reference
     * (Nothing in Visual Basic) if no inner exception is specified.
     */
    public ArgumentNullException(String message, Exception innerException)
    {
        super(message , innerException);
    }
}
