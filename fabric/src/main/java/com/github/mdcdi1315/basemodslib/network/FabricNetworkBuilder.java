package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.Action2ToRunnable;
import com.github.mdcdi1315.basemodslib.utils.Pair;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

public final class FabricNetworkBuilder
    implements INetworkBuilder
{
    private String mod_id;
    private boolean aso, aco;
    private Version network_version;
    private final ResourceLocation networking_location;
    private List<ClientSideNetworkPacketRegistrationInfo<?>> client_side_info;
    private List<ServerSideNetworkPacketRegistrationInfo<?>> server_side_info;

    public FabricNetworkBuilder(String mod_id)
    {
        networking_location = ResourceLocation.tryBuild(this.mod_id = mod_id, "mdcdi1315_BML_networking_manager");
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
    public <T> void RegisterClientBoundPacket(ClientSideNetworkPacketRegistrationInfo<T> info) {
        client_side_info.Add(info);
    }

    @Override
    public <T> void RegisterServerBoundPacket(ServerSideNetworkPacketRegistrationInfo<T> info) {
        server_side_info.Add(info);
    }

    private record ServerPlayChannelInfoHandling<T>(FabricBasedNetworkManager manager, Func2<FriendlyByteBuf, T> packet_decoder, Action2<ServerPlayer, T> handler)
        implements ServerPlayNetworking.PlayChannelHandler
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
        public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
            server.execute(new HandlerImplementation<>(this.handler, player , manager, packet_decoder.function(buf)));
        }
    }

    private <T> FabricBasedNetworkManager.PacketData<T> RegisterServerBoundPacketInternal(ResourceLocation constructed_name, ServerSideNetworkPacketRegistrationInfo<T> info, FabricBasedNetworkManager manager)
    {
        ServerPlayNetworking.registerGlobalReceiver(
                constructed_name,
                new ServerPlayChannelInfoHandling<>(manager, info.decode_function(), info.handler())
        );
        return new FabricBasedNetworkManager.PacketData<>(constructed_name , info.encode_function());
    }

    private record ClientPlayChannelInfoHandling<T>(Func2<FriendlyByteBuf, T> packet_decoder, Action2<Player, T> handler)
        implements ClientPlayNetworking.PlayChannelHandler
    {
        @Override
        public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
            client.execute(new Action2ToRunnable<>(this.handler, client.player, packet_decoder.function(buf)));
        }
    }

    private <T> FabricBasedNetworkManager.PacketData<T> RegisterClientBoundPacketInternal(ResourceLocation constructed_name, ClientSideNetworkPacketRegistrationInfo<T> info)
    {
        ClientPlayNetworking.registerGlobalReceiver(
                constructed_name,
                new ClientPlayChannelInfoHandling<>(info.decode_function(), info.handler())
        );
        return new FabricBasedNetworkManager.PacketData<>(constructed_name , info.encode_function());
    }

    public void Build(FabricBasedNetworkManager manager)
    {
        manager.Mod_Info = new ServerBoundModInfoPacket(mod_id, network_version, aco, aso);
        manager.Client_Packet_IDs = new HashMap<>(client_side_info.getCount());
        manager.Server_Packet_IDs = new HashMap<>(server_side_info.getCount());
        int packet_ordinal = 0;
        ResourceLocation temp_location;
        IEnumerator<ServerSideNetworkPacketRegistrationInfo<?>> server_e = server_side_info.GetEnumerator();
        try {
            ServerSideNetworkPacketRegistrationInfo<?> inf;
            while (server_e.MoveNext()) {
                inf = server_e.getCurrent();
                temp_location = ResourceLocation.tryBuild(mod_id, "networking_packet_id_" + packet_ordinal++);
                if (temp_location == null) {
                    BaseModsLib.LOGGER.info("Failed to register a packet because the packet location could not be constructed.");
                } else {
                    manager.Server_Packet_IDs.put(inf.cls(), RegisterServerBoundPacketInternal(temp_location, inf , manager));
                }
            }
        } finally {
            server_e.Dispose();
        }

        IEnumerator<ClientSideNetworkPacketRegistrationInfo<?>> client_e = client_side_info.GetEnumerator();
        try {
            ClientSideNetworkPacketRegistrationInfo<?> inf; // It is OK this class object to be allocated on server-only, since itself does not access any client-only classes.
            while (client_e.MoveNext()) {
                inf = client_e.getCurrent();
                temp_location = ResourceLocation.tryBuild(mod_id, "networking_packet_id_" + packet_ordinal++);
                if (temp_location == null) {
                    BaseModsLib.LOGGER.info("Failed to register a packet because the packet location could not be constructed.");
                } else {
                    manager.Client_Packet_IDs.put(inf.cls(), RegisterClientBoundPacketInternal(temp_location, inf));
                }
            }
        } finally {
            server_e.Dispose();
        }
    }
}
