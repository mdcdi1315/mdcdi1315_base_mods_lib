package com.github.mdcdi1315.basemodslib.mods.information;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

/**
 * Defines a dependency to another mod.
 * @param ModID The ID of the mod that the parent mod depends on.
 * @param LoadTime The time when this mod dependency should be loaded.
 * @param Type The type of the dependency (optional,required, etc.)
 */
public record ModDependency(String ModID, DependencyLoadTime LoadTime, DependencyType Type)
{
    public ModDependency
    {
        ArgumentNullException.ThrowIfNull(Type, "Type");
        ArgumentNullException.ThrowIfNull(ModID, "ModID");
        ArgumentNullException.ThrowIfNull(LoadTime, "LoadTime");
    }
}
