package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.ModdingEnvironment;
import com.github.mdcdi1315.basemodslib.menu.MenuProviderEx;

import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.event.network.CustomPayloadEvent;

public final class ForgeBasedNetworkManager
    extends NetworkManager
{
    private String mod_id;
    private SimpleChannel channel;

    public CustomPayloadEvent.Context cxt;

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
    public <T extends CustomPacketPayload> void Reply(T message) {
        if (cxt == null) {
            throw new InvalidOperationException("There is not a context to reply to");
        }

        channel.reply(message , cxt);
    }

    @Override
    public <T extends CustomPacketPayload> void SendTo(Player player, T message) {
        channel.send(message, PacketDistributor.PLAYER.with((ServerPlayer) player));
    }

    @Override
    public <T extends CustomPacketPayload> void SendToTracking(ServerLevel world, BlockPos pos, T message) {
        channel.send(message, PacketDistributor.TRACKING_CHUNK.with(world.getChunkAt(pos)));
    }

    @Override
    public <T extends CustomPacketPayload> void SendToTracking(Entity entity, T message) {
        channel.send(message, PacketDistributor.TRACKING_ENTITY.with(entity));
    }

    @Override
    public <T extends CustomPacketPayload> void SendToAllPlayers(MinecraftServer server, T message) {
        channel.send(message, PacketDistributor.ALL.noArg());
    }

    @Override
    public <T extends CustomPacketPayload> void SendToServer(T message)
    {
        if (BaseModsLib.GetEnvironment() == ModdingEnvironment.CLIENT) {
            SendToServerInternal(message);
        }
    }

    private <T extends CustomPacketPayload> void SendToServerInternal(T msg)
    {
        if (Minecraft.getInstance().getConnection() == null) {
            BaseModsLib.LOGGER.warn("NETWORKING: Not dispatching packet {} because we are not connected to a server!" , msg);
        }

        channel.send(msg , PacketDistributor.SERVER.noArg());
    }

    private record WriteScreenDataTranslater(ServerPlayer sp, MenuProviderEx mpx)
            implements Action1<FriendlyByteBuf>
    {
        @Override
        public void action(FriendlyByteBuf obj) { mpx.WriteScreenOpeningData(sp , obj); }
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
