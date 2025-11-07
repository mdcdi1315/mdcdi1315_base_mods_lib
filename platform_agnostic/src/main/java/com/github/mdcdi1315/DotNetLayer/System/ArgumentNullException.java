package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.CallerArgumentExpression;

/**
 * The exception that is thrown when a null reference ({@code Nothing} in Visual Basic) is passed to a method that does not accept it as a valid argument.
 */
public class ArgumentNullException
        extends ArgumentException
{
    private static final String DEFAULT_MSG_VALUE = "Value cannot be null.";

    /**
     * Throws an {@link ArgumentNullException} if argument is null.
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
     * Throws an {@link ArgumentNullException} if argument is null.
     * @param any The reference type argument to validate as non-null.
     * @throws ArgumentNullException Thrown if <em>any</em> is 'null'.
     */
    public static void ThrowIfNull(Object any)
            throws ArgumentNullException
    {
        ThrowIfNull(any , null);
    }

    /**
     * Initializes a new instance of the {@link ArgumentNullException} class.
     */
    public ArgumentNullException() {
        super(DEFAULT_MSG_VALUE);
    }

    /**
     * Initializes a new instance of the {@link ArgumentNullException} class with the name of the parameter that causes this exception.
     * @param paramname The name of the parameter that caused the exception.
     */
    public ArgumentNullException(@MaybeNull String paramname)
    {
        super(DEFAULT_MSG_VALUE , paramname);
    }

    /**
     * Initializes an instance of the {@link ArgumentNullException} class with a specified error message and the name of the parameter that causes this exception.
     * @param paramname The name of the parameter that caused the exception.
     * @param message A message that describes the error.
     */
    public ArgumentNullException(@MaybeNull String paramname , @MaybeNull String message)
    {
        super(message , paramname);
    }

    /**
     * Initializes a new instance of the {@link  ArgumentNullException} class with a specified
     * error message and the exception that is the cause of this exception.
     * @param message The error message that explains the reason for this exception.
     * @param innerException The exception that is the cause of the current exception, or a null reference
     * (Nothing in Visual Basic) if no inner exception is specified.
     */
    public ArgumentNullException(@MaybeNull String message, @MaybeNull Exception innerException)
    {
        super(message , innerException);
    }
}
