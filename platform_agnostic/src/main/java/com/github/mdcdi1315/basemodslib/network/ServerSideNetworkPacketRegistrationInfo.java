package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record ServerSideNetworkPacketRegistrationInfo<T>(
        Class<T> cls,
        @NotNull Action2<T, FriendlyByteBuf> encode_function,
        @NotNull Func2<FriendlyByteBuf, T> decode_function,
        @NotNull Action2<ServerPlayer, T> handler
) {
    public ServerSideNetworkPacketRegistrationInfo {
        ArgumentNullException.ThrowIfNull(cls,"cls");
        ArgumentNullException.ThrowIfNull(encode_function, "encode_function");
        ArgumentNullException.ThrowIfNull(decode_function, "decode_function");
        ArgumentNullException.ThrowIfNull(handler, "handler");
    }
}
