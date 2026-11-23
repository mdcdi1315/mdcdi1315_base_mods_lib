package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

import java.io.IOException;

/**
 * Exception indicating that there was a fatal I/O exception during an asynchronous de/encoding task. <br />
 * This exception type can only be thrown by asynchronous method variants provided in the de/encoder pipelines.
 */
public class AsyncIOErrorException
        extends SerializationException
{
    private final IOException exception;

    /**
     * Initializes a new instance of the {@link AsyncIOErrorException} class.
     * @param exception The unchecked {@link IOException} exception that was occurred.
     * @throws ArgumentNullException {@code exception} is {@code null}.
     */
    public AsyncIOErrorException(IOException exception)
            throws ArgumentNullException
    {
        super();
        ArgumentNullException.ThrowIfNull(exception, "exception");
        this.exception = exception;
        addSuppressed(exception);
    }

    /**
     * Gets the original exception that was thrown inside the asynchronous code execution block.
     * @return The I/O exception that was originally thrown.
     */
    @Pure
    @NotNull
    public final IOException GetIOException() { return exception; }

    @Override
    public String getMessage()
    {
        return StringUtils.Concat(
                "An I/O exception has been occurred during execution.\nOriginal exception message: ",
                exception.getMessage()
        );
    }
}
