package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides to mod instances networking services. <br />
 * For the networking manager to become valid for a mod, that mod must configure the networking version the mod will operate on. <br />
 * To do that, the mod must have retrieved the network builder instance and configure it. <br />
 * Finally, mods using this must retain a reference to this object to dispatch networking events.
 */
public abstract class NetworkManager
{
    private INetworkBuilder builder;
    private boolean build_phase_completed;

    public NetworkManager() {
        build_phase_completed = false;
        builder = null;
    }

    protected abstract INetworkBuilder CreateNetworkBuilder();

    @NotNull
    public final INetworkBuilder GetBuilder()
        throws InvalidOperationException
    {
        if (build_phase_completed) {
            throw new InvalidOperationException("Building for this network manager has completed. Building cannot happen again.");
        }
        if (builder == null) {
            builder = CreateNetworkBuilder();
        }
        return builder;
    }

    /**
     * For the library support only. <br />
     * Destroys the network manager builder after getting it for configuring the network information to the mod loader.
     * @return The network builder instance.
     */
    @MaybeNull
    @ApiStatus.Internal
    public final INetworkBuilder GetBuilderAndDestroy() {
        INetworkBuilder nb = builder;
        builder = null;
        build_phase_completed = true;
        return nb;
    }

    public abstract <T extends CustomPacketPayload> void Reply(T message);

    public abstract <T extends CustomPacketPayload> void SendTo(Player player, T message);

    public abstract <T extends CustomPacketPayload> void SendToTracking(ServerLevel world, BlockPos pos, T message);

    public abstract <T extends CustomPacketPayload> void SendToTracking(Entity entity, T message);

    public abstract <T extends CustomPacketPayload> void SendToAllPlayers(MinecraftServer server, T message);

    public abstract <T extends CustomPacketPayload> void SendToServer(T message);
}
