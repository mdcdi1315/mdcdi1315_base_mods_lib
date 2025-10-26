package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.ModdingEnvironment;
import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;

import net.minecraft.client.Minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public final class ForgeBasedNetworkManager
    extends NetworkManager
{
    private String mod_id;
    private SimpleChannel channel;

    public NetworkEvent.Context cxt;

    public ForgeBasedNetworkManager(String mod_id) {
        this.mod_id = mod_id;
        cxt = null;
        channel = null;
    }

    @Override
    protected INetworkBuilder CreateNetworkBuilder() {
        return new ForgeSimpleChannelNetworkBuilder(mod_id);
    }

    public void InitializeNetworkManager(@MaybeNull INetworkBuilder builder)
    {
        if (builder == null) {
            // Mod did not requested networking services, destroy
            mod_id = null;
            return;
        }
        ForgeSimpleChannelNetworkBuilder b = (ForgeSimpleChannelNetworkBuilder)builder;
        channel = b.Build(this);
        mod_id = null;
    }

    @Override
    public <T> void Reply(T message) {
        if (cxt == null) {
            throw new InvalidOperationException("There is not a context to reply to");
        }

        channel.reply(message , cxt);
    }

    @Override
    public <T> void SendTo(Player player, T message) {
        channel.send(PacketDistributor.PLAYER.with(new ElementSupplier<>((ServerPlayer) player)), message);
    }

    private record LevelChunkSupplier(ServerLevel world, BlockPos position)
        implements Supplier<LevelChunk>
    {
        @Override
        public LevelChunk get() {
            return world.getChunkAt(position);
        }
    }

    @Override
    public <T> void SendToTracking(ServerLevel world, BlockPos pos, T message) {
        channel.send(PacketDistributor.TRACKING_CHUNK.with(new LevelChunkSupplier(world, pos)), message);
    }

    @Override
    public <T> void SendToTracking(Entity entity, T message) {
        channel.send(PacketDistributor.TRACKING_ENTITY.with(new ElementSupplier<>(entity)), message);
    }

    @Override
    public <T> void SendToAllPlayers(MinecraftServer server, T message) {
        channel.send(PacketDistributor.ALL.noArg(), message);
    }

    @Override
    public <T> void SendToServer(T message)
    {
        if (BaseModsLib.GetEnvironment() == ModdingEnvironment.CLIENT) {
            SendToServerInternal(message);
        }
    }

    private <T> void SendToServerInternal(T msg)
    {
        if (Minecraft.getInstance().getConnection() == null) {
            BaseModsLib.LOGGER.info("NETWORKING: Not dispatching packet {} because we are not connected to a server!" , msg);
        }

        channel.sendToServer(msg);
    }
}
