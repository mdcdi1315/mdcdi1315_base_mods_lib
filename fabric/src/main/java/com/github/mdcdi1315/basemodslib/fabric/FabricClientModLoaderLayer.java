package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.EmptyModObject;
import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;
import com.github.mdcdi1315.basemodslib.IClientModLoaderLayer;
import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;
import com.github.mdcdi1315.basemodslib.network.ServerBoundModInfoPacket;
import com.github.mdcdi1315.basemodslib.client.FabricClientArtifactsRegistrar;
import com.github.mdcdi1315.basemodslib.eventapi.mods.ModLoadingCompleteEvent;

import com.github.mdcdi1315.basemodslib.eventapi.client.ClientConnectedToServerEvent;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@ClientOnlyEnvironment
public final class FabricClientModLoaderLayer
    implements IClientModLoaderLayer
{
    private static List<ClientConnectedToServer_DispatchModInfoPacketImpl> mod_info_packet_events;

    static {
        mod_info_packet_events = new List<>(3);
    }

    public static void RegisterModInfoPacketDispatcher(ServerBoundModInfoPacket packet) {
        mod_info_packet_events.Add(new ClientConnectedToServer_DispatchModInfoPacketImpl(packet));
    }

    @Override
    public void Dispose() {

    }

    private record ClientConnectedToServer_DispatchModInfoPacketImpl(ServerBoundModInfoPacket packet)
            implements Action1<ClientConnectedToServerEvent>
    {
        @Override
        public void action(ClientConnectedToServerEvent obj) {
            ClientPlayNetworking.send(packet);
        }
    }

    public FabricClientModLoaderLayer() {
        BaseModsLib.GetEventsManager().AddEventListener(ModLoadingCompleteEvent.class, FabricClientModLoaderLayer::OnModLoadingComplete);
    }

    private static void OnModLoadingComplete(ModLoadingCompleteEvent evt)
    {
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
    }

    @Override
    public void InitializeClientModInstance(IClientModInstance instance, Object o) {
        if (!(o instanceof EmptyModObject)) {
            throw new InvalidOperationException(String.format("The mod object was not of type EmptyModObject!!!!\nActual type: %s", o.getClass().getName()));
        }

        FabricClientArtifactsRegistrar registrar = new FabricClientArtifactsRegistrar();

        instance.RegisterModelDefinitions(registrar);
        instance.RegisterEntityRenderers(registrar);
        instance.RegisterBlockEntityRenderers(registrar);
        instance.RegisterColorHandlers(registrar);
        instance.RegisterParticleProviders(registrar);
        instance.RegisterMenuScreens(registrar);
    }
}
