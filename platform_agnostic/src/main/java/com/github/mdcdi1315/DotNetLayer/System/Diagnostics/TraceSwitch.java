package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

/**
 * Provides a multilevel switch to control tracing and debug output without recompiling your code.
 */
public class TraceSwitch
    extends Switch
{
    /**
     * Initializes a new instance of the {@link TraceSwitch} class, using the specified display name and description.
     * @param displayName The name to display on a user interface.
     * @param description The description of the switch.
     */
    public TraceSwitch(String displayName, String description) {
        super(displayName, description);
    }

    /**
     * Initializes a new instance of the {@link TraceSwitch} class, using the specified display name, description, and default value for the switch.
     * @param displayName The name to display on a user interface.
     * @param description The description of the switch.
     * @param defaultSwitchValue The default value of the switch.
     */
    public TraceSwitch(String displayName, String description, String defaultSwitchValue) {
        super(displayName, description, defaultSwitchValue);
    }

    /**
     * Gets the trace level that determines the messages the switch allows.
     * @return One of the {@link TraceLevel} values that specifies the level of messages that are allowed by the switch.
     * @throws ArgumentException Level is set to a value that is not one of the {@link TraceLevel} values.
     */
    public TraceLevel GetLevel()
        throws ArgumentException
    {
        return TraceLevel.FromPackedValue(GetSwitchSetting());
    }

    /**
     * Sets the trace level that determines the messages the switch allows.
     * @param value The new level to define.
     * @throws ArgumentException Level is set to a value that is not one of the {@link TraceLevel} values.
     */
    public void SetLevel(TraceLevel value)
        throws ArgumentException
    {
        ArgumentNullException.ThrowIfNull(value, "value");
        if (value.value__ < TraceLevel.Off.value__ || value.value__ > TraceLevel.Verbose.value__) {
            throw new ArgumentException("Invalid tracing level was passed.", "value");
        }
        SetSwitchSetting(value.value__);
        SetValue(value.name());
    }

    /**
     * Gets a value indicating whether the switch allows error-handling messages.
     * @return {@code true} if the {@link #GetLevel()} method is set to {@link TraceLevel#Error}, {@link TraceLevel#Warning}, {@link TraceLevel#Info}, or {@link TraceLevel#Verbose}; otherwise, {@code false}.
     */
    public boolean GetTraceError() {
        return GetSwitchSetting() >= TraceLevel.Error.value__;
    }

    /**
     * Gets a value indicating whether the switch allows warning messages.
     * @return {@code true} if the {@link #GetLevel()} method is set to {@link TraceLevel#Warning}, {@link TraceLevel#Info}, or {@link TraceLevel#Verbose}; otherwise, {@code false}.
     */
    public boolean GetTraceWarning() {
        return GetSwitchSetting() >= TraceLevel.Warning.value__;
    }

    /**
     * Gets a value indicating whether the switch allows informational messages.
     * @return {@code true} if the {@link #GetLevel()} method is set to {@link TraceLevel#Info}, or {@link TraceLevel#Verbose}; otherwise, {@code false}.
     */
    public boolean GetTraceInfo() {
        return GetSwitchSetting() >= TraceLevel.Info.value__;
    }

    /**
     * Gets a value indicating whether the switch allows all messages.
     * @return {@code true} if the {@link #GetLevel()} method is set to {@link TraceLevel#Verbose}; otherwise, {@code false}.
     */
    public boolean GetTraceVerbose() {
        return GetSwitchSetting() >= TraceLevel.Verbose.value__;
    }

    protected void OnSwitchSettingChanged()
    {
        int level = GetSwitchSetting();
        if (level < TraceLevel.Off.value__) {
            // TODO: Implement the below line.
            // Trace.WriteLine(SR.Format(SR.TraceSwitchLevelTooLow, DisplayName));
            SetSwitchSetting(TraceLevel.Off.value__);
        } else if (level > TraceLevel.Verbose.value__) {
            // TODO: Implement the below line.
            // Trace.WriteLine(SR.Format(SR.TraceSwitchLevelTooHigh, DisplayName));
            SetSwitchSetting(TraceLevel.Verbose.value__);
        }
    }
}
