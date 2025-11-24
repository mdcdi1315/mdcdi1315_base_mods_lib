package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

/**
 * The exception that is thrown when the library itself could not be initialized. <br />
 * This represents a critical error and should not be caught or circumvented somehow. <br />
 * After this is thrown, the Minecraft instance must be guaranteed to fail.
 * @since 1.0.11
 */
public final class CriticalLibraryInitializationException
    extends BaseModsLibraryException
{
    private final Exception exception;

    /**
     * Creates a new instance of the {@link CriticalLibraryInitializationException} class with the specified exception that is the cause of this exception to be thrown. <br />
     * If the argument itself is {@code null}, an exception of this class is thrown back. Critical to critical!
     * @param exception The exception object that caused this exception to be thrown.
     * @throws CriticalLibraryInitializationException {@code exception} is {@code null}.
     */
    public CriticalLibraryInitializationException(Exception exception)
            throws CriticalLibraryInitializationException
    {
        if (exception == null) {
            throw new CriticalLibraryInitializationException(new ArgumentNullException("exception" , "Cannot initialize the underlying exception with null."));
        }
        this.exception = exception;
    }

    /**
     * Gets the {@link Exception} instance that is the cause of this exception object.
     * @return The {@link Exception} that is the cause of the current exception object.
     */
    public Exception getException() { return exception; }

    private static void AppendTabCount(short tabs , StringBuilder builder) {
        for (short tc = 0; tc < tabs; tc++) { builder.append('\t'); }
    }

    private static String BuildExceptionStackTraceDetails(Throwable exception, short tab_count)
    {
        StringBuilder sb = new StringBuilder(1024);
        Throwable[] ts;
        Throwable current = exception;
        while (current != null && tab_count < 32767)
        {
            AppendTabCount(tab_count, sb);
            sb.append("--> Reporting exception of type ")
                    .append(current.getClass().getName())
                    .append(":\n");
            for (StackTraceElement ste : current.getStackTrace())
            {
                AppendTabCount(tab_count, sb);
                sb.append("At: ")
                    .append(ste.toString())
                    .append('\n');
            }
            ts = current.getSuppressed();
            if (ts.length > 0)
            {
                AppendTabCount(tab_count, sb);
                sb.append("Suppressed exceptions (Found ");
                sb.append(ts.length);
                sb.append(" suppressed exceptions):\n");
                for (Throwable supp : current.getSuppressed())
                {
                    AppendTabCount(tab_count, sb);
                    sb.append("Suppressed: ");
                    sb.append(BuildExceptionStackTraceDetails(supp, tab_count)).append('\n');
                }
            }
            AppendTabCount(tab_count, sb);
            sb.append("--> Done reporting exception of type ").append(current.getClass().getName()).append('\n');
            current = current.getCause();
            if (current != null) {
                AppendTabCount(tab_count, sb);
                sb.append("Caused by: \n");
            }
            tab_count++;
        }
        sb.append('\n');
        return sb.toString();
    }

    /**
     * Gets the message for this exception. This builds a VERY detailed string with the error that was occurred.
     * @return The detailed message.
     */
    @Override
    public String getMessage()
    {
        return String.format("""

=============================================================================================
An unexpected exception occurred.
Library cannot continue initialization. Please report this error log to mdcdi1315.
=============================================================================================
All the known details of the thrown exception are:
Exception class name: %s
Exception hash code: %d
Exception is a .NET Layer exception: %s
Exception has suppressed exceptions: %s
Exception message: %s
=============================================================================================
A detailed stack trace of the exception that was failed, as well as it's suppressed exceptions as well:
%s
""",
                exception.getClass().getName(),
                exception.hashCode(),
                exception instanceof com.github.mdcdi1315.DotNetLayer.System.Exception ? "Yes" : "No",
                exception.getSuppressed().length > 0 ? "Yes" : "No",
                exception.getMessage(),
                BuildExceptionStackTraceDetails(exception, (short) 1)
        );
    }
}
