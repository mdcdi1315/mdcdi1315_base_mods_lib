package com.github.mdcdi1315.basemodslib.mods.reporting;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

/**
 * Exception thrown when a {@link MessageReporter} instance has been frozen,
 * while attempting to add a new diagnostic message.
 */
@SuppressWarnings("unused")
public final class FrozenMessageReporterException
        extends InvalidOperationException
{
    public FrozenMessageReporterException()
    {
        super("The message reporter has been frozen, and no more diagnostic messages can be provided to it.");
    }
}
