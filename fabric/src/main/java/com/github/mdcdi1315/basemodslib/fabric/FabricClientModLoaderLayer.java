package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

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
    private static SingleLinkedListBasedRegister<ClientConnectedToServer_DispatchModInfoPacketImpl> mod_info_packet_events;

    static {
        mod_info_packet_events = new SingleLinkedListBasedRegister<>();
    }

    public static void RegisterModInfoPacketDispatcher(ServerBoundModInfoPacket packet) {
        mod_info_packet_events.Register(new ClientConnectedToServer_DispatchModInfoPacketImpl(packet));
    }

    @Override
    public void Dispose() {

    }

    private record ClientConnectedToServer_DispatchModInfoPacketImpl(ServerBoundModInfoPacket packet)
            implements Action1<ClientConnectedToServerEvent>
    {
        @Override
        public void action(ClientConnectedToServerEvent obj) { ClientPlayNetworking.send(packet); }
    }

    public FabricClientModLoaderLayer() {
        ClientLifecycleEvents.CLIENT_STARTED.register(FabricClientModLoaderLayer::OnClientStarted);
        ClientLifecycleEvents.CLIENT_STOPPING.register(ClientEventHooks::ClientStopping);
    }

    private static void OnClientStarted(Minecraft mc)
    {
        // When mod loading is complete, do the below:
        var em = BaseModsLib.GetEventsManager();
        var en = mod_info_packet_events.GetEnumerator();
        try {
            while (en.MoveNext()) {
                em.AddEventListener(ClientConnectedToServerEvent.class , en.getCurrent());
            }
        } finally {
            en.Dispose();
        }
        mod_info_packet_events = null;
        BaseModsLib.Destroy();
        ClientEventHooks.ClientStarted(mc);
    }

    @Override
    public void InitializeClientModInstance(IClientModInstance instance, Object o) {
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
}
