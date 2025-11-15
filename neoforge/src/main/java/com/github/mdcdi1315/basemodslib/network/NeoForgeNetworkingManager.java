package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.menu.MenuProviderEx;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

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
        PacketDistributor.sendToServer(message);
    }

    private record WriteScreenDataTranslater(ServerPlayer sp, MenuProviderEx mpx)
            implements Action1<RegistryFriendlyByteBuf>
    {
        @Override
        public void action(RegistryFriendlyByteBuf obj) {
            mpx.WriteScreenOpeningData(sp , obj);
        }
    }

    @Override
    public void OpenMenu(Player player, MenuProvider provider)
            throws NotSupportedException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(player, "player");
        ArgumentNullException.ThrowIfNull(provider, "provider");

        if (player instanceof ServerPlayer sp) {
            if (provider instanceof MenuProviderEx mpx) {
                sp.openMenu(mpx , new WriteScreenDataTranslater(sp , mpx));
            } else {
                sp.openMenu(provider);
            }
        }
    }
}
