package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.minecraftforge.network.*;
import net.minecraftforge.event.network.CustomPayloadEvent;

public final class ForgeSimpleChannelNetworkBuilder
    implements INetworkBuilder
{
    private boolean aso, aco;
    private Version network_version;
    private final ResourceLocation manager_channel_location;
    private SingleLinkedListBasedRegister<ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> client_packet_reg_info;
    private SingleLinkedListBasedRegister<ServerSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> server_packet_reg_info;

    public ForgeSimpleChannelNetworkBuilder(String mod_id) {
        manager_channel_location = RegistryUtils.ConstructResourceLocation(mod_id, "mdcdi1315_bml_networking_manager");
        client_packet_reg_info = new SingleLinkedListBasedRegister<>();
        server_packet_reg_info = new SingleLinkedListBasedRegister<>();
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
    public void DeclareClientOptionalPresence() { aco = true; }

    @Override
    public void DeclareServerOptionalPresence() { aso = true; }

    @Override
    public <T extends CustomPacketPayload> void RegisterClientBoundPacket(ClientSideNetworkPacketRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        client_packet_reg_info.Register(info);
    }

    @Override
    public <T extends CustomPacketPayload> void RegisterServerBoundPacket(ServerSideNetworkPacketRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        server_packet_reg_info.Register(info);
    }

    private void InitializePackets(SimpleChannel sc, ForgeBasedNetworkManager mgr)
    {
        int packet_index = 1;
        IEnumerator<ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> client_e = client_packet_reg_info.GetEnumerator();
        try {
            while (client_e.MoveNext()) {
                BuildClientBoundMessage(mgr ,client_e.getCurrent() , packet_index++, sc);
            }
        } finally {
            client_e.Dispose();
        }
        client_packet_reg_info = null; // We have registered all the packets, we can clean this list now.

        IEnumerator<ServerSideNetworkPacketRegistrationInfo<?>> server_e = server_packet_reg_info.GetEnumerator();
        try {
            while (server_e.MoveNext()) {
                BuildServerBoundMessage(mgr, server_e.getCurrent(), packet_index++, sc);
            }
        } finally {
            server_e.Dispose();
        }
        server_packet_reg_info = null; // We have registered all the packets, we can clean this list now.
    }

    private record ClientHandlerAction<TP extends CustomPacketPayload>(ForgeBasedNetworkManager manager, Action2<Player, TP> handler)
        implements Action2<TP , CustomPayloadEvent.Context>
    {
        @Override
        public void action(TP packet, CustomPayloadEvent.Context context)
        {
            try {
                manager.CreateReplyEnvironment(context);
                handler.action(BaseModsLibClient.GetLoggedInPlayer() , packet);
            } finally {
                manager.DestroyReplyEnvironment();
            }
        }
    }

    private static <T extends CustomPacketPayload> void BuildClientBoundMessage(ForgeBasedNetworkManager manager, ClientSideNetworkPacketRegistrationInfo<T> info, int discriminator, SimpleChannel channel)
    {
        channel
                .messageBuilder(
                        info.cls(),
                        discriminator,
                        NetworkDirection.PLAY_TO_CLIENT
                ).codec(info.codec())
                .consumerMainThread(new ClientHandlerAction<>(manager , info.handler()))
                .add();
    }

    private record ServerHandlerAction<T extends CustomPacketPayload>(ForgeBasedNetworkManager manager, Action2<ServerPlayer , T> handler)
        implements Action2<T, CustomPayloadEvent.Context>
    {
        @Override
        public void action(T t, CustomPayloadEvent.Context context)
        {
            try {
                manager.CreateReplyEnvironment(context);
                handler.action(context.getSender() , t);
            } finally {
                manager.DestroyReplyEnvironment();
            }
        }
    }

    private static <T extends CustomPacketPayload> void BuildServerBoundMessage(ForgeBasedNetworkManager manager, ServerSideNetworkPacketRegistrationInfo<T> info , int discriminator, SimpleChannel channel)
    {
        channel
                .messageBuilder(
                    info.cls(),
                    discriminator,
                    NetworkDirection.PLAY_TO_SERVER
                ).codec(info.codec())
                .consumerMainThread(new ServerHandlerAction<>(manager, info.handler()))
                .add();
    }

    private record StrictVersionTest(int ver)
        implements Channel.VersionTest
    {
        @Override
        public boolean accepts(Status status, int version) { return status == Status.PRESENT && version == ver; }
    }

    public SimpleChannel Build(ForgeBasedNetworkManager manager)
    {
        var builder = ChannelBuilder.named(manager_channel_location);
        int packed = NetworkHelpers.PackVersion(network_version == null ? new Version(1, 0) : network_version);
        builder.networkProtocolVersion(packed);
        // The below predicate object may be shared in both acceptedVersions methods,
        // so it is best to just create a single predicate object that will be deallocated if not used at all.
        StrictVersionTest svt = new StrictVersionTest(packed);
        if (aco) {
            builder.optionalClient();
        } else {
            builder.clientAcceptedVersions(svt);
        }
        if (aso) {
            builder.optionalServer();
        } else {
            builder.acceptedVersions(svt);
        }
        SimpleChannel sc = builder.simpleChannel();
        InitializePackets(sc , manager);
        return sc.build();
    }
}
