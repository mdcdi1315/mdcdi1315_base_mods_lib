package com.github.mdcdi1315.basemodslib.client.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.Contract;

import net.minecraft.server.packs.resources.PreparableReloadListener;

/**
 * CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY <br />
 * Provides methods for registering registry objects and registries to mod loaders for Minecraft clients only.
 * @since 1.0.24
 */
@Contract
public interface IClientRegistryRegistrar
{
    /**
     * Registers a preparable resource reload listener of the specified name. <br />
     * Note: This registers resource reload listeners for client resource packs.
     * If you need for data packs, use the {@link com.github.mdcdi1315.basemodslib.registries.IRegistryRegistrar#RegisterResourceReloadListener(String, PreparableReloadListener)} method instead.
     * @param name The name of the preparable resource reload listener.
     * @param preparable_reload_listener The preparable resource reload listener to register.
     * @throws ArgumentNullException {@code location} and/or {@code name} are {@code null}.
     */
    void RegisterResourceReloadListener(String name, PreparableReloadListener preparable_reload_listener) throws ArgumentNullException;
}
