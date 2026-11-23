package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

import com.mojang.serialization.DataResult;

/**
 * Exception class that is thrown when a {@link DataResult} from a serialization primitive (encoder or decoder)
 * has given an error instance.
 */
public class ErrorDataResultException
    extends SerializationException
{
    private final DataResult.Error<?> error;

    /**
     * Initializes a new instance of the {@link ErrorDataResultException} class.
     * @param error The serialization error.
     * @throws ArgumentNullException {@code error} is {@code null}.
     */
    public ErrorDataResultException(DataResult.Error<?> error)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(error, "error");
        super(null);
        this.error = error;
    }

    /**
     * Gets the serialization error instance that caused this exception to be thrown.
     * @return The serialization error instance.
     */
    @Pure
    @NotNull
    public final DataResult.Error<?> GetError() { return error; }

    @Override
    public String getMessage()
    {
        return StringUtils.Concat(
                "An error result occurred during the de/serialization process.\n",
                "Error data: ",
                error.message()
        );
    }
}
