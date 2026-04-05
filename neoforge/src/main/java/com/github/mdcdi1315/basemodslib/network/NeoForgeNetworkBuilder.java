package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.NeoForgeUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NeoForgeNetworkBuilder
    implements INetworkBuilder
{
    private boolean optional;
    private Version networking_version;
    private NeoForgeNetworkingManager manager;
    private SingleLinkedListBasedRegister<ClientSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> client_side_infos;
    private SingleLinkedListBasedRegister<ServerSideNetworkPacketRegistrationInfo<? extends CustomPacketPayload>> server_side_infos;

    public NeoForgeNetworkBuilder(NeoForgeNetworkingManager manager)
    {
        optional = false;
        this.manager = manager;
        client_side_infos = new SingleLinkedListBasedRegister<>();
        server_side_infos = new SingleLinkedListBasedRegister<>();
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
    public void DeclareClientOptionalPresence() { optional = true; }

    @Override
    public void DeclareServerOptionalPresence() { optional = true; }

    @Override
    public <T extends CustomPacketPayload> void RegisterClientBoundPacket(ClientSideNetworkPacketRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        client_side_infos.Register(info);
    }

    @Override
    public <T extends CustomPacketPayload> void RegisterServerBoundPacket(ServerSideNetworkPacketRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        server_side_infos.Register(info);
    }

    public void Build(IEventBus event_bus)
    {
        NeoForgeUtils.AddListener(
                event_bus,
                RegisterPayloadHandlersEvent.class,
                new NeoForgeNetworkBuilderTranslator(
                        optional,
                        networking_version == null ? "1.0" : networking_version.toString(),
                        manager,
                        client_side_infos.HasItems() ? client_side_infos : null,
                        server_side_infos.HasItems() ? server_side_infos : null
                )
        );
        manager = null;
        client_side_infos = null;
        server_side_infos = null;
        networking_version = null;
    }
}
