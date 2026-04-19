package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.EmptyModObject;
import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;
import com.github.mdcdi1315.basemodslib.IClientModLoaderLayer;
import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientEventHooks;
import com.github.mdcdi1315.basemodslib.network.ServerBoundModInfoPacket;
import com.github.mdcdi1315.basemodslib.client.FabricClientRegistryRegistrar;
import com.github.mdcdi1315.basemodslib.client.FabricClientArtifactsRegistrar;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientConnectedToServerEvent;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import net.minecraft.client.Minecraft;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

@ClientOnlyEnvironment
public final class FabricClientModLoaderLayer
    implements IClientModLoaderLayer
{
    private static SingleLinkedListBasedRegister<ServerBoundModInfoPacket> mod_info_packets;

    static {
        mod_info_packets = new SingleLinkedListBasedRegister<>();
    }

    public static void RegisterModInfoPacketDispatcher(ServerBoundModInfoPacket packet) { mod_info_packets.Register(packet); }

    public FabricClientModLoaderLayer() {
        BaseModsLib.GetEventsManager().AddEventListener(ClientConnectedToServerEvent.class, FabricClientModLoaderLayer::DispatchModInfoPacketsAction);
        ClientLifecycleEvents.CLIENT_STARTED.register(FabricClientModLoaderLayer::OnClientStarted);
        ClientLifecycleEvents.CLIENT_STOPPING.register(ClientEventHooks::ClientStopping);
    }

    private static void DispatchModInfoPacketsAction(ClientConnectedToServerEvent event)
    {
        IEnumerator<ServerBoundModInfoPacket> packets = mod_info_packets.GetEnumerator();
        try {
            while (packets.MoveNext()) {
                ClientPlayNetworking.send(packets.getCurrent());
            }
        } finally {
            packets.Dispose();
        }
    }

    private static void OnClientStarted(Minecraft mc)
    {
        BaseModsLib.Destroy();
        ClientEventHooks.ClientStarted(mc);
    }

    @Override
    public void InitializeClientModInstance(IClientModInstance instance, Object o)
    {
        if (!(o instanceof EmptyModObject)) {
            throw new InvalidOperationException(String.format("The mod object was not of type EmptyModObject!!!!\nActual type: %s", o == null ? "<NULL>" : o.getClass().getName()));
        }

        FabricClientArtifactsRegistrar registrar = new FabricClientArtifactsRegistrar();

        instance.RegisterModelDefinitions(registrar);
        instance.RegisterEntityRenderers(registrar);
        instance.RegisterBlockEntityRenderers(registrar);
        instance.RegisterColorHandlers(registrar);
        instance.RegisterParticleProviders(registrar);
        instance.RegisterMenuScreens(registrar);

        instance.RegisterClientRegistryItems(new FabricClientRegistryRegistrar(instance.GetModId()));
    }

    @Override
    public void Dispose() { mod_info_packets = null; }
}
