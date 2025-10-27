package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.utils.Action2ToBiConsumer;
import com.github.mdcdi1315.basemodslib.utils.Func2ToFunction;
import com.github.mdcdi1315.basemodslib.utils.StringSupplier;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ForgeSimpleChannelNetworkBuilder
    implements INetworkBuilder
{
    private Version network_version;
    private final ResourceLocation manager_channel_location;
    private Predicate<String> client_version_predicate , server_version_predicate;
    private List<ClientSideNetworkPacketRegistrationInfo<?>> client_packet_reg_info;
    private List<ServerSideNetworkPacketRegistrationInfo<?>> server_packet_reg_info;

    private static boolean VersionAlwaysPassPredicate(String s) { return true; }

    public ForgeSimpleChannelNetworkBuilder(String mod_id) {
        manager_channel_location = ResourceLocation.tryBuild(mod_id, "mdcdi1315_BML_networking_manager");
        client_packet_reg_info = new List<>();
        server_packet_reg_info = new List<>();
        network_version = null;
        client_version_predicate = null;
        server_version_predicate = null;
    }

    @Override
    public void DefineNetworkVersion(Version ver) {
        ArgumentNullException.ThrowIfNull(ver, "ver");
        this.network_version = ver;
    }

    @Override
    public void AllowServerOnly() {
        server_version_predicate = ForgeSimpleChannelNetworkBuilder::VersionAlwaysPassPredicate;
    }

    @Override
    public void AllowClientOnly() {
        client_version_predicate = ForgeSimpleChannelNetworkBuilder::VersionAlwaysPassPredicate;
    }

    @Override
    public <T> void RegisterClientBoundPacket(ClientSideNetworkPacketRegistrationInfo<T> info) {
        client_packet_reg_info.Add(info);
    }

    @Override
    public <T> void RegisterServerBoundPacket(ServerSideNetworkPacketRegistrationInfo<T> info) {
        server_packet_reg_info.Add(info);
    }

    private record PredicateByVersion(String version)
        implements Predicate<String>
    {
        @Override
        public boolean test(String s) {
            return version.equals(s);
        }
    }

    private void InitializePackets(SimpleChannel sc, ForgeBasedNetworkManager mgr)
    {
        int id_index = 0;
        IEnumerator<ClientSideNetworkPacketRegistrationInfo<?>> client_e = client_packet_reg_info.GetEnumerator();
        try {
            while (client_e.MoveNext()) {
                RegisterClientBoundInternal(client_e.getCurrent(), id_index++, sc);
            }
        } finally {
            client_e.Dispose();
        }
        IEnumerator<ServerSideNetworkPacketRegistrationInfo<?>> server_e = server_packet_reg_info.GetEnumerator();
        try {
            while (server_e.MoveNext()) {
                RegisterServerBoundInternal(server_e.getCurrent() , id_index++, sc, mgr);
            }
        } finally {
            server_e.Dispose();
        }
    }

    private record MessageConsumer_Client<T>(Action2<Player , T> act)
        implements BiConsumer<T, Supplier<NetworkEvent.Context>>
    {
        private record WorkImplementation<T>(Action2<Player , T> act, T packet)
            implements Runnable
        {
            @Override
            public void run() {
                act.action(BaseModsLibClient.GetLoggedInPlayer(), packet);
            }
        }

        @Override
        public void accept(T any, Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context cxt = supplier.get();
            if (cxt.getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
                BaseModsLib.LOGGER.warn("NETWORKMANAGER: Received {} on incorrect side {}", any.getClass().getName(), cxt.getDirection());
                return;
            }

            cxt.enqueueWork(new WorkImplementation<>(act , any));
            cxt.setPacketHandled(true);
        }
    }

    private record MessageConsumer_Server<T>(Action2<ServerPlayer, T> act, ForgeBasedNetworkManager manager)
        implements BiConsumer<T, Supplier<NetworkEvent.Context>>
    {
        private record WorkImplementation<T>(Action2<ServerPlayer , T> act, NetworkEvent.Context cxt, ForgeBasedNetworkManager manager, T packet)
                implements Runnable
        {
            @Override
            public void run() {
                manager.cxt = cxt;
                act.action(cxt.getSender(), packet);
                manager.cxt = null;
            }
        }

        @Override
        public void accept(T any, Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context context = supplier.get();
            if (context.getDirection() != NetworkDirection.PLAY_TO_SERVER) {
                BaseModsLib.LOGGER.warn("NETWORKMANAGER: Received {} on incorrect side {}", any.getClass().getName(), context.getDirection());
                return;
            }

            context.enqueueWork(new WorkImplementation<>(act, context, manager, any));
            context.setPacketHandled(true);
        }
    }

    private static <T> void RegisterClientBoundInternal(ClientSideNetworkPacketRegistrationInfo<T> inf, int next_index, SimpleChannel sc)
    {
        sc.registerMessage(
                next_index,
                inf.cls(),
                new Action2ToBiConsumer<>(inf.encode_function()),
                new Func2ToFunction<>(inf.decode_function()),
                new MessageConsumer_Client<>(inf.handler())
        );
    }

    private static <T> void RegisterServerBoundInternal(ServerSideNetworkPacketRegistrationInfo<T> inf, int next_index, SimpleChannel sc, ForgeBasedNetworkManager mgr)
    {
        sc.registerMessage(
                next_index,
                inf.cls(),
                new Action2ToBiConsumer<>(inf.encode_function()),
                new Func2ToFunction<>(inf.decode_function()),
                new MessageConsumer_Server<>(inf.handler(), mgr)
        );
    }

    public SimpleChannel Build(ForgeBasedNetworkManager manager)
    {
        String network_version_string;
        if (network_version == null) {
            network_version_string = "1.0";
        } else {
            network_version_string = network_version.toString();
        }
        if (client_version_predicate == null) {
            client_version_predicate = new PredicateByVersion(network_version_string);
        }
        if (server_version_predicate == null) {
            server_version_predicate = new PredicateByVersion(network_version_string);
        }
        SimpleChannel sc = NetworkRegistry.newSimpleChannel(
                manager_channel_location,
                new StringSupplier(network_version_string),
                client_version_predicate,
                server_version_predicate
        );
        InitializePackets(sc , manager);
        return sc;
    }
}
