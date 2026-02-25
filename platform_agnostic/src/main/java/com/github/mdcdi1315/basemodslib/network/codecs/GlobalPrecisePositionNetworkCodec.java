package com.github.mdcdi1315.basemodslib.network.codecs;

import com.github.mdcdi1315.basemodslib.network.NetworkHelpers;
import com.github.mdcdi1315.basemodslib.world.GlobalPrecisePosition;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.StreamCodec;

/**
 * Provides a {@link StreamCodec} for de/encoding {@link GlobalPrecisePosition} instances.
 * @since 1.0.18
 */
public final class GlobalPrecisePositionNetworkCodec
    extends GlobalPositionBaseNetworkCodec<GlobalPrecisePosition>
{
    public static final GlobalPrecisePositionNetworkCodec INSTANCE = new GlobalPrecisePositionNetworkCodec();

    @Override
    public GlobalPrecisePosition decode(ByteBuf buf) {
        var k = resource_key_codec.decode(buf);
        var p = NetworkHelpers.ReadVec3Unsafe(buf);
        return new GlobalPrecisePosition(k, p);
    }

    @Override
    public void encode(ByteBuf output, GlobalPrecisePosition p)
    {
        resource_key_codec.encode(output, p.level());
        NetworkHelpers.WritePositionUnsafe(output, p.position());
    }
}
