package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.SystemException;

/**
 * The exception that is thrown when the key specified for accessing an element
 * in a collection does not match any key in the collection.
 */
public class KeyNotFoundException
       extends SystemException
{
    /**
     * Initializes a new instance of the {@link KeyNotFoundException} class using default property values.
     */
    public KeyNotFoundException()
    {
        super();
    }

    /**
     * Initializes a new instance of the {@link KeyNotFoundException} class with the specified error message.
     * @param message The message that describes the error.
     */
    public KeyNotFoundException(String message)
    {
        super(message);
    }

    /**
     * Initializes a new instance of the {@link KeyNotFoundException}
     * class with the specified error message and a reference to the inner exception
     * that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception. If the innerException
     * parameter is not null, the current exception is raised in a catch block that
     * handles the inner exception.
     */
    public KeyNotFoundException(String message, Exception innerException)
    {
        super(message , innerException);
    }
}