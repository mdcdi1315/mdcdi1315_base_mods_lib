package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.minecraftforge.network.*;

public final class ForgeSimpleChannelNetworkBuilder
    implements INetworkBuilder
{
    private boolean aso, aco;
    private Version network_version;
    private final ResourceLocation manager_channel_location;
    private List<ClientSideNetworkPacketRegistrationInfo<?>> client_packet_reg_info;
    private List<ServerSideNetworkPacketRegistrationInfo<?>> server_packet_reg_info;

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
        IEnumerator<ClientSideNetworkPacketRegistrationInfo<?>> client_e = client_packet_reg_info.GetEnumerator();
        try {
            while (client_e.MoveNext()) {

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

    public SimpleChannel Build(ForgeBasedNetworkManager manager)
    {
        var builder = ChannelBuilder.named(manager_channel_location);
        builder.networkProtocolVersion(NetworkHelpers.PackVersion(network_version == null ? new Version(1, 0) : network_version));
        if (aco) {
            builder.optionalClient();
        } else {

        }
        if (aso) {
            builder.optionalServer();
        }

        var sc = builder.simpleChannel();
        InitializePackets(sc , manager);
        return sc;
    }
}
