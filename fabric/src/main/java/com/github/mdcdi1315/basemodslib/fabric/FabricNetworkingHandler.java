package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.Action3ToRunnable;
import com.github.mdcdi1315.basemodslib.network.ServerBoundModInfoPacket;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.HashMap;

// Manages mod networking negotiation in Fabric.
public final class FabricNetworkingHandler
    implements IDisposable
{
    private Map<String, Version> networking_versions_map;

    public FabricNetworkingHandler(Action3<FabricNetworkingHandler, ServerPlayer, ServerBoundModInfoPacket> on_packet_detected_action)
    {
        networking_versions_map = new HashMap<>();
        PayloadTypeRegistry.playC2S().register(ServerBoundModInfoPacket.TYPE , new ServerBoundModInfoPacket.NetCodec());
        ServerPlayNetworking.registerGlobalReceiver(
                ServerBoundModInfoPacket.TYPE,
                new ChannelHandler(on_packet_detected_action, this)
        );
    }

    private record ChannelHandler(
            Action3<FabricNetworkingHandler, ServerPlayer, ServerBoundModInfoPacket> action,
            FabricNetworkingHandler handler
    ) implements ServerPlayNetworking.PlayPayloadHandler<ServerBoundModInfoPacket>
    {
        @Override
        @SuppressWarnings("resource")
        public void receive(ServerBoundModInfoPacket payload, ServerPlayNetworking.Context context) {
            context.server().execute(new Action3ToRunnable<>(action, handler, context.player(), payload));
        }
    }

    public void RegisterVersionByPacket(ServerBoundModInfoPacket packet)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(packet, "packet");
        RegisterModNetworkingVersion(packet.Mod_ID, packet.Mod_Network_Version);
    }

    public void RegisterModNetworkingVersion(
            String mod_id,
            Version version
    ) throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mod_id, "mod_id");
        ArgumentNullException.ThrowIfNull(version, "version");
        networking_versions_map.put(mod_id, version);
    }

    @MaybeNull
    public Version LookupVersion(String mod_id)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mod_id, "mod_id");
        return networking_versions_map.get(mod_id);
    }

    @Override
    public void Dispose()
    {
        networking_versions_map = null;
        ServerPlayNetworking.unregisterGlobalReceiver(ServerBoundModInfoPacket.LOCATION);
    }
}
