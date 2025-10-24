package com.github.mdcdi1315.DotNetLayer;

import com.github.mdcdi1315.DotNetLayer.System.ExecutionEngineException;

/**
 * Special class type for porting .NET by-reference parameters.
 * Should be used along with the {@link DotNetByRefParameter} annotation.
 * @param <T> The actual type of the parameter.
 */
public final class ByRefParameter<T>
{
    private static final String ASSERTION_FAILED = "An assertion has been failed.";

    public T Value;

    public ByRefParameter()
    {
        Value = null;
    }

    public ByRefParameter(T initialvalue)
    {
        Value = initialvalue;
    }

    @PrivateImplementationDetail
    public static void AssertEOut(ByRefParameter<?> any)
    {
        if (any == null) {
            throw new AssertionError(ASSERTION_FAILED , new ExecutionEngineException("The specified output by-ref parameter was not properly initialized."));
        }
    }

    @PrivateImplementationDetail
    public static void AssertEIn(ByRefParameter<?> any)
    {
        if (any == null) {
            throw new AssertionError(ASSERTION_FAILED , new ExecutionEngineException("The specified input by-ref parameter was not properly initialized."));
        }
    }

    @PrivateImplementationDetail
    public static void AssertERef(ByRefParameter<?> any)
    {
        if (any == null) {
            throw new AssertionError(ASSERTION_FAILED , new ExecutionEngineException("The specified by-ref parameter was not properly initialized."));
        }
    }
}
