package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.network.FriendlyByteBuf;

/**
 * A special packet class used for dispatching the network versions for a given mod - dispatched once a player connects to a server. <br />
 * These are then verified on the server. <br />
 * Users of this class must actually provide the method that verifies the version correctness on server side.
 */
public final class ServerBoundModInfoPacket
{
    private static final byte ALLOW_FLAG_CLIENT = 1 << 0, ALLOW_FLAG_SERVER = 1 << 1;

    public String Mod_ID;
    public Version Mod_Network_Version;
    private byte Allow_Flags;

    /**
     * Gets a value whether is not required for the client to have the specified mod networking version.
     * @return A value whether is not required for the client to have the specified mod networking version.
     */
    public boolean AllowedOnClient() {
        return (Allow_Flags & ALLOW_FLAG_CLIENT) != 0;
    }

    /**
     * Gets a value whether is not required for the server to have the specified mod networking version.
     * @return A value whether is not required for the server to have the specified mod networking version.
     */
    public boolean AllowedOnServer() {
        return (Allow_Flags & ALLOW_FLAG_SERVER) != 0;
    }

    private ServerBoundModInfoPacket() {}

    public ServerBoundModInfoPacket(String mod_id, String network_version, boolean client, boolean server)
            throws ArgumentException
    {
        ArgumentNullException.ThrowIfNull(mod_id, "mod_id");
        Mod_ID = mod_id;
        Mod_Network_Version = Version.Parse(network_version);
        if (client) {
            Allow_Flags |= ALLOW_FLAG_CLIENT;
        }
        if (server) {
            Allow_Flags |= ALLOW_FLAG_SERVER;
        }
    }

    public ServerBoundModInfoPacket(String mod_id, Version network_version, boolean client, boolean server)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mod_id, "mod_id");
        ArgumentNullException.ThrowIfNull(network_version, "network_version");
        Mod_ID = mod_id;
        Mod_Network_Version = network_version;
        if (client) {
            Allow_Flags |= ALLOW_FLAG_CLIENT;
        }
        if (server) {
            Allow_Flags |= ALLOW_FLAG_SERVER;
        }
    }

    public static void Encode(ServerBoundModInfoPacket p, FriendlyByteBuf buffer)
    {
        buffer.writeUtf(p.Mod_ID);
        buffer.writeUtf(p.Mod_Network_Version.toString());
        buffer.writeByte(p.Allow_Flags);
    }

    public static ServerBoundModInfoPacket Decode(FriendlyByteBuf buffer)
    {
        ServerBoundModInfoPacket p = new ServerBoundModInfoPacket();
        p.Mod_ID = buffer.readUtf();
        p.Mod_Network_Version = Version.Parse(buffer.readUtf());
        p.Allow_Flags = buffer.readByte();
        return p;
    }
}
