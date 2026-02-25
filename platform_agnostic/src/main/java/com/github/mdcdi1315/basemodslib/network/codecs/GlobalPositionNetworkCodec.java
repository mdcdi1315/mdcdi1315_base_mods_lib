package com.github.mdcdi1315.basemodslib.network.codecs;

import com.github.mdcdi1315.basemodslib.world.GlobalPosition;
import com.github.mdcdi1315.basemodslib.network.NetworkHelpers;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.StreamCodec;

/**
 * Provides a {@link StreamCodec} for de/encoding {@link GlobalPosition} instances.
 * @since 1.0.18
 */
public final class GlobalPositionNetworkCodec
    extends GlobalPositionBaseNetworkCodec<GlobalPosition>
{
    /**
     * Gets the single and only instance of the {@link GlobalPositionNetworkCodec} class.
     */
    public static final GlobalPositionNetworkCodec INSTANCE = new GlobalPositionNetworkCodec();

    @Override
    public GlobalPosition decode(ByteBuf buf)
    {
        var rk = resource_key_codec.decode(buf);
        var p = NetworkHelpers.ReadBlockPosUnsafe(buf);
        return new GlobalPosition(rk, p);
    }

    @Override
    public void encode(ByteBuf o, GlobalPosition pos)
    {
        resource_key_codec.encode(o, pos.level());
        NetworkHelpers.WriteVec3iUnsafe(o, pos.position());
    }
}
