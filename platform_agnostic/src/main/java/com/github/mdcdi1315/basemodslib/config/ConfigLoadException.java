package com.github.mdcdi1315.basemodslib.config;

import com.github.mdcdi1315.DotNetLayer.System.ApplicationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Exception class thrown when the configuration file cannot be loaded from the specified data store.
 */
public final class ConfigLoadException
        extends ApplicationException
{
    private final Exception cause;

    public ConfigLoadException(@MaybeNull Exception ex) {
        this("Configuration file could not be loaded.", ex);
    }

    public ConfigLoadException(@MaybeNull String s) {
        this(s , null);
    }

    public ConfigLoadException(@MaybeNull String s, @MaybeNull Exception ex) {
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
