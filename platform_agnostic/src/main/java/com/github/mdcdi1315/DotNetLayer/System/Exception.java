package com.github.mdcdi1315.DotNetLayer.System;

/**
 * The {@link Exception} class emulates the .NET equivalent of System.Exception class,
 * roughly providing the same services as the Java equivalent would do.
 * 
 * <p>
 * {@link Exception} instances are considered by Java as unchecked exceptions always (Since they derive from {@link RuntimeException}).
 * </p>
 * <p>
 * This is happening to harmonize both platforms. In .NET all the exception classes are always unchecked exceptions.
 * </p>
 * <p>
 *     From this class all the translated .NET Exception classes should be derived from.
 * </p>
 * <p>
 * This class in 99% of the cases MUST BE subclassed.
 * </p>
 */
public class Exception 
    extends RuntimeException 
{
    protected static final String InnerExceptionPrefix = " ---> ";
    /**
     *  Creates an empty {@code Exception} object.
     */
    public Exception()
    {
        super("System.Exception" , null , true , true);
    }

    /**
     * Creates an {@code Exception} object, with the error message to be provided along the exception data. 
     * @param message The error message to be provided along with this exception instance.
     */
    public Exception(String message)
    {
        super(message , null , true , true);
    }

    /**
     * Creates an {@code Exception} object, with the error message to be provided along the exception data,
     * and the original {@code Exception} causing this exception to be thrown.
     * @param message The error message to be provided along with this exception instance.
     * @param innerException The inner {@code Exception} object causing this exception to be thrown.
     */
    public Exception(String message , Exception innerException)
    {
        super(message , innerException , true , true);
    }

    @Override
    public final synchronized Throwable getCause()
    {
        return super.getCause();
    }

    public final synchronized Throwable initCause(Throwable t)
    {
        throw new InvalidOperationException("Not allowed to set the cause on a .NET-translated exception.");
    }

    public synchronized Exception getInnerException()
    {
        Throwable c = getCause();
        if (c == null) {
            return null;
        } else if (c instanceof Exception) {
            return (Exception)c;
        } else {
            throw new ExecutionEngineException("Invalid Exception code path");
        }
    }

    /**
     * Gets the {@link Exception} object that is the root cause of this exception <br />
     * May be null if no root cause exception was determined.
     * @return The {@link Exception} object that is the root cause of this exception instance.
     */
    public synchronized Exception GetBaseException()
    {
        Throwable c = getCause();
        if (c instanceof Exception e)
        {
            return e;
        }
        return null;
    }
}
