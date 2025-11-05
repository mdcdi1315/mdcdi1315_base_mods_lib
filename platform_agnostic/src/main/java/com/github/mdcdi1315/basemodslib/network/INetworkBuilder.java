package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Provides the primary means for configuring your mod network.
 */
public interface INetworkBuilder
{
    default void DefineNetworkVersion(String str)
        throws ArgumentNullException, FormatException
    {
        ArgumentNullException.ThrowIfNull(str, "str");
        DefineNetworkVersion(Version.Parse(str));
    }

    void DefineNetworkVersion(Version ver) throws ArgumentNullException;

    void AllowServerOnly();

    void AllowClientOnly();

    default void AllowBothClientAndServer() {
        AllowClientOnly();
        AllowServerOnly();
    }

    <T extends CustomPacketPayload> void RegisterClientBoundPacket(ClientSideNetworkPacketRegistrationInfo<T> info) throws ArgumentNullException;

    <T extends CustomPacketPayload> void RegisterServerBoundPacket(ServerSideNetworkPacketRegistrationInfo<T> info) throws ArgumentNullException;
}
