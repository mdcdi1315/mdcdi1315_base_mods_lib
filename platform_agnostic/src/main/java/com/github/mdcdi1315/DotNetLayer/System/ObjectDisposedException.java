package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

public class ObjectDisposedException
    extends InvalidOperationException
{
    private final String object_name;

    /**
     * Throws an {@link ObjectDisposedException} if the specified condition is true.
     * @param condition The condition to evaluate.
     * @param instance The object whose type's full name should be included in any resulting {@link ObjectDisposedException}.
     * @exception ObjectDisposedException The {@code condition} is {@code true}.
     */
    public static void ThrowIf(boolean condition, Object instance)
        throws ObjectDisposedException
    {
        if (condition) {
            throw new ObjectDisposedException(instance.getClass().getName());
        }
    }

    /**
     * Initializes a new instance of the {@link ObjectDisposedException} class with a string containing the name of the disposed object.
     * @param object_name A string containing the name of the disposed object.
     */
    public ObjectDisposedException(@MaybeNull String object_name) {
        super("The specified object is disposed.");
        this.object_name = object_name;
    }

    /**
     * Initializes a new instance of the {@link ObjectDisposedException} class with a specified error message and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception.
     *                       If {@code innerException} is not {@code null}, the current exception is raised in a {@code catch} block that handles the inner exception.
     */
    public ObjectDisposedException(@MaybeNull String message, @MaybeNull Exception innerException) {
        super(message , innerException);
        object_name = null;
    }

    /**
     * Initializes a new instance of the {@link ObjectDisposedException} class with the specified object name and message.
     * @param objectName The name of the disposed object.
     * @param message The error message that explains the reason for the exception.
     */
    public ObjectDisposedException(@MaybeNull String objectName, @MaybeNull String message) {
        super(message);
        object_name = objectName;
    }

    /**
     * Gets the name of the disposed object. <br /> <br />
     * Remarks: <br />
     * If the current property is not {@code null} or {@link String#isEmpty()}, the value of this property is included in the string returned by the Message property.
     * @return A string containing the name of the disposed object.
     */
    @MaybeNull
    public String GetObjectName() {
        return object_name;
    }

    @Override
    public String getMessage() {
        String m = super.getMessage();
        if (object_name != null && !object_name.isEmpty()) {
            return String.format("%s\nObject Name: %s" , m , object_name);
        } else {
            return m;
        }
    }
}
