package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.ForgeUtils;
import com.github.mdcdi1315.basemodslib.client.registries.IClientRegistryRegistrar;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import net.minecraft.server.packs.resources.PreparableReloadListener;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

public final class ForgeClientRegistriesRegistrar
    implements IClientRegistryRegistrar
{
    private SingleLinkedListBasedRegister<PreparableReloadListener> client_resource_reload_listeners;

    public ForgeClientRegistriesRegistrar() { client_resource_reload_listeners = new SingleLinkedListBasedRegister<>(); }

    @Override
    public void RegisterResourceReloadListener(String name, PreparableReloadListener preparable_reload_listener)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(preparable_reload_listener, "preparable_reload_listener");
        client_resource_reload_listeners.Register(preparable_reload_listener);
    }

    public void RegisterToEventBus(IEventBus bus)
    {
        ForgeUtils.AddEnumerableListener_DispatchOnce(bus, RegisterClientReloadListenersEvent.class, client_resource_reload_listeners, RegisterClientReloadListenersEvent::registerReloadListener);
        client_resource_reload_listeners = null;
    }
}
