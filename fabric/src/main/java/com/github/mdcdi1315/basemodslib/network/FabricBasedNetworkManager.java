package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.menu.MenuProviderEx;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public final class FabricBasedNetworkManager
    extends NetworkManager
{
    private String mod_id;
    public Player Player_To_Reply_To;
    public ServerBoundModInfoPacket Mod_Info;
    public Map<Class<?> , PacketData<?>> Packet_IDs;

    public record PacketData<TP>(ResourceLocation location, Action2<TP , FriendlyByteBuf> packet_encoder)
    {
        public FriendlyByteBuf UnsafeEncode(Object obj) {
            FriendlyByteBuf buffer = PacketByteBufs.create();
            packet_encoder.action((TP)obj, buffer);
            return buffer;
        }
    }

    public FabricBasedNetworkManager(String mod_id) {
        this.mod_id = mod_id;
        Player_To_Reply_To = null;
        Packet_IDs = null;
        Mod_Info = null;
    }

    @Override
    protected INetworkBuilder CreateNetworkBuilder() {
        return new FabricNetworkBuilder(mod_id);
    }

    public void InitializeNetworkManager(@MaybeNull INetworkBuilder builder)
    {
        if (builder == null) {
            // Mod did not requested networking services, destroy
            mod_id = null;
            return;
        }
        ((FabricNetworkBuilder) builder).Build(this);
        mod_id = null;
    }

    @Override
    public <T> void Reply(T message)
    {
        if (Player_To_Reply_To == null) {
            throw new InvalidOperationException("There is not a context to reply to");
        }

        SendTo(Player_To_Reply_To, message);
    }

    @Override
    public <T> void SendTo(Player player, T message)
    {
        PacketData<?> data = Packet_IDs.get(message.getClass());

        if (data == null) {
            throw new ArgumentException("Attempted to send a non-existent packet type!");
        }

        ServerPlayNetworking.send((ServerPlayer) player, data.location, data.UnsafeEncode(message));
    }

    @Override
    public <T> void SendToTracking(ServerLevel world, BlockPos pos, T message)
    {
        PacketData<?> data = Packet_IDs.get(message.getClass());

        if (data == null) {
            throw new ArgumentException("Attempted to send a non-existent packet type!");
        }

        FriendlyByteBuf buffer = data.UnsafeEncode(message);

        for (ServerPlayer player : PlayerLookup.tracking(world, pos)) {
            ServerPlayNetworking.send(player, data.location, buffer);
        }
    }

    @Override
    public <T> void SendToTracking(Entity entity, T message)
    {
        PacketData<?> data = Packet_IDs.get(message.getClass());

        if (data == null) {
            throw new ArgumentException("Attempted to send a non-existent packet type!");
        }

        FriendlyByteBuf buffer = data.UnsafeEncode(message);

        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, data.location, buffer);
        }
    }

    @Override
    public <T> void SendToAllPlayers(MinecraftServer server, T message)
    {
        PacketData<?> data = Packet_IDs.get(message.getClass());

        if (data == null) {
            throw new ArgumentException("Attempted to send a non-existent packet type!");
        }

        FriendlyByteBuf buffer = data.UnsafeEncode(message);

        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, data.location, buffer);
        }
    }

    @Override
    public <T> void SendToServer(T message)
    {
        PacketData<?> data = Packet_IDs.get(message.getClass());

        if (data == null) {
            throw new ArgumentException("Attempted to send a non-existent packet type!");
        }

        ClientPlayNetworking.send(data.location , data.UnsafeEncode(message));
    }

    @Override
    public void OpenMenu(Player player, MenuProvider provider)
        throws ArgumentNullException , NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(player, "player");
        ArgumentNullException.ThrowIfNull(provider, "provider");

        if (player instanceof ServerPlayer sp) {
            sp.openMenu(provider instanceof MenuProviderEx mpx ? new ExtendedScreenFactoryTranslation(mpx) : provider);
        }
    }
}
