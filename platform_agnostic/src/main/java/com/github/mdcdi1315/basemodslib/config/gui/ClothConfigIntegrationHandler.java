package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.UnreachableException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.CommonModLoaderBranding;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ClothConfigIntegrationHandler
{
    private static Object instantiated_object_or_failure;
    private static final String CLOTH_CONFIG_API_FORGE = "cloth_config";
    private static final String CLOTH_CONFIG_API_FABRIC = "cloth-config";

    static {
        instantiated_object_or_failure = new Object();
    }

    public static void Instantiate()
    {
        boolean found = false;
        try {
            found = BaseModsLib.IsModLoaded(BaseModsLib.GetCommonModLoaderBranding() == CommonModLoaderBranding.FABRIC ? CLOTH_CONFIG_API_FABRIC : CLOTH_CONFIG_API_FORGE);
        } catch (FormatException fe) {
            BaseModsLib.LOGGER.warn("ClothConfigIntegrationHandler: Could not realize the specified mod loader: \"{}\". Cloth Config API support won't be initialized as a defense measure.", BaseModsLib.GetModLoaderBranding());
        }
        if (found) {
            try {
                instantiated_object_or_failure = IClothConfigScreenCreator.CreateInstance();
            } catch (NoClassDefFoundError | Exception e) {
                instantiated_object_or_failure = e;
                BaseModsLib.LOGGER.warn("ClothConfigIntegrationHandler: Cloth Config API integration handler init has been catastrophically failed.", e);
            }
        } else {
            instantiated_object_or_failure = null;
        }
    }

    public static void Destroy()
    {
        instantiated_object_or_failure = null;
    }

    @NotNull
    public static IClothConfigScreenCreator GetCreator()
            throws NoClassDefFoundError, ClothConfigAPINotSupportedException
    {
        if (instantiated_object_or_failure == null) {
            throw new ClothConfigAPINotSupportedException();
        } else if (instantiated_object_or_failure instanceof IClothConfigScreenCreator c) {
            return c;
        } else if (instantiated_object_or_failure instanceof NoClassDefFoundError c) {
            throw c;
        } else {
            throw new UnreachableException();
        }
    }
}
