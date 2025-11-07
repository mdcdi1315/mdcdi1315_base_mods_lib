package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

/**
 * Provides a simple on/off switch that controls debugging and tracing output.
 */
public class BooleanSwitch
    extends Switch
{
    /**
     * Initializes a new instance of the {@link BooleanSwitch} class with the specified display name and description.
     * @param displayName The name to display on a user interface.
     * @param description The description of the switch.
     * @see BooleanSwitch
     * @see Switch
     */
    public BooleanSwitch(String displayName, String description) {
        super(displayName, description);
    }

    /**
     * Initializes a new instance of the {@link BooleanSwitch} class with the specified display name, description, and default switch value.
     * @param displayName The name to display on a user interface.
     * @param description The description of the switch.
     * @param defaultSwitchValue The default value of the switch.
     */
    public BooleanSwitch(String displayName, String description, String defaultSwitchValue) {
        super(displayName, description, defaultSwitchValue);
    }

    /**
     * Gets a value indicating whether the switch is enabled or disabled.
     * @return {@code true} if the switch is enabled; otherwise, {@code false}. The default is {@code false}.
     */
    public boolean GetEnabled() {
        return GetSwitchSetting() != 0;
    }

    /**
     * Sets a value indicating whether the switch is enabled or disabled.
     * @param value The value.
     */
    public void SetEnabled(boolean value) {
        SetSwitchSetting(value ? 1 : 0);
    }

    @Override
    protected void OnValueChanged() {
        try {
            SetSwitchSetting(Boolean.parseBoolean(GetValue()) ? 1 : 0);
        } catch (Exception e) {
            super.OnValueChanged();
        }
    }
}
