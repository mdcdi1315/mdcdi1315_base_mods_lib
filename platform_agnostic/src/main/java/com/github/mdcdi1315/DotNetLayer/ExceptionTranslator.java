package com.github.mdcdi1315.DotNetLayer;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.ArithmeticException;

import java.io.UTFDataFormatException;
import java.util.FormatterClosedException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.CancellationException;

/**
 * Translates {@link Throwable} objects to {@link Exception} objects.
 */
public final class ExceptionTranslator
{
    private ExceptionTranslator() { }

    /**
     * Attempts to translate the specified {@link Throwable} to the .NET Layer-equivalent {@link Exception}. <br />
     * The {@link Exception} inherits the stack trace of the {@link Throwable}.
     * @param e The {@link Throwable} to translate.
     * @return The translated {@link Exception} object, or {@code null} if the translation is not possible.
     */
    public static Exception TranslateException(Throwable e)
    {
        if (e == null) { return null; }
        else if (e instanceof Exception g) { return g; }
        var captured_trace = e.getStackTrace();
        Exception ex = switch (e)
        {
            case OutOfMemoryError g -> new OutOfMemoryException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case NullPointerException g -> new NullReferenceException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case CancellationException g -> new TimeoutException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case NumberFormatException g -> new FormatException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case IllegalArgumentException g -> new ArgumentException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case FormatterClosedException g -> new ObjectDisposedException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case IllegalStateException g -> new InvalidOperationException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case ClassCastException g -> new InvalidCastException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case IllegalAccessError g -> new UnauthorizedAccessException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case IllegalAccessException g -> new UnauthorizedAccessException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case IndexOutOfBoundsException g -> new IndexOutOfRangeException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case CloneNotSupportedException g -> new NotSupportedException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case ClosedChannelException g -> new ObjectDisposedException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case UTFDataFormatException g -> new FormatException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case UnsupportedOperationException g -> new NotSupportedException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case NegativeArraySizeException g -> new IndexOutOfRangeException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case java.lang.ArithmeticException g -> new ArithmeticException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case ArrayStoreException g -> new ArrayTypeMismatchException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            case VirtualMachineError g -> new ExecutionEngineException(
                    g.getMessage(),
                    TranslateException(g.getCause())
            );
            default -> null;
        };
        if (ex != null) { ex.setStackTrace(captured_trace); }
        return ex;
    }
}
