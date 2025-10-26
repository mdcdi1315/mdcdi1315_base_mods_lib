package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;

/**
 * Like {@link IModLoaderLayer}, this provides the mod loader layer for client side mods.
 */
public interface IClientModLoaderLayer
{
    void InitializeClientModInstance(IClientModInstance instance, Object mod_object);
}
