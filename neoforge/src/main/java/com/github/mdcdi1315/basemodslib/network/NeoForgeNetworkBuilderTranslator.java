package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

import com.github.mdcdi1315.basemodslib.utils.EmptyEnumerable;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NeoForgeNetworkBuilderTranslator
    implements Action1<RegisterPayloadHandlersEvent>
{
    private record ActionWithManager<T1 , T2>(NeoForgeNetworkingManager manager, IPayloadContext cxt, Action2<T1 , T2> action , T1 in_1, T2 in_2)
            implements Runnable
    {
        @Override
        public void run()
        {
            try {
                manager.CreateReplyEnvironment(cxt);
                action.action(in_1 , in_2);
            } finally {
                manager.DestroyReplyEnvironment();
            }
        }
    }

    private record PayloadHandler_Client<T extends CustomPacketPayload>(NeoForgeNetworkingManager manager, Action2<Player, T> handler)
            implements IPayloadHandler<T>
    {
        @Override
        public void handle(T packet, IPayloadContext pc) {
            pc.enqueueWork(new ActionWithManager<>(manager, pc , handler , pc.player() , packet));
        }
    }

    private static <T extends CustomPacketPayload> void RegisterClientSidePacket(NeoForgeNetworkingManager manager, PayloadRegistrar registrar , ClientSideNetworkPacketRegistrationInfo<T> info)
    {
        registrar.playToClient(
                info.type(),
                info.codec(),
                new PayloadHandler_Client<>(manager , info.handler())
        );
    }

    private record PayloadHandler_Server<T extends CustomPacketPayload>(NeoForgeNetworkingManager manager, Action2<ServerPlayer, T> handler)
            implements IPayloadHandler<T>
    {
        @Override
        public void handle(T packet, IPayloadContext pc) {
            pc.enqueueWork(new ActionWithManager<>(manager , pc , handler, (ServerPlayer) pc.player() , packet));
        }
    }

    private static <T extends CustomPacketPayload> void RegisterServerSidePacket(NeoForgeNetworkingManager manager, PayloadRegistrar registrar , ServerSideNetworkPacketRegistrationInfo<T> info)
    {
        registrar.playToServer(
                info.type(),
                info.codec(),
                new PayloadHandler_Server<>(manager , info.handler())
        );
    }

    private boolean optional;
    private String net_version;
    private NeoForgeNetworkingManager manager;
    private IEnumerable<ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> client_side_infos;
    private IEnumerable<ServerSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> server_side_infos;

    public NeoForgeNetworkBuilderTranslator(
            boolean opt,
            String network_version,
            NeoForgeNetworkingManager manager,
            IEnumerable<ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> client,
            IEnumerable<ServerSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> server)
    {
        optional = opt;
        this.manager = manager;
        client_side_infos = client;
        server_side_infos = server;
        net_version = network_version;
        if (client_side_infos == null) {
            client_side_infos = new EmptyEnumerable<>();
        }
        if (server_side_infos == null) {
            server_side_infos = new EmptyEnumerable<>();
        }
    }

    @Override
    public void action(RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar(net_version);
        net_version = null;
        if (optional) { registrar = registrar.optional(); }
        registrar = registrar.executesOn(HandlerThread.MAIN); // always main thread.
        var client_en = client_side_infos.GetEnumerator();
        try {
            while (client_en.MoveNext()) {
                RegisterClientSidePacket(manager, registrar , client_en.getCurrent());
            }
        } finally {
            client_en.Dispose();
        }
        client_side_infos = null; // We have registered all the packets, we can clean this list now.
        var server_en = server_side_infos.GetEnumerator();
        try {
            while (server_en.MoveNext()) {
                RegisterServerSidePacket(manager, registrar, server_en.getCurrent());
            }
        } finally {
            server_en.Dispose();
        }
        server_side_infos = null; // We have registered all the packets, we can clean this list now.
        manager = null; // Unallocate network manager as well.
    }
}
