package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.BaseModsLibClient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.network.*;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.function.BiConsumer;

public final class ForgeSimpleChannelNetworkBuilder
    implements INetworkBuilder
{
    private boolean aso, aco;
    private Version network_version;
    private final ResourceLocation manager_channel_location;
    private List<ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> client_packet_reg_info;
    private List<ServerSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> server_packet_reg_info;

    public ForgeSimpleChannelNetworkBuilder(String mod_id) {
        manager_channel_location = ResourceLocation.tryBuild(mod_id, "mdcdi1315_bml_networking_manager");
        client_packet_reg_info = new List<>();
        server_packet_reg_info = new List<>();
        network_version = null;
        aso = false;
        aco = false;
    }

    @Override
    public void DefineNetworkVersion(Version ver) {
        ArgumentNullException.ThrowIfNull(ver, "ver");
        this.network_version = ver;
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
        client_packet_reg_info.Add(info);
    }

    @Override
    public <T extends CustomPacketPayload> void RegisterServerBoundPacket(ServerSideNetworkPacketRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        server_packet_reg_info.Add(info);
    }

    private void InitializePackets(SimpleChannel sc, ForgeBasedNetworkManager mgr)
    {
        int packet_index = 0;
        IEnumerator<ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> client_e = client_packet_reg_info.GetEnumerator();
        try {
            while (client_e.MoveNext()) {
                BuildClientBoundMessage(client_e.getCurrent() , packet_index++, sc);
            }
        } finally {
            client_e.Dispose();
        }
        IEnumerator<ServerSideNetworkPacketRegistrationInfo<?>> server_e = server_packet_reg_info.GetEnumerator();
        try {
            while (server_e.MoveNext()) {

            }
        } finally {
            server_e.Dispose();
        }
    }

    private record HandlerToConsumerMainThread_Client<T extends CustomPacketPayload>(Action2<Player, T> handler)
            implements BiConsumer<T, CustomPayloadEvent.Context>
    {
        @Override
        public void accept(T t, CustomPayloadEvent.Context context) {
            handler.action(BaseModsLibClient.GetLoggedInPlayer(), t);
        }
    }

    private static <T extends CustomPacketPayload> void BuildClientBoundMessage(ClientSideNetworkPacketRegistrationInfo<T> info, int discriminator, SimpleChannel channel)
    {
        channel
                .messageBuilder(
                        info.cls(),
                        discriminator,
                        NetworkDirection.PLAY_TO_CLIENT
                ).codec(info.codec())
                .consumerMainThread(new HandlerToConsumerMainThread_Client<>(info.handler()))
                .add();
    }

    private record HandlerToConsumerMainThread_Server<T extends CustomPacketPayload>(Action2<ServerPlayer , T> handler)
        implements BiConsumer<T, CustomPayloadEvent.Context>
    {
        @Override
        public void accept(T t, CustomPayloadEvent.Context context) {
            handler.action(context.getSender() , t);
        }
    }

    private static <T extends CustomPacketPayload> void BuildServerBoundMessage(ServerSideNetworkPacketRegistrationInfo<T> info , int discriminator, SimpleChannel channel)
    {
        channel
                .messageBuilder(
                    info.cls(),
                    discriminator,
                    NetworkDirection.PLAY_TO_SERVER
                ).codec(info.codec())
                .consumerMainThread(new HandlerToConsumerMainThread_Server<>(info.handler()))
                .add();
    }

    private record StrictVersionTest(int ver)
        implements Channel.VersionTest
    {
        @Override
        public boolean accepts(Status status, int version) {
            return status == Status.PRESENT && version == ver;
        }
    }

    public SimpleChannel Build(ForgeBasedNetworkManager manager)
    {
        var builder = ChannelBuilder.named(manager_channel_location);
        int packed = NetworkHelpers.PackVersion(network_version == null ? new Version(1, 0) : network_version);
        builder.networkProtocolVersion(packed);
        if (aco) {
            builder.optionalClient();
        } else {
            builder.clientAcceptedVersions(new StrictVersionTest(packed));
        }
        if (aso) {
            builder.optionalServer();
        } else {
            builder.acceptedVersions(new StrictVersionTest(packed));
        }
        var sc = builder.simpleChannel();
        InitializePackets(sc , manager);
        return sc.build();
    }
}
