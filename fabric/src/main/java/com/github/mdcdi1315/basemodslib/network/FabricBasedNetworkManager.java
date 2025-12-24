package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.menu.MenuProviderEx;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class FabricBasedNetworkManager
    extends NetworkManager
{
    private String mod_id;
    public Player Player_To_Reply_To;
    public ServerBoundModInfoPacket Mod_Info;

    public FabricBasedNetworkManager(String mod_id) {
        this.mod_id = mod_id;
        Player_To_Reply_To = null;
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
    public <T extends CustomPacketPayload> void Reply(T message)
    {
        if (Player_To_Reply_To == null) {
            throw new InvalidOperationException("There is not a context to reply to");
        }

        SendTo(Player_To_Reply_To, message);
    }

    @Override
    public <T extends CustomPacketPayload> void SendTo(Player player, T message)
    {
        ServerPlayNetworking.send((ServerPlayer) player, message);
    }

    @Override
    public <T extends CustomPacketPayload> void SendToTracking(ServerLevel world, BlockPos pos, T message)
    {
        for (ServerPlayer player : PlayerLookup.tracking(world, pos)) {
            ServerPlayNetworking.send(player, message);
        }
    }

    @Override
    public <T extends CustomPacketPayload> void SendToTracking(Entity entity, T message)
    {
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, message);
        }
    }

    @Override
    public <T extends CustomPacketPayload> void SendToAllPlayers(MinecraftServer server, T message)
    {
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, message);
        }
    }

    @Override
    public <T extends CustomPacketPayload> void SendToServer(T message)
    {
        ClientPlayNetworking.send(message);
    }

    @Override
    public void OpenMenu(Player player, MenuProvider provider)
            throws NotSupportedException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(player, "player");
        ArgumentNullException.ThrowIfNull(provider, "provider");

        if (player instanceof ServerPlayer sp) {
            sp.openMenu(provider instanceof MenuProviderEx mpx ? new ExtendedScreenHandlerFactoryTranslation(mpx) : provider);
        }
    }
}
