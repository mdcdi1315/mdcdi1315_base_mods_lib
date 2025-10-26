package com.github.mdcdi1315.basemodslib.config;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Defines the base interface for mod configuration files.
 */
public interface IModConfig
{
    /**
     * Gets the name of this configuration file.
     * @return The name of the configuration file.
     */
    @NotNull
    String GetName();

    /**
     * Gets a generic comment for this configuration file.
     * @return A comment describing the configuration file in general.
     */
    @MaybeNull
    default String GetComment() {
        return "";
    }
}
