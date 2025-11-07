package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized.StringDictionary;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Provides an abstract base class to create new debugging and tracing switches.
 */
public abstract class Switch
{
    @AllowNull
    private final String _description;
    private final String _displayName;
    private int _switchSetting;
    private volatile boolean _initialized;
    private boolean _initializing;
    @AllowNull
    private volatile String _switchValueString = "";
    private final String _defaultValue;
    private AtomicReference<Object> _initializedLock;

    @AllowNull
    private StringDictionary _attributes;

    private Object GetInitializedLock()
    {
        if (_initializedLock.get() == null) {
            _initializedLock.set(new Object());
        }

        return _initializedLock.get();
    }

    /**
     * Initializes a new instance of the {@link Switch} class.
     * @param displayName The name of the switch.
     * @param description The description for the switch.
     */
    protected Switch(String displayName, @MaybeNull String description) {
        this(displayName , description , "");
    }

    /**
     * Initializes a new instance of the {@link Switch} class, specifying the display name, description, and default value for the switch.
     * @param displayName The name of the switch.
     * @param description The description for the switch.
     * @param defaultSwitchValue The default value for the switch.
     */
    protected Switch(String displayName, @MaybeNull String description, String defaultSwitchValue)
    {
        _initializedLock = new AtomicReference<>();
        _displayName = displayName == null ? "" : displayName;
        _description = description;
        _defaultValue = defaultSwitchValue;
    }

    /**
     * Gets the current setting for this switch.
     * @return The current setting for this switch. The default is zero.
     */
    protected int GetSwitchSetting() {
        if (!_initialized)
        {
            if (InitializeWithStatus())
            {
                OnSwitchSettingChanged();
            }
        }
        return _switchSetting;
    }

    /**
     * Sets the current setting for this switch.
     * @param value The new setting value to set for this switch.
     */
    protected void SetSwitchSetting(int value)
    {
        boolean didUpdate = false;
        synchronized (GetInitializedLock())
        {
            _initialized = true;
            if (_switchSetting != value)
            {
                _switchSetting = value;
                didUpdate = true;
            }
        }

        if (didUpdate) {
            OnSwitchSettingChanged();
        }
    }

    /**
     * Gets the value of the switch.
     * @return A string representing the value of the switch.
     */
    public String GetValue() {
        Initialize();
        return _switchValueString;
    }

    /**
     * Sets the value of the switch.
     * @param value The new value of the switch.
     */
    public void SetValue(String value) {
        Initialize();
        _switchValueString = value;
        OnValueChanged();
    }

    /**
     * Gets the default value assigned in the constructor.
     * @return The default value.
     */
    @NotNull
    public String GetDefaultValue() {
        return _defaultValue;
    }

    private void Initialize()
    {
        InitializeWithStatus();
    }

    private void OnInitializing() {} // Empty method currently.

    private boolean InitializeWithStatus()
    {
        if (!_initialized)
        {
            synchronized (GetInitializedLock())
            {
                if (_initialized || _initializing) {
                    return false;
                }

                // This method is re-entrant during initialization, since calls to OnValueChanged() in subclasses could end up having InitializeWithStatus()
                // called again, we don't want to get caught in an infinite loop.
                _initializing = true;

                _switchValueString = null;

                try {
                    OnInitializing();
                } catch (Exception e) {
                    _initialized = false;
                    _initializing = false;
                    throw e;
                }

                if (_switchValueString == null)
                {
                    _switchValueString = _defaultValue;
                    OnValueChanged();
                }

                _initialized = true;
                _initializing = false;
            }
        }

        return true;
    }

    /**
     * Gets the custom switch attributes defined in the application configuration file.
     * @return A {@link StringDictionary} containing the case-insensitive custom attributes for the trace switch.
     */
    @NotNull
    public StringDictionary GetAttributes() {
        Initialize();
        return _attributes == null ? _attributes = new StringDictionary() : _attributes;
    }

    /**
     * Gets the custom attributes supported by the switch.
     * @return A string array that contains the names of the custom attributes supported by the switch, or {@code null} if no custom attributes are supported.
     */
    @MaybeNull
    protected String[] GetSupportedAttributes() { return null; }

    /**
     * Gets a description of the switch.
     * @return The description of the switch. The default value is an empty string ("").
     */
    @NotNull
    public String GetDescription() {
        return _description == null ? "" : _description;
    }

    /**
     * Gets a name used to identify the switch.
     * @return The name used to identify the switch. The default value is an empty string ("").
     */
    @NotNull
    public String GetDisplayName() {
        return _displayName;
    }

    /**
     * Invoked when the {@link #SetSwitchSetting(int)} method is invoked.
     */
    protected void OnSwitchSettingChanged() { }

    /**
     * Invoked when the {@link #SetValue(String)} method is invoked.
     */
    protected void OnValueChanged()
    {
        SetSwitchSetting(Integer.parseInt(GetValue()));
    }

    /**
     * Refreshes the trace configuration data.
     */
    public void Refresh()
    {
        synchronized (GetInitializedLock())
        {
            _initialized = false;
            Initialize();
        }
    }

}
