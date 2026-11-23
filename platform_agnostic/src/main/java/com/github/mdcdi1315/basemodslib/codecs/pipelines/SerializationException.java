package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.BaseModsLibraryException;

/**
 * Base exception class thrown when any error in the de/encoding process is aroused.
 */
public class SerializationException
    extends BaseModsLibraryException
{
    /**
     * Initializes a new instance of the {@link SerializationException} class without any additional information on why this exception object was created.
     */
    public SerializationException() { super(); }

    /**
     * Initializes a new instance of the {@link SerializationException} class. <br />
     * The error message parameter provided describes why this exception object was created.
     * @param message An error message describing the faulty condition that was determined.
     */
    public SerializationException(@AllowNull String message) { super(message); }

    /**
     * Initializes a new instance of the {@link SerializationException} class. <br />
     * The error message parameter provided describes why this exception object was created,
     * and the inner exception parameter provides the cause of why this exception object was created.
     * @param message An error message describing the faulty condition that was determined.
     * @param inner The exception causing this exception object to be created.
     */
    public SerializationException(@AllowNull String message, @AllowNull Exception inner) { super(message, inner); }
}
