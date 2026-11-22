package com.github.mdcdi1315.basemodslib.mods.information;

/**
 * Provides constants that define the type of a dependency of a mod.
 */
public enum DependencyType
{
    /**
     * Placeholder value for dependency types that are provided
     * by mod-loaders and pertain to their functionality.
     */
    UNMAPPABLE,
    /**
     * Defines that the dependency is required,
     * and if not found, the mod-loader should
     * stop loading.
     */
    REQUIRED,
    /**
     * Defines that the dependency is optional,
     * and if not found, the mod-loader should
     * continue loading without it.
     */
    OPTIONAL,
    /**
     * Defines that the dependency, if found,
     * the mod-loader stop loading because
     * that mod does breaking changes which
     * break the parent mod itself.
     */
    INCOMPATIBLE,
}
