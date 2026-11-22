package com.github.mdcdi1315.basemodslib.mods.information;

/**
 * Defines the time when the dependency will be loaded. <br />
 * After the mod that defines it, or before. <br />
 * {@link #NONE} is also an option if the developer is okay loading it at whatever time.
 */
public enum DependencyLoadTime
{
    /**
     * No specific time to load the dependency.
     */
    NONE,
    /**
     * The dependency must be loaded AFTER the mod that
     * defines the dependency loads.
     */
    AFTER_PARENT,
    /**
     * The dependency must be loaded BEFORE the mod that
     * defines the dependency loads.
     */
    BEFORE_PARENT
}
