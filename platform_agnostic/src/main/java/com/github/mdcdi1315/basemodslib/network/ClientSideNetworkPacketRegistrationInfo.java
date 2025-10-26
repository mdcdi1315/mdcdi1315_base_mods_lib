package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.TypeDescriptor;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record ClientSideNetworkPacketRegistrationInfo<T>(
        @NotNull Action2<T, FriendlyByteBuf> encode_function,
        @NotNull Func2<FriendlyByteBuf, T> decode_function,
        @NotNull Action2<Player, T> handler
) {
    public ClientSideNetworkPacketRegistrationInfo {
        ArgumentNullException.ThrowIfNull(encode_function, "encode_function");
        ArgumentNullException.ThrowIfNull(decode_function, "decode_function");
        ArgumentNullException.ThrowIfNull(handler, "handler");
    }

    public Class<T> GetPacketClass() {
        return TypeDescriptor.DescribeTypeParameter();
    }
}
