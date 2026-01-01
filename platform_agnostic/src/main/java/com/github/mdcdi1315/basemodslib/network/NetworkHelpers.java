package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Networking helpers.
 */
public final class NetworkHelpers
{
    /**
     * Packs the specified version number into an integer. <br />
     * All components cannot be larger than 0xFF or 255; if they are the method will not throw an exception, but it will wrap them around.
     * @param v The {@link Version} to pack into an integer.
     * @return The packed version.
     * @throws ArgumentNullException {@code v} is {@code null}.
     */
    public static int PackVersion(Version v)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(v, "v");
        int build = v.Build();
        build = (build < 0) ? 0 : build & 0xFF;
        int revision = v.Revision();
        revision = (revision < 0) ? 0 : revision & 0xFF;
        return (v.Major() & 0xFF) << 24 | (v.Minor() & 0xFF) << 16 | build << 8 | revision;
    }

    /**
     * Unpacks a previously encoded {@link Version} value using the specified arbitrary integer value.
     * @param value The packed value to decode into a {@link Version} instance.
     * @return The unpacked version, previously packed with {@link #PackVersion(Version)}.
     */
    @NotNull
    public static Version UnpackVersion(int value) {
        return new Version((value >> 24) & 0xFF , (value >> 16) & 0xFF, (value >> 8) & 0xFF, value & 0xFF);
    }
}
