package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.Action2ToRunnable;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedList;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class FabricNetworkBuilder
    implements INetworkBuilder
{
    private String mod_id;
    private boolean aso, aco;
    private Version network_version;
    private SingleLinkedList<ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> client_side_info;
    private SingleLinkedList<ServerSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> server_side_info;

    public FabricNetworkBuilder(String mod_id)
    {
        this.mod_id = mod_id;
        client_side_info = new SingleLinkedList<>();
        server_side_info = new SingleLinkedList<>();
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
    public void DeclareClientOptionalPresence() {
        aco = true;
    }

    @Override
    public void DeclareServerOptionalPresence() {
        aso = true;
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
        private record HandlerImplementation<T>(FabricBasedNetworkManager manager, Action2<ServerPlayer, T> action, ServerPlayer sp, T packet)
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
            context.server().execute(new HandlerImplementation<>(manager, this.handler, context.player(), t));
        }
    }

    private <T extends CustomPacketPayload> void RegisterServerBoundPacketInternal(ServerSideNetworkPacketRegistrationInfo<T> info, FabricBasedNetworkManager manager)
    {
        PayloadTypeRegistry.playC2S().register(info.type() , info.codec());
        ServerPlayNetworking.registerGlobalReceiver(info.type(), new ServerPlayChannelInfoHandling<>(manager, info.handler()));
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

    private <T extends CustomPacketPayload> void RegisterClientBoundPacketInternal(ClientSideNetworkPacketRegistrationInfo<T> info)
    {
        PayloadTypeRegistry.playS2C().register(info.type() , info.codec());
        ClientPlayNetworking.registerGlobalReceiver(info.type(), new ClientPlayChannelInfoHandling<>(info.handler()));
    }

    public void Build(FabricBasedNetworkManager manager)
    {
        manager.Mod_Info = new ServerBoundModInfoPacket(mod_id, network_version == null ? new Version(1,0) : network_version, aco, aso);
        mod_id = null;
        IEnumerator<ServerSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> server_e = server_side_info.GetEnumerator();
        try {
            ServerSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload> inf;
            while (server_e.MoveNext()) {
                inf = server_e.getCurrent();
                RegisterServerBoundPacketInternal(inf , manager);
            }
        } finally {
            server_e.Dispose();
        }
        server_side_info = null;

        IEnumerator<ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> client_e = client_side_info.GetEnumerator();
        try {
            ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload> inf; // It is OK this class object to be allocated on server-only, since itself does not access any client-only classes.
            while (client_e.MoveNext()) {
                inf = client_e.getCurrent();
                RegisterClientBoundPacketInternal(inf);
            }
        } finally {
            server_e.Dispose();
        }
        client_side_info = null;
    }
}
