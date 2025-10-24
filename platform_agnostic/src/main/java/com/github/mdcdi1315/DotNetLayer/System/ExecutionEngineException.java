package com.github.mdcdi1315.DotNetLayer.System;

/**
 * The exception that is thrown when there is an internal error in the execution
 * engine of the common language runtime. This class cannot be inherited.
 */
// [Obsolete("ExecutionEngineException previously indicated an unspecified fatal error in the runtime. The runtime no longer raises this exception so this type is obsolete.")]
public final class ExecutionEngineException
        extends SystemException
{
    /**
     * Initializes a new instance of the {@link ExecutionEngineException} class.
     */
    public ExecutionEngineException() {super();}

    /**
     * Initializes a new instance of the {@link ExecutionEngineException} class with a specified error message.
     * @param message The message that describes the error.
     */
    public ExecutionEngineException(String message) { super(message); }

    /**
     * Initializes a new instance of the {@link ExecutionEngineException} class with
     * a specified error message and a reference to the inner exception that is the
     * cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerException The exception that is the cause of the current exception. If the innerException
     * parameter is not a null reference (Nothing in Visual Basic), the current exception
     * is raised in a catch block that handles the inner exception.
     */
    public ExecutionEngineException(String message, Exception innerException) { super(message , innerException); }
}