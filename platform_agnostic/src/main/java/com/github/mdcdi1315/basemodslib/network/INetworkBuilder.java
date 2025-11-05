package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Provides a builder instance for configuring your mod's networking details.
 */
public interface INetworkBuilder
{
    /**
     * Declares the network version you wish to use as a string. Typically this string is constant, just describing the version that your mod networking will run into.
     * @param str The version string to provide.
     * @throws ArgumentNullException {@code str} is {@code null}.
     * @throws FormatException {@code str} is not a valid version string. See {@link Version#Parse(String)} for more information.
     */
    default void DefineNetworkVersion(@ConstantExpected String str)
        throws ArgumentNullException, FormatException
    {
        ArgumentNullException.ThrowIfNull(str, "str");
        DefineNetworkVersion(Version.Parse(str));
    }

    /**
     * Defines the networking version that your mod will operate on.
     * @param ver The networking version of your mod networking.
     * @throws ArgumentNullException {@code ver} is {@code null}.
     */
    void DefineNetworkVersion(Version ver) throws ArgumentNullException;

    /**
     * Run this method during building to declare that your mod does not require the client side mod to have initialized the networking; <br />
     * that is, the mod does only dispatch server bound packets. <br />
     * NOTE: There is not a way to revert this intention from this builder instance, make sure that you this is what you want to do!
     */
    void DeclareClientOptionalPresence();

    /**
     * Run this method during building to declare that your mod does not require the server side mod to have initialized the networking; <br />
     * that is, the mod does only dispatch client bound packets. <br />
     * NOTE: There is not a way to revert this intention from this builder instance, make sure that you this is what you want to do!
     */
    void DeclareServerOptionalPresence();

    /**
     * Declares that the networking details of this mod are purely optional; <br />
     * that is, both sides can operate without having the networking initialized, or your networking is written with such a way that allows this. <br />
     * By default, this calls in both the {@link #DeclareClientOptionalPresence()} and the {@link #DeclareServerOptionalPresence()} methods. <br />
     * NOTE: There is not a way to revert this intention from this builder instance, make sure that you this is what you want to do! <br />
     * @implNote Implementations of this builder are free to override this method as they consider appropriate. However, the overridden method must strictly follow the semantics of the aforementioned methods.
     */
    default void DeclareOptionalNetworking() {
        DeclareClientOptionalPresence();
        DeclareServerOptionalPresence();
    }

    /**
     * Registers a packet that will be dispatched from the client to the server.
     * @param info The client-side packet registration information to use for registering the packet to use.
     * @param <T> The type of the packet to declare.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T extends CustomPacketPayload> void RegisterClientBoundPacket(ClientSideNetworkPacketRegistrationInfo<T> info) throws ArgumentNullException;

    /**
     * Registers a packet that will be dispatched from the server to the client.
     * @param info The server-side packet registration information to use for registering the packet to use.
     * @param <T> The type of the packet to declare.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T extends CustomPacketPayload> void RegisterServerBoundPacket(ServerSideNetworkPacketRegistrationInfo<T> info) throws ArgumentNullException;
}
