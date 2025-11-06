package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientSideNetworkPacketRegistrationInfo<T extends CustomPacketPayload>(
        @NotNull Class<T> cls,
        @NotNull CustomPacketPayload.Type<T> type,
        @NotNull StreamCodec<RegistryFriendlyByteBuf, T> codec,
        @NotNull Action2<Player, T> handler
) {
    public ClientSideNetworkPacketRegistrationInfo {
        ArgumentNullException.ThrowIfNull(cls, "cls");
        ArgumentNullException.ThrowIfNull(type, "type");
        ArgumentNullException.ThrowIfNull(codec, "codec");
        ArgumentNullException.ThrowIfNull(handler, "handler");
    }
}
