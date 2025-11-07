package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;

/**
 * Like {@link IModLoaderLayer}, this provides the mod loader layer for client side mods.
 */
public interface IClientModLoaderLayer
{
    /**
     * Initializes a new client-side mod instance.
     * @param instance The client-side mod instance to further initialize.
     * @param mod_object The mod object provided by the mod loader. It is used for actually configuring the mod.
     */
    void InitializeClientModInstance(IClientModInstance instance, Object mod_object);
}