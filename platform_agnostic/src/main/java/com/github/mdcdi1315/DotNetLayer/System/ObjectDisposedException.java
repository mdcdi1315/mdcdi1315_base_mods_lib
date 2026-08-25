package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.*;

/**
 * The exception that is thrown when an operation is performed on a disposed object.
 */
public class ObjectDisposedException
    extends InvalidOperationException
{
    @AllowNull
    private final String object_name;

    /**
     * Throws an {@link ObjectDisposedException} if the specified condition is true.
     * @param condition The condition to evaluate.
     * @param instance The object whose type's full name should be included in any resulting {@link ObjectDisposedException}.
     * @exception ObjectDisposedException The {@code condition} is {@code true}.
     */
    public static void ThrowIf(@DoesNotReturnIf(ParameterValue = true) boolean condition, @DisallowNull Object instance)
        throws ObjectDisposedException
    {
        if (condition) {
            throw new ObjectDisposedException(instance.getClass().getName());
        }
    }

    /**
     * Throws an {@link ObjectDisposedException} if the specified condition is true.
     * @param condition The condition to evaluate.
     * @param instance_class The type whose full name should be included in any resulting {@link ObjectDisposedException}.
     * @throws ObjectDisposedException The {@code condition} is {@code true}.
     */
    public static void ThrowIf(@DoesNotReturnIf(ParameterValue = true) boolean condition, @DisallowNull Class<?> instance_class)
            throws ObjectDisposedException
    {
        if (condition) {
            throw new ObjectDisposedException(instance_class.getName());
        }
    }

    /**
     * Initializes a new instance of the {@link ObjectDisposedException} class with a string containing the name of the disposed object.
     * @param object_name A string containing the name of the disposed object.
     */
    public ObjectDisposedException(@AllowNull String object_name)
    {
        super("The specified object is disposed.");
        this.object_name = object_name;
    }

    /**
     * Initializes a new instance of the {@link ObjectDisposedException} class with a specified error message and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception.
     *                       If {@code innerException} is not {@code null}, the current exception is raised in a {@code catch} block that handles the inner exception.
     */
    public ObjectDisposedException(@AllowNull String message, @AllowNull Exception innerException)
    {
        super(message , innerException);
        object_name = null;
    }

    /**
     * Initializes a new instance of the {@link ObjectDisposedException} class with the specified object name and message.
     * @param objectName The name of the disposed object.
     * @param message The error message that explains the reason for the exception.
     */
    public ObjectDisposedException(@AllowNull String objectName, @AllowNull String message)
    {
        super(message);
        object_name = objectName;
    }

    /**
     * Gets the name of the disposed object. <br /> <br />
     * Remarks: <br />
     * If the underlying value of the current method return value is not {@code null} or {@link String#isEmpty()}, the value of this method is included in the string returned by the {@link #getMessage()} method.
     * @return A string containing the name of the disposed object.
     */
    @NotNull
    public String GetObjectName() { return object_name == null ? StringUtils.Empty : object_name; }

    @Override
    public String getMessage()
    {
        String m = super.getMessage();
        return StringUtils.IsNullOrEmpty(object_name) ? m : m + "\nObject Name: " + object_name;
    }
}
