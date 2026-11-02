package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.Dictionary;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Provides members for setting and retrieving data about an application's context. <br />
 *
 * The {@link AppContext} class enables library writers to provide a uniform opt-out mechanism for new functionality for their users. <br />
 * It establishes a loosely coupled contract between components in order to communicate an opt-out request. <br />
 * This capability is typically important when a change is made to existing functionality. <br />
 * Conversely, there is already an implicit opt-in for new functionality.
 *
 * <h2>AppContext for library developers</h2>
 * Libraries use the AppContext class to define and expose compatibility switches,
 * while library users can set those switches to affect the library behavior.
 * By default, libraries provide the new functionality, and they only alter it (that is, they provide the previous functionality) if the switch is set.
 * This allows libraries to provide new behavior for an existing API while continuing to support callers who depend on the previous behavior.
 *
 * <h3>Define the switch name</h3>
 * The most common way to allow consumers of your library to opt out of a change of behavior is to define a named switch.
 * Its value element is a name/value pair that consists of the name of a switch and its Boolean value.
 * By default, the switch is always implicitly {@code false}, which provides the new behavior (and makes the new behavior opt-in by default).
 * Setting the switch to {@code true} enables it, which provides the legacy behavior.
 * Explicitly setting the switch to false also provides the new behavior. <br /> <br />
 *
 * It's beneficial to use a consistent format for switch names, since they're a formal contract exposed by a library.
 * The following are two obvious formats:
 *
 * <ul>
 *     <li><em>Switch.namespace.switchname</em></li>
 *     <li><em>Switch.library.switchname</em></li>
 * </ul>
 *
 * Once you define and document the switch, callers can use it by calling the {@link #SetSwitch(String, boolean)} method programmatically.
 *
 * <h3>Check the setting</h3>
 * You can check if a consumer has declared the value of the switch and act appropriately by calling the {@link #TryGetSwitch(String, ByRefParameter)} method.
 * The method returns {@code true} if the {@code switchName} argument is found, and its isEnabled argument indicates the value of the switch.
 * Otherwise, the method returns {@code false}.
 */
public final class AppContext
{
    private AppContext() {}

    @AllowNull
    private static Dictionary<String, Object> s_dataStore;
    @AllowNull
    private static Dictionary<String, Boolean> s_switches;
    @AllowNull
    private static String s_defaultBaseDirectory;
    @AllowNull
    private static String s_definedTargetFramework;

    private static final ReentrantLock stores_lock = new ReentrantLock();

    /**
     * Gets the file path of the base directory of the application.
     * @return The file path of the base directory of the application.
     */
    @NotNull
    public static String GetBaseDirectory()
    {
        // The value of APP_CONTEXT_BASE_DIRECTORY key has to be a string, and it is not allowed to be any other type.
        // Otherwise, the caller will get invalid cast exception
        var o = GetData("APP_CONTEXT_BASE_DIRECTORY");
        if (o instanceof String s) {
            return s;
        } else if (o != null) {
            throw new ClassCastException("APP_CONTEXT_BASE_DIRECTORY cannot be a " + o.getClass().getSimpleName());
        } else if (s_defaultBaseDirectory == null) {
            s_defaultBaseDirectory = System.getProperty("user.dir");
        }
        return s_defaultBaseDirectory;
    }

    /**
     * Gets the name of the framework version targeted by the current application.
     * @return The name of the framework version targeted by the current application.
     */
    @MaybeNull
    public static String GetTargetFrameworkName()
    {
        if (s_definedTargetFramework == null)
        {
            if (GetData("TargetFrameworkName") instanceof String s) { s_definedTargetFramework = s; }
        }
        return s_definedTargetFramework;
    }

    /**
     * Returns the value of the named data element assigned to the current application domain.
     * @param name The name of the data element.
     * @return The value of {@code name}, if {@code name} identifies a named value; otherwise, {@code null}.
     * @throws ArgumentNullException {@code name} is {@code null}.
     */
    @MaybeNull
    public static Object GetData(String name)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");

        if (s_dataStore == null)
            return null;

        stores_lock.lock();
        try {
            ByRefParameter<Object> data = new ByRefParameter<>();
            s_dataStore.TryGetValue(name, data);
            return data.Value;
        } finally {
            stores_lock.unlock();
        }
    }

    /**
     * Sets the value of the named data element assigned to the current application domain.
     * @param name The name of the data element
     * @param data The value of {@code name}
     * @throws ArgumentNullException {@code name} is {@code null}.
     */
    public static void SetData(String name, @AllowNull Object data)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");

        stores_lock.lock();
        try {
            if (s_dataStore == null)
            {
                // Interlocked.CompareExchange(ref s_dataStore, new Dictionary<string, object?>(), null);
                s_dataStore = new Dictionary<>();
            }
            s_dataStore.setItem(name, data);
        } finally {
            stores_lock.unlock();
        }
    }

    /**
     * Try to get the value of the switch.
     * @param switchName The name of the switch
     * @param isEnabled A variable where to place the value of the switch
     * @return A return value of {@code true} represents that the switch was set and {@code isEnable} contains the value of the switch
     * @throws ArgumentNullException {@code switchName} is {@code null}.
     * @throws ArgumentException {@code switchName} is {@link StringUtils#Empty}.
     */
    public static boolean TryGetSwitch(String switchName, @DotNetByRefParameter(ByRefParameterType.OUT) ByRefParameter<Boolean> isEnabled)
        throws ArgumentNullException, ArgumentException
    {
        ByRefParameter.AssertEOut(isEnabled);
        ArgumentException.ThrowIfNullOrEmpty(switchName, "switchName");

        if (s_switches != null)
        {
            stores_lock.lock();
            try {
                if (s_switches.TryGetValue(switchName, isEnabled))
                    return true;
            } finally {
                stores_lock.unlock();
            }
        }

        var d = GetData(switchName);
        if (d instanceof String value) {
            isEnabled.Value = Boolean.parseBoolean(value);
            return true;
        } else if (d instanceof Boolean b) {
            isEnabled.Value = b;
            return true;
        } else {
            isEnabled.Value = false;
            return false;
        }
    }

    /**
     * Sets the value of a switch.
     * @param switchName The name of the switch.
     * @param isEnabled The value of the switch.
     * @throws ArgumentNullException {@code switchName} is {@code null}.
     * @throws ArgumentException {@code switchName} is {@link StringUtils#Empty}.
     */
    public static void SetSwitch(String switchName, boolean isEnabled)
        throws ArgumentNullException, ArgumentException
    {
        ArgumentException.ThrowIfNullOrEmpty(switchName, "switchName");

        if (s_switches == null)
        {
            // Compatibility switches are rarely used. Initialize the Dictionary lazily
            // Interlocked.CompareExchange(ref s_switches, new Dictionary<string, bool>(), null);
            stores_lock.lock();
            try {
                s_switches = new Dictionary<>();
            } finally {
                stores_lock.unlock();
            }
        }

        stores_lock.lock();
        try {
            s_switches.setItem(switchName, isEnabled);
        } finally {
            stores_lock.unlock();
        }
    }
}
