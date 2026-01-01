package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides to mod instances networking services. <br />
 * For the networking manager to become valid for a mod, that mod must configure the networking version the mod will operate on. <br />
 * To do that, the mod must have retrieved the network builder instance and configure it. <br />
 * Finally, mods using this must retain a reference to this object to dispatch networking events.
 */
public abstract class NetworkManager
{
    // The networking builder instance is held temporarily by the manager, until
    // it is time for the manager to actually register the packets.
    // Once packet registration is done, this field becomes null
    // and remains in that way until the mod instance is disposed of.
    private INetworkBuilder builder;
    private boolean build_phase_completed;

    public NetworkManager() {
        build_phase_completed = false;
        builder = null;
    }

    /**
     * Creates the network builder to be used by mods. <br />
     * Called through the {@link #GetBuilder()} method.
     * @return An object that implements the {@link INetworkBuilder} interface.
     */
    @NotNull
    protected abstract INetworkBuilder CreateNetworkBuilder();

    /**
     * Gets a builder instance that allows to initialize a mod's networking. <br />
     * After the mod has successfully declared the packets that is going to dispatch
     * for the entire session, the current object can be then subsequently used to
     * dispatch packets as the mod wishes to.
     * @return An object that implements the {@link INetworkBuilder} interface.
     * @throws InvalidOperationException Building has already been done and was finalized by the mod loader layer. As such, building again is invalid operation.
     */
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

    /**
     * Replies to a previously sent packet. <br />
     * This is typically called only by the handler action specified in the {@link ClientSideNetworkPacketRegistrationInfo} instance during registration. <br />
     * For the reply mechanism to actually work, the packet to be dispatched must be a server-bound packet.
     * @param message The packet to reply with.
     * @param <T> The type of the packet to be dispatched.
     * @throws InvalidOperationException This method was not called from the {@link ClientSideNetworkPacketRegistrationInfo#handler()} method.
     */
    public abstract <T extends CustomPacketPayload> void Reply(T message) throws InvalidOperationException;

    /**
     * Sends to the specified player the specified message. <br />
     * Typically, this is dispatched by servers to the specified client,
     * and as such, this corresponds as sending a client-bound packet. <br />
     * See the {@link INetworkBuilder#RegisterClientBoundPacket(ClientSideNetworkPacketRegistrationInfo)} method for more information.
     * @param player The player to dispatch the specified packet to.
     * @param message The packet to be dispatched.
     * @param <T> The type of the packet to be dispatched.
     */
    public abstract <T extends CustomPacketPayload> void SendTo(Player player, T message);

    /**
     * Sends the specified packet to all the tracking entities in the specified level and position.
     * @param world The level to find it's currently tracking entities.
     * @param pos The position to filter out tracking entities.
     * @param message The message to dispatch to the tracking entities.
     * @param <T> The type of the message to send.
     */
    public abstract <T extends CustomPacketPayload> void SendToTracking(ServerLevel world, BlockPos pos, T message);

    /**
     * Sends to the specified tracking entity the specified packet.
     * @param entity The entity that this tracked and must be sent the specified packet.
     * @param message The message to dispatch to the tracking entity.
     * @param <T> The type of the message to send.
     */
    public abstract <T extends CustomPacketPayload> void SendToTracking(Entity entity, T message);

    /**
     * Sends the specified packet to the players in the specified server. <br />
     * This is dispatched by the servers. <br />
     * This corresponds as sending the specified client-bound packet to all the players. <br />
     * See the {@link INetworkBuilder#RegisterClientBoundPacket(ClientSideNetworkPacketRegistrationInfo)} method for more information.
     * @param server The Minecraft server to use to find the players and dispatch to them the specified message.
     * @param message The message to dispatch to all the players.
     * @param <T> The type of the message to send.
     */
    public abstract <T extends CustomPacketPayload> void SendToAllPlayers(MinecraftServer server, T message);

    /**
     * Sends to the server the specified message. <br />
     * This corresponds as sending a server-bound packet.
     * See the {@link INetworkBuilder#RegisterServerBoundPacket(ServerSideNetworkPacketRegistrationInfo)} method for more information.
     * @param message The message/packet to send.
     * @param <T> The type of the message to send.
     */
    public abstract <T extends CustomPacketPayload> void SendToServer(T message);

    /**
     * Dispatches to the network an 'open menu' packet.
     * @param player The player to dispatch the menu open packet to.
     * @param provider The menu provider to use.
     * @throws ArgumentNullException {@code player} and/or {@code provider} are {@code null}.
     * @throws NotSupportedException A menu provider of type {@link com.github.mdcdi1315.basemodslib.menu.MenuProviderEx} was used, but it is not supported by the underlying mod loader.
     */
    public abstract void OpenMenu(Player player, MenuProvider provider) throws NotSupportedException, ArgumentNullException;
}
