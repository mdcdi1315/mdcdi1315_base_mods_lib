package com.github.mdcdi1315.basemodslib.config;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Exception class thrown when the configuration file could not be saved. <br />
 * This is typically accompanied by an exception object caught before this exception is thrown.
 */
public final class ConfigSaveException
        extends ConfigException
{
    /**
     * Creates a new instance of the {@link ConfigSaveException} class with the specified
     * error message that describes why this exception object was created.
     * @param s The error message that explains why this exception object was created.
     */
    public ConfigSaveException(@MaybeNull String s) {
        this(s , null);
    }

    /**
     * Creates a new instance of the {@link ConfigSaveException} class with the specified
     * exception object that is the cause of this exception object to be created. <br />
     * The error message is set to a library-provided message.
     * @param ex The inner exception that is the cause of this exception object to be created.
     */
    public ConfigSaveException(@MaybeNull Exception ex) {
        this("Configuration file could not be saved.", ex);
    }

    /**
     * Creates a new instance of the {@link ConfigSaveException} class with the specified
     * error message that describes why this exception object was created, and with the
     * specified exception object that is the cause of this exception object to be created.
     * @param s The error message that explains why this exception object was created.
     * @param ex The inner exception that is the cause of this exception object to be created.
     */
    public ConfigSaveException(@MaybeNull String s, @MaybeNull Exception ex) { super(s, ex); }
}