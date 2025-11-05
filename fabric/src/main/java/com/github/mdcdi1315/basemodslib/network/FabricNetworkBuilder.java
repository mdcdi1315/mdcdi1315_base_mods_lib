package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.Action2ToRunnable;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;

public final class FabricNetworkBuilder
    implements INetworkBuilder
{
    private String mod_id;
    private boolean aso, aco;
    private Version network_version;
    private List<ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> client_side_info;
    private List<ServerSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> server_side_info;

    public FabricNetworkBuilder(String mod_id)
    {
        this.mod_id = mod_id;
        client_side_info = new List<>();
        server_side_info = new List<>();
        network_version = null;
        aso = false;
        aco = false;
    }

    @Override
    public void DefineNetworkVersion(Version ver)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(ver, "ver");
        network_version = ver;
    }

    @Override
    public void AllowServerOnly() {
        aso = true;
    }

    @Override
    public void AllowClientOnly() {
        aco = true;
    }

    @Override
    public <T extends CustomPacketPayload> void RegisterClientBoundPacket(ClientSideNetworkPacketRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        client_side_info.Add(info);
    }

    @Override
    public <T extends CustomPacketPayload> void RegisterServerBoundPacket(ServerSideNetworkPacketRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        server_side_info.Add(info);
    }

    private record ServerPlayChannelInfoHandling<T extends CustomPacketPayload>(FabricBasedNetworkManager manager, Action2<ServerPlayer, T> handler)
        implements ServerPlayNetworking.PlayPayloadHandler<T>
    {
        private record HandlerImplementation<T>(Action2<ServerPlayer, T> action, ServerPlayer sp, FabricBasedNetworkManager manager, T packet)
            implements Runnable
        {
            @Override
            public void run() {
                manager.Player_To_Reply_To = sp;
                action.action(sp , packet);
                manager.Player_To_Reply_To = null;
            }
        }

        @Override
        public void receive(T t, ServerPlayNetworking.Context context) {
            var server = context.server();
            server.execute(new HandlerImplementation<>(this.handler, context.player() , manager, t));
        }
    }

    private <T extends CustomPacketPayload> void RegisterServerBoundPacketInternal(ResourceLocation constructed_name, ServerSideNetworkPacketRegistrationInfo<T> info, FabricBasedNetworkManager manager)
    {
        var type = new CustomPacketPayload.Type<T>(constructed_name);
        PayloadTypeRegistry.playS2C().register(type , info.codec());
        ServerPlayNetworking.registerGlobalReceiver(type, new ServerPlayChannelInfoHandling<>(manager, info.handler()));
    }

    private record ClientPlayChannelInfoHandling<T extends CustomPacketPayload>(Action2<Player, T> handler)
        implements ClientPlayNetworking.PlayPayloadHandler<T>
    {
        @Override
        public void receive(T t, ClientPlayNetworking.Context context) {
            var client = context.client();
            client.execute(new Action2ToRunnable<>(this.handler, client.player, t));
        }
    }

    private <T extends CustomPacketPayload> void RegisterClientBoundPacketInternal(ResourceLocation constructed_name, ClientSideNetworkPacketRegistrationInfo<T> info)
    {
        var type = new CustomPacketPayload.Type<T>(constructed_name);
        PayloadTypeRegistry.playC2S().register(type , info.codec());
        ClientPlayNetworking.registerGlobalReceiver(type, new ClientPlayChannelInfoHandling<>(info.handler()));
    }

    public void Build(FabricBasedNetworkManager manager)
    {
        manager.Mod_Info = new ServerBoundModInfoPacket(mod_id, network_version == null ? new Version(1,0) : network_version, aco, aso);
        int packet_ordinal = 0;
        ResourceLocation temp_location;
        IEnumerator<ServerSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> server_e = server_side_info.GetEnumerator();
        try {
            ServerSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload> inf;
            while (server_e.MoveNext()) {
                inf = server_e.getCurrent();
                temp_location = ResourceLocation.tryBuild(mod_id, "networking_packet_id_" + packet_ordinal++);
                if (temp_location == null) {
                    BaseModsLib.LOGGER.warn("NETWORKING: Failed to register a packet because the packet location could not be constructed.");
                } else {
                    RegisterServerBoundPacketInternal(temp_location, inf , manager);
                }
            }
        } finally {
            server_e.Dispose();
        }

        IEnumerator<ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> client_e = client_side_info.GetEnumerator();
        try {
            ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload> inf; // It is OK this class object to be allocated on server-only, since itself does not access any client-only classes.
            while (client_e.MoveNext()) {
                inf = client_e.getCurrent();
                temp_location = ResourceLocation.tryBuild(mod_id, "networking_packet_id_" + packet_ordinal++);
                if (temp_location == null) {
                    BaseModsLib.LOGGER.warn("NETWORKING: Failed to register a packet because the packet location could not be constructed.");
                } else {
                    RegisterClientBoundPacketInternal(temp_location, inf);
                }
            }
        } finally {
            server_e.Dispose();
        }
    }
}
