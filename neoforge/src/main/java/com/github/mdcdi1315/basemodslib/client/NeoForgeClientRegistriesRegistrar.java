package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.NeoForgeUtils;
import com.github.mdcdi1315.basemodslib.client.registries.IClientRegistryRegistrar;
import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

public final class NeoForgeClientRegistriesRegistrar
    implements IClientRegistryRegistrar
{
    private String mod_id;
    private SingleLinkedListBasedRegister<Pair<ResourceLocation, PreparableReloadListener>> client_resource_reload_listeners;

    public NeoForgeClientRegistriesRegistrar(String mod_id) {
        this.mod_id = mod_id;
        client_resource_reload_listeners = new SingleLinkedListBasedRegister<>();
    }

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
        client_resource_reload_listeners.Register(new Pair<>(BuildAndValidateLocation(name), preparable_reload_listener));
    }

    private static void RegisterClientReloadListener(AddClientReloadListenersEvent event, Pair<ResourceLocation, PreparableReloadListener> entry) { event.addListener(entry.first(), entry.second()); }

    public void RegisterToEventBus(IEventBus bus)
    {
        NeoForgeUtils.AddEnumerableListener_DispatchOnce(bus, AddClientReloadListenersEvent.class, client_resource_reload_listeners, NeoForgeClientRegistriesRegistrar::RegisterClientReloadListener);
        client_resource_reload_listeners = null;
        mod_id = null;
    }
}
