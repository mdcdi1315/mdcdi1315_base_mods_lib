package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

/**
 * Defines hard-coded constants and utility methods for handling mod-loader code from platform-agnostic projects.
 * @since 1.0.11
 */
public enum CommonModLoaderBranding
{
    /**
     * The library runs into a non-common or currently unknown mod loader.
     */
    UNKNOWN("<unknown>"),
    /**
     * The library runs under the Forge mod loader.
     */
    FORGE("forge"),
    /**
     * The library runs under the NeoForge mod loader.
     */
    NEOFORGE("neoforge"),
    /**
     * The library runs under the Fabric mod loader.
     */
    FABRIC("fabric");

    /**
     * Gets a lower-cased string representing the mod loader for the current enumeration constant.
     */
    public final String ModLoaderId;

    CommonModLoaderBranding(String mod_loader_id) { ModLoaderId = mod_loader_id; }

    /**
     * Parses from a mod loader branding string into one of the enumeration constants of the {@link CommonModLoaderBranding} enumeration.
     * @param mod_loader_branding The mod loader branding string to parse from
     * @return The constant value, if that found, that represents the passed mod loader branding string.
     * @throws ArgumentNullException {@code mod_loader_branding} is {@code null}.
     * @throws FormatException {@code mod_loader_branding} cannot be mapped to one of the enumeration constants.
     */
    public static CommonModLoaderBranding Parse(String mod_loader_branding)
        throws ArgumentNullException, FormatException
    {
        ArgumentNullException.ThrowIfNull(mod_loader_branding, "mod_loader_branding");
        for (CommonModLoaderBranding b : values()) {
            if (b.ModLoaderId.equalsIgnoreCase(mod_loader_branding)) { return b; }
        }
        throw new FormatException(String.format("No matching constant found for mod loader branding '%s'!" , mod_loader_branding));
    }

    /**
     * Gets a value whether the currently identified mod loader branding matches the mod loader branding value reported by the library.
     * @param branding The mod loader branding to test against.
     * @return A boolean value ({@code true} or {@code false}), indicating whether the currently identified mod loader branding matches the mod loader branding value reported by the library.
     * @throws ArgumentNullException {@code branding} is {@code null}.
     */
    public static boolean IsModLoader(CommonModLoaderBranding branding)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(branding, "branding");
        return BaseModsLib.GetModLoaderBranding().equalsIgnoreCase(branding.ModLoaderId);
    }
}
