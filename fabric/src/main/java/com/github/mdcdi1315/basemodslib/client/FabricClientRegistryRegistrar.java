package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;
import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.client.registries.IClientRegistryRegistrar;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

@ClientOnlyEnvironment
public final class FabricClientRegistryRegistrar
    implements IClientRegistryRegistrar
{
    private final String mod_id;

    public FabricClientRegistryRegistrar(String mod_id) { this.mod_id = mod_id; }

    @Override
    public void RegisterResourceReloadListener(String name, PreparableReloadListener preparable_reload_listener)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(preparable_reload_listener, "preparable_reload_listener");
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                RegistryUtils.ConstructResourceLocation(mod_id, name),
                preparable_reload_listener
        );
    }
}
