package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DoesNotReturn;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

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
     *  Creates an empty {@link Exception} object.
     */
    public Exception()
    {
        super("System.Exception" , null , true , true);
    }

    /**
     * Creates an {@link Exception} object, with the error message to be provided along the exception data.
     * @param message The error message to be provided along with this exception instance.
     */
    public Exception(String message)
    {
        super(message , null , true , true);
    }

    /**
     * Creates an {@link Exception} object, with the error message to be provided along the exception data,
     * and the original {@link Exception} causing this exception to be thrown.
     * @param message The error message to be provided along with this exception instance.
     * @param innerException The inner {@link Exception} object causing this exception to be thrown.
     */
    public Exception(String message , Exception innerException)
    {
        super(message , innerException , true , true);
    }

    @Override
    @MaybeNull
    public final synchronized Throwable getCause()
    {
        return super.getCause();
    }

    @DoesNotReturn
    public final synchronized Throwable initCause(Throwable t)
    {
        throw new InvalidOperationException("Not allowed to set the cause on a .NET-translated exception.");
    }

    @MaybeNull
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
    @MaybeNull
    public synchronized Exception GetBaseException()
    {
        Throwable c = getCause();
        if (c instanceof Exception e)
        {
            return e;
        }
        return null;
    }

    // Helper for removing StackTraceElements that their methods or declaring classes are decorated with StackTraceHidden annotation.
    private static boolean IsEligibleForRemoving(StackTraceElement e)
    {
        Method cm = null;
        try {
            cm = Class.forName(e.getClassName()).getMethod(e.getMethodName());
        } catch (java.lang.Exception ex) {}
        if (cm == null) {
            return false;
        } else if (cm.getAnnotation(StackTraceHidden.class) != null) {
            return true;
        } else {
            return cm.getDeclaringClass().getAnnotation(StackTraceHidden.class) != null;
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            s.print(getClass().getName());
            s.print(':');
            s.println(getMessage());
            var suppressed = getSuppressed();
            if (suppressed.length > 0) {
                s.print("Suppressed: ");
                for (var p : suppressed) {
                    s.println(p);
                }
                s.println();
            }
            var t = getCause();
            if (t != null) {
                s.print("Caused by: ");
                s.println(t);
            }
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            s.print(getClass().getName());
            s.print(':');
            s.println(getMessage());
            var suppressed = getSuppressed();
            if (suppressed.length > 0) {
                s.print("Suppressed: ");
                for (var p : suppressed) {
                    s.println(p);
                }
                s.println();
            }
            var t = getCause();
            if (t != null) {
                s.print("Caused by: ");
                s.println(t);
            }
        }
    }

    @Override
    public StackTraceElement[] getStackTrace()
    {
        StackTraceElement[] original = super.getStackTrace();
        List<StackTraceElement> elements = new List<>(original.length);
        for (StackTraceElement e : original) {
            if (IsEligibleForRemoving(e)) { continue; }
            elements.Add(e);
        }
        StackTraceElement[] ef = new StackTraceElement[elements.getCount()];
        elements.CopyTo(ef , 0);
        return ef;
    }


}
