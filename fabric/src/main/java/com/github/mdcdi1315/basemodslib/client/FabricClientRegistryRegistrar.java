package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;
import com.github.mdcdi1315.basemodslib.client.registries.IClientRegistryRegistrar;
import com.github.mdcdi1315.basemodslib.registries.FabricBridgedIdentifiableReloadListener;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.server.packs.PackType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

@ClientOnlyEnvironment
public final class FabricClientRegistryRegistrar
    implements IClientRegistryRegistrar
{
    private final String mod_id;

    public FabricClientRegistryRegistrar(String mod_id) { this.mod_id = mod_id; }

    private ResourceLocation BuildAndValidateLocation(String path)
    {
        ResourceLocation ret = ResourceLocation.tryBuild(mod_id, path);

        if (ret == null) {
            throw new RuntimeException("Could not create the resource location!");
        }

        return ret;
    }

    @Override
    public void RegisterResourceReloadListener(String name, PreparableReloadListener preparable_reload_listener)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(preparable_reload_listener, "preparable_reload_listener");
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricBridgedIdentifiableReloadListener(
                BuildAndValidateLocation(name),
                preparable_reload_listener
        ));
    }
}
