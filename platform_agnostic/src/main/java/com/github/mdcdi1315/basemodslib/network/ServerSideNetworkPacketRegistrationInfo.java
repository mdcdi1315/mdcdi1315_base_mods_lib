package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerSideNetworkPacketRegistrationInfo<T extends CustomPacketPayload>(
        Class<T> cls,
        @NotNull StreamCodec<RegistryFriendlyByteBuf , T> codec,
        @NotNull Action2<ServerPlayer, T> handler
) {
    public ServerSideNetworkPacketRegistrationInfo {
        ArgumentNullException.ThrowIfNull(cls,"cls");
        ArgumentNullException.ThrowIfNull(codec , "codec");
        ArgumentNullException.ThrowIfNull(handler, "handler");
    }
}
