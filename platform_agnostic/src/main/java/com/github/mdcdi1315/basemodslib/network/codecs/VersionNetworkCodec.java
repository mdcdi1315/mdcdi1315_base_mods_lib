package com.github.mdcdi1315.basemodslib.network.codecs;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Provides a {@link StreamCodec} implementation for de/encoding {@link Version} objects from/to the network.
 * @since 1.0.14
 */
public final class VersionNetworkCodec
    implements StreamCodec<FriendlyByteBuf, Version>
{
    private VersionNetworkCodec() {}

    /**
     * Gets the single and only instance of the {@link VersionNetworkCodec} class.
     */
    public static final VersionNetworkCodec INSTANCE = new VersionNetworkCodec();

    // Version format will be as follows:
    // byte FIELD_COUNT
    // int MAJOR
    // int MINOR
    // [int BUILD]
    // [int REVISION]
    // BUILD and/or REVISION do not always exist, depending on whether the FIELD_COUNT is 0, 1 or 2.
    // FIELD_COUNT == 0 : Only MAJOR and MINOR fields are defined.
    // FIELD_COUNT == 1 : Only MAJOR, MINOR and BUILD fields are defined.
    // FIELD_COUNT == 2 : All fields are defined.
    // To be further assured of format issues due to the underlying CPU endianness, all the numbers will be encoded as little-endian.

    @Override
    public Version decode(FriendlyByteBuf buffer) {
        byte n_fields = buffer.readByte();
        int major = buffer.readIntLE();
        int minor = buffer.readIntLE();
        if (n_fields == 0) {
            return new Version(major, minor);
        } else if (n_fields > 0) {
            int build = buffer.readIntLE();
            if (n_fields == 2) {
                int revision = buffer.readIntLE();
                return new Version(major, minor, build, revision);
            } else {
                return new Version(major, minor, build);
            }
        } else {
            // Unreachable case.
            throw new ArgumentException(StringUtils.Format("Invalid n_fields field value in buffer: {0}" , n_fields));
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer, Version version) {
        int build = version.Build();
        int revision = version.Revision();
        byte n_fields = 0;
        if (build > -1) { n_fields++; }
        if (revision > -1) { n_fields++; }
        buffer.writeByte(n_fields);
        buffer.writeIntLE(version.Major());
        buffer.writeIntLE(version.Minor());
        if (build > -1) {
            buffer.writeIntLE(build);
        }
        if (revision > -1) {
            buffer.writeIntLE(revision);
        }
    }
}
