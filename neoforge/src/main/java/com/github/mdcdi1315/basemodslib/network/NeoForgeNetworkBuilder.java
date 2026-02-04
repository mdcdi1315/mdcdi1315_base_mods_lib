package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.NeoForgeUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedList;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NeoForgeNetworkBuilder
    implements INetworkBuilder
{
    private boolean optional;
    private Version networking_version;
    private NeoForgeNetworkingManager manager;
    private SingleLinkedList<ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> client_side_infos;
    private SingleLinkedList<ServerSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> server_side_infos;

    public NeoForgeNetworkBuilder(NeoForgeNetworkingManager manager)
    {
        optional = false;
        this.manager = manager;
        client_side_infos = new SingleLinkedList<>();
        server_side_infos = new SingleLinkedList<>();
    }

    @Override
    public void DefineNetworkVersion(Version ver)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(ver, "ver");
        networking_version = ver;
    }

    // It seems that we cannot individually declare optionality,
    // so do the same on both cases.

    @Override
    public void DeclareClientOptionalPresence() {
        optional = true;
    }

    @Override
    public void DeclareServerOptionalPresence() {
        optional = true;
    }

    @Override
    public <T extends CustomPacketPayload> void RegisterClientBoundPacket(ClientSideNetworkPacketRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        client_side_infos.Add(info);
    }

    @Override
    public <T extends CustomPacketPayload> void RegisterServerBoundPacket(ServerSideNetworkPacketRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        server_side_infos.Add(info);
    }

    private record ActionWithManager<T1 , T2>(NeoForgeNetworkingManager manager, IPayloadContext cxt, Action2<T1 , T2> action , T1 in_1, T2 in_2)
        implements Runnable
    {
        @Override
        public void run() {
            manager.Context = cxt;
            action.action(in_1 , in_2);
            manager.Context = null;
        }
    }

    private record PayloadHandler_Client<T extends CustomPacketPayload>(NeoForgeNetworkingManager manager, Action2<Player , T> handler)
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

    public void Build(IEventBus event_bus) {
        NeoForgeUtils.AddListener(event_bus, RegisterPayloadHandlersEvent.class, this::RegisterPacketsEvent);
    }

    private void RegisterPacketsEvent(RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar((networking_version == null ? "1.0" : networking_version.toString()));
        if (optional) {
            registrar = registrar.optional();
        }
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
