package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class NeoForgeNetworkingManager
    extends NetworkManager
{
    public IPayloadContext Context;

    @Override
    protected INetworkBuilder CreateNetworkBuilder() {
        return new NeoForgeNetworkBuilder(this);
    }

    @Override
    public <T extends CustomPacketPayload> void Reply(T message)
    {
        if (Context == null) {
            throw new InvalidOperationException("There is no context to reply to!");
        }

        Context.reply(message);
    }

    @Override
    public <T extends CustomPacketPayload> void SendTo(Player player, T message)
    {
        if (player instanceof ServerPlayer sp) {
            PacketDistributor.sendToPlayer(sp , message);
        }
    }

    @Override
    public <T extends CustomPacketPayload> void SendToTracking(ServerLevel world, BlockPos pos, T message) {
        PacketDistributor.sendToPlayersTrackingChunk(world , new ChunkPos(pos), message);
    }

    @Override
    public <T extends CustomPacketPayload> void SendToTracking(Entity entity, T message) {
        PacketDistributor.sendToPlayersTrackingEntity(entity,message);
    }

    @Override
    public <T extends CustomPacketPayload> void SendToAllPlayers(MinecraftServer server, T message) {
        PacketDistributor.sendToAllPlayers(message);
    }

    @Override
    public <T extends CustomPacketPayload> void SendToServer(T message) {
        ClientPacketDistributor.sendToServer(message);
    }
}
