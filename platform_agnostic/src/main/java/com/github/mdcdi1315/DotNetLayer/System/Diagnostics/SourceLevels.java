package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

import com.github.mdcdi1315.DotNetLayer.System.Flags;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

/**
 * Specifies the levels of trace messages filtered by the source switch and event type filter. <br /> <br />
 *
 * This enumeration supports a bitwise combination of its member values.
 */
@Flags
public enum SourceLevels
{
    /**
     * Allows all events through.
     */
    All(-1),
    /**
     * Does not allow any events through.
     */
    Off(0),
    /**
     * Allows only {@link #Critical} events through.
     */
    Critical(1),
    /**
     * Allows {@link #Critical} and {@link #Error} events through.
     */
    Error(3),
    /**
     * Allows {@link #Critical}, {@link #Error}, and {@link #Warning} events through.
     */
    Warning(7),
    /**
     * Allows {@link #Critical}, {@link #Error}, {@link #Warning}, and {@link #Information} events through.
     */
    Information(15),
    /**
     * Allows {@link #Critical}, {@link #Error}, {@link #Warning}, {@link #Information}, and {@link #Verbose} events through.
     */
    Verbose(31),
    /**
     * Allows the Stop, Start, Suspend, Transfer, and Resume events through.
     */
    ActivityTracing(65280);

    public final int value__; // This is the name exposed by .NET, but never accessed by C#

    SourceLevels(int value) {
        value__ = value;
    }

    public static SourceLevels[] FromPackedValue(int value)
    {
        List<SourceLevels> source_levels = new List<>(10);
        int v;
        for (SourceLevels level : values())
        {
            v = level.value__;
            if ((value & v) == v) {
                source_levels.Add(level);
            }
        }
        return source_levels.ToArray();
    }

    public static int ToPackedValue(SourceLevels... levels)
    {
        ArgumentNullException.ThrowIfNull(levels, "levels");
        int value = 0;
        for (SourceLevels level : levels) {
            value |= level.value__;
        }
        return value;
    }
}
