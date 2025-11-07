package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

public class SourceSwitch
    extends Switch
{
    public SourceSwitch(String displayName, String description) {
        super(displayName, description);
    }

    public SourceSwitch(String displayName, String description, String defaultSwitchValue) {
        super(displayName, description, defaultSwitchValue);
    }

    public SourceLevels[] GetLevel() {
        return SourceLevels.FromPackedValue(GetSwitchSetting());
    }

    public void SetLevel(SourceLevels... value) {
        ArgumentNullException.ThrowIfNull(value, "value");
        SetSwitchSetting(SourceLevels.ToPackedValue(value));
    }

    public boolean ShouldTrace(TraceEventType eventType) {
        return (GetSwitchSetting() & eventType.value__) != 0;
    }

    protected void OnValueChanged() {
        SetSwitchSetting(SourceLevels.valueOf(SourceLevels.class, GetValue()).value__);
    }
}
