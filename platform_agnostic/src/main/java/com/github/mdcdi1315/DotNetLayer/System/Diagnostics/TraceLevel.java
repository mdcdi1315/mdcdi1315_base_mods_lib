package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;

/**
 * Specifies what messages to output for the Debug, Trace and {@link TraceSwitch} classes.
 */
public enum TraceLevel
{
    /**
     * Output no tracing and debugging messages.
     */
    Off(0),
    /**
     * Output error-handling messages.
     */
    Error(1),
    /**
     * Output warnings and error-handling messages.
     */
    Warning(2),
    /**
     * Output informational messages, warnings, and error-handling messages.
     */
    Info(3),
    /**
     * Output all debugging and tracing messages.
     */
    Verbose(4);

    public final int value__;

    TraceLevel(int value) {
        value__ = value;
    }

    public static TraceLevel FromPackedValue(int value)
    {
        for (TraceLevel v : values()) {
            if (v.value__ == value) { return v; }
        }
        throw new ArgumentException(String.format("No enum constant corresponds to value %d" , value), "value");
    }
}
