package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

/**
 * Represents one or more errors that occur during application execution.
 */
public class AggregateException
        extends Exception
{
    private static final String DEFAULT_MSG = "One or more errors occurred during application execution.";
    private List<Exception> exceptionList;
    /**
     * Initializes a new instance of the {@link AggregateException} class with a system-supplied message that describes the error.
     */
    public AggregateException()
    {
        super(DEFAULT_MSG);
        exceptionList = new List<>();
    }

    /**
     * Initializes a new instance of the {@link AggregateException} class with references to the inner exceptions that are the cause of this exception.
     * @param innerExceptions The exceptions that are the cause of the current exception.
     * @throws ArgumentException An element of innerExceptions is null.
     * @throws ArgumentNullException The innerExceptions argument is null.
     */
    public AggregateException(IEnumerable<Exception> innerExceptions)
            throws ArgumentException
    {
        super(DEFAULT_MSG);
        exceptionList = new List<>(innerExceptions);
    }

    /**
     *  Initializes a new instance of the {@link AggregateException} class with references to the inner exceptions that are the cause of this exception.
     * @param innerExceptions The exceptions that are the cause of the current exception.
     * @throws ArgumentException An element of innerExceptions is null.
     * @throws ArgumentNullException The innerExceptions argument is null.
     */
    public AggregateException(Exception[] innerExceptions)
    {
        super(DEFAULT_MSG);
        ArgumentNullException.ThrowIfNull(innerExceptions , "innerExceptions");
        exceptionList = new List<>(innerExceptions.length);
        for (var e : innerExceptions)
        {
            exceptionList.Add(e);
        }
    }

    /**
     * Initializes a new instance of the {@link AggregateException} class with a specified message that describes the error.
     * @param message The message that describes the exception. The caller of this constructor is required to ensure that this string has been localized for the current system culture.
     */
    public AggregateException(String message)
    {
        super(message);
        exceptionList = new List<>();
    }

    /**
     * Initializes a new instance of the {@link AggregateException} class with a specified error message and references to the inner exceptions that are the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerExceptions The exceptions that are the cause of the current exception.
     * @throws ArgumentException An element of innerExceptions is null.
     * @throws ArgumentNullException The innerExceptions argument is null.
     */
    public AggregateException(String message, IEnumerable<Exception> innerExceptions)
    {
        super(message);
        exceptionList = new List<>(innerExceptions);
    }

    /**
     * Initializes a new instance of the {@link AggregateException} class with a specified
     * error message and a reference to the inner exception that is the cause of this
     * exception.
     * @param message The message that describes the exception. The caller of this constructor is required
     * to ensure that this string has been localized for the current system culture.
     * @param innerException The exception that is the cause of the current exception. If the innerException
     * parameter is not null, the current exception is raised in a catch block that
     * handles the inner exception.
     * @throws ArgumentNullException The innerException argument is null.
     */
    public AggregateException(String message, Exception innerException)
        throws ArgumentNullException
    {
        super(message);
        ArgumentNullException.ThrowIfNull(innerException , "innerException");
        exceptionList = new List<>();
        exceptionList.Add(innerException);
    }

    /**
     * Initializes a new instance of the System.AggregateException class with a specified
     * error message and references to the inner exceptions that are the cause of this
     * exception.
     * @param message The error message that explains the reason for the exception.
     * @param innerExceptions The exceptions that are the cause of the current exception.
     * @throws ArgumentException An element of innerExceptions is null.
     * @throws ArgumentNullException The innerExceptions argument is null.
     */
    public AggregateException(String message, Exception[] innerExceptions)
    {
        super(message);
        ArgumentNullException.ThrowIfNull(innerExceptions , "innerExceptions");
        exceptionList = new List<>(innerExceptions.length);
        for (var e : innerExceptions)
        {
            exceptionList.Add(e);
        }
    }


    /**
     * Gets a read-only collection of the System.Exception instances that caused the current exception.
     * @return A read-only collection of the System.Exception instances that caused the current exception.
     */
    public ICollection<Exception> GetInnerExceptions()
    {
        return exceptionList;
    }

    /**
     * Flattens an {@link AggregateException} instances into a single, new instance.
     * @return A new, flattened {@link AggregateException}.
     */
    public AggregateException Flatten()
    {
        // Initialize a collection to contain the flattened exceptions.
        List<Exception> flattenedExceptions = new List<Exception>();

        // Create a list to remember all aggregates to be flattened, this will be accessed like a FIFO queue
        var exceptionsToFlatten = new List<AggregateException>();
        exceptionsToFlatten.Add(this);
        int nDequeueIndex = 0;

        // Continue removing and recursively flattening exceptions, until there are no more.
        while (exceptionsToFlatten.getCount() > nDequeueIndex)
        {
            // dequeue one from exceptionsToFlatten
            IList<Exception> currentInnerExceptions = exceptionsToFlatten.getItem(nDequeueIndex++).exceptionList;

            for (int i = 0; i < currentInnerExceptions.getCount(); i++)
            {
                Exception currentInnerException = currentInnerExceptions.getItem(i);

                if (currentInnerException == null) { continue; }

                // If this exception is an aggregate, keep it around for later.  Otherwise,
                // simply add it to the list of flattened exceptions to be returned.
                if (currentInnerException instanceof AggregateException currentInnerAsAggregate) {
                    exceptionsToFlatten.Add(currentInnerAsAggregate);
                } else {
                    flattenedExceptions.Add(currentInnerException);
                }
            }
        }

        return new AggregateException(super.getMessage(), flattenedExceptions.ToArray());
    }

    /**
     * Returns the {@link AggregateException} that is the root cause of this exception.
     * @return The {@link AggregateException} that is the root cause of this exception.
     */
    @Override
    public Exception GetBaseException()
    {
        if (exceptionList.getCount() > 0) {
            return exceptionList.getItem(0);
        } else {
            return null;
        }
    }

    /**
     * Invokes a handler on each {@link Exception} contained by this {@link AggregateException}.
     * @param predicate The predicate to execute for each exception. The predicate accepts as an argument
     * the {@link Exception} to be processed and returns a {@link Boolean} to indicate whether
     * the exception was handled.
     * @throws ArgumentNullException The predicate argument is null.
     * @throws AggregateException An exception contained by this {@link AggregateException} was not handled.
     */
    public void Handle(Func2<Exception, Boolean> predicate)
    {
        ArgumentNullException.ThrowIfNull(predicate);

        List<Exception> unhandledExceptions = null;
        for (int i = 0; i < exceptionList.getCount(); i++)
        {
            // If the exception was not handled, lazily allocate a list of unhandled
            // exceptions (to be rethrown later) and add it.
            if (!predicate.function(exceptionList.getItem(i)))
            {
                unhandledExceptions = new List<Exception>();
                unhandledExceptions.Add(exceptionList.getItem(i));
            }
        }

        // If there are unhandled exceptions remaining, throw them.
        if (unhandledExceptions != null)
        {
            throw new AggregateException(getMessage(), unhandledExceptions.ToArray());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(4096);
        sb.append(super.toString());

        Exception item;
        for (int i = 0; i < exceptionList.getCount(); i++)
        {
            item = exceptionList.getItem(i);
            if (item == getInnerException())
            {
                // Already logged in super.toString()
                continue;
            }
            sb.append('\n');
            sb.append(InnerExceptionPrefix);
            sb.append(String.format("Inner Exception #%d:" , i));
            sb.append(item.toString());
            sb.append("<---");
            sb.append('\n');
        }

        return sb.toString();
    }
}