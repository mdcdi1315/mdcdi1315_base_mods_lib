package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * The exception that is thrown when a feature does not run on a particular platform.
 */
public class PlatformNotSupportedException
        extends NotSupportedException
{
    /**
     * Initializes a new instance of the {@link PlatformNotSupportedException} class with default properties.
     */
    public PlatformNotSupportedException() {
        super("");
    }

    /**
     * Initializes a new instance of the {@link PlatformNotSupportedException} class with a specified error message.
     * @param message The text message that explains the reason for the exception.
     */
    public PlatformNotSupportedException(@MaybeNull String message) {
        super(message);
    }

    /**
     * Initializes a new instance of the {@link PlatformNotSupportedException} class with a specified error message and a reference to the inner exception that is the cause of this exception.
     * @param message The error message that explains the reason for the exception.
     * @param inner The exception that is the cause of the current exception.
     *             If the {@code inner} parameter is not {@code null}, the current exception is raised in a {@code catch} block that handles the inner exception.
     */
    public PlatformNotSupportedException(@MaybeNull String message, @MaybeNull Exception inner) { super(message, inner); }
}
