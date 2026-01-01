package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Provides the information required so that a client can dispatch a packet to a server.
 * @param cls The class of the packet that is being registered.
 * @param type The type of the packet that is being registered.
 * @param codec A {@link StreamCodec} that is able to encode the packet from the client in order to be decoded by the server.
 * @param handler The function that, upon invoking it, it does provide the server-side player caused the dispatch as well as the packet that was dispatched.
 * @param <T> The type of the packet to be sent to the server.
 */
public record ServerSideNetworkPacketRegistrationInfo<T extends CustomPacketPayload>(
        @NotNull Class<T> cls,
        @NotNull CustomPacketPayload.Type<T> type,
        @NotNull StreamCodec<RegistryFriendlyByteBuf , T> codec,
        @NotNull Action2<ServerPlayer, T> handler
) {
    /**
     * Initializes a new instance of the {@link ServerSideNetworkPacketRegistrationInfo} class.
     * @param cls The class of the packet that is being registered.
     * @param type The type of the packet that is being registered.
     * @param codec A {@link StreamCodec} that is able to encode the packet from the client in order to be decoded by the server.
     * @param handler The function that, upon invoking it, it does provide the server-side player caused the dispatch as well as the packet that was dispatched.
     * @throws ArgumentNullException {@code cls} and/or {@code type} and/or {@code codec} and/or {@code handler} are {@code null}.
     */
    public ServerSideNetworkPacketRegistrationInfo {
        ArgumentNullException.ThrowIfNull(cls,"cls");
        ArgumentNullException.ThrowIfNull(type, "type");
        ArgumentNullException.ThrowIfNull(codec , "codec");
        ArgumentNullException.ThrowIfNull(handler, "handler");
    }
}
