package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;

/**
 * Identifies the type of event that has caused the trace.
 */
public enum TraceEventType
{
    /**
     * Fatal error or application crash.
     */
    Critical(1),
    /**
     * Recoverable error.
     */
    Error(2),
    /**
     * Noncritical problem.
     */
    Warning(4),
    /**
     * Informational message.
     */
    Information(8),
    /**
     * Debugging trace.
     */
    Verbose(16),
    /**
     * Starting of a logical operation.
     */
    Start(256),
    /**
     * Stopping of a logical operation.
     */
    Stop(512),
    /**
     * Suspension of a logical operation.
     */
    Suspend(1024),
    /**
     * Resumption of a logical operation.
     */
    Resume(2048),
    /**
     * Changing of correlation identity.
     */
    Transfer(4096);

    public final int value__;

    TraceEventType(int value) {
        value__ = value;
    }

    public static TraceEventType FromPackedValue(int value)
    {
        for (TraceEventType v : values()) {
            if (v.value__ == value) { return v; }
        }
        throw new ArgumentException(String.format("No enum constant corresponds to value %d" , value), "value");
    }
}
