package com.github.mdcdi1315.basemodslib.mods.reporting;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

/**
 * Provides a way to query a message's severity level.
 * @param severity_key The severity level of the message.
 * @apiNote There are provided common severity-level constants that you can use
 * for specifying the severity of your message. You can specify a custom severity
 * as well, but that should be sparingly used, and you would need to specify
 * a singleton value for your severity value, because all comparisons are done
 * in a singleton basis.
 */
@SuppressWarnings("NullableProblems")
public record MessageSeverity(@DisallowNull String severity_key)
{
    /**
     * Initializes a new instance of the {@link MessageSeverity} class.
     * @param severity_key A coded value indicating the severity of the message.
     */
    public MessageSeverity
    {
        ArgumentNullException.ThrowIfNull(severity_key, "severity_key");
    }

    /**
     * A message marked with this severity is an error message.
     */
    public static final MessageSeverity ERROR = new MessageSeverity("error");

    /**
     * A message marked with this severity is a warning message.
     */
    public static final MessageSeverity WARNING = new MessageSeverity("warning");

    /**
     * A message marked with this severity is an informational message.
     */
    public static final MessageSeverity INFO = new MessageSeverity("info");

    /**
     * A message marked with this severity is a recommendation message.
     */
    public static final MessageSeverity RECOMMENDATION = new MessageSeverity("recommendation");

    @Override
    public int hashCode() { return this.severity_key.hashCode(); }

    @NotNull
    @Override
    public String toString() { return StringUtils.Concat("Message Severity: ", this.severity_key); }

    @Override
    @SuppressWarnings("DeconstructionCanBeUsed")
    public boolean equals(Object obj) { return obj instanceof MessageSeverity s && this.severity_key.equals(s.severity_key); }
}
