package com.github.mdcdi1315.basemodslib.config;

import com.github.mdcdi1315.DotNetLayer.System.ApplicationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Exception class thrown when the configuration file could not be saved. <br />
 * This is typically accompanied by an exception object caught before this exception is thrown.
 */
public final class ConfigSaveException
        extends ApplicationException
{
    private final Exception cause;

    public ConfigSaveException(@MaybeNull Exception ex) {
        this("Configuration file could not be saved.", ex);
    }

    public ConfigSaveException(@MaybeNull String s) {
        this(s , null);
    }

    public ConfigSaveException(@MaybeNull String s, @MaybeNull Exception ex) {
        super(s);
        cause = ex;
    }

    @MaybeNull
    public Exception GetCause() {
        return cause;
    }

    @Override
    public String getMessage() {
        if (cause != null) {
            return String.format("%s\nException Data: %s", super.getMessage(), cause);
        } else {
            return super.getMessage();
        }
    }
}