package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.MinecraftDependentFeature;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtAccounter;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.util.zip.GZIPInputStream;

/**
 * Provides a Named Binary Tag decoder pipeline. <br />
 * Known issue: Any stream you provide will be implicitly
 * closed once you provide the stream to read the data from.
 */
@MinecraftDependentFeature
public final class NBTDecoderPipeline
    extends DecoderPipeline<Tag>
{
    private final boolean saved_as_gzip;
    private final NbtAccounter accounter;

    /**
     * Initializes a new instance of the {@link NBTDecoderPipeline} class.
     */
    public NBTDecoderPipeline() { this(null, true); }

    /**
     * Initializes a new instance of the {@link FBFEncoderPipeline} class.
     * @param are_saved_as_gzip Whether all the read streams/files are using GZIP compression or not.
     * @param accounter The NBT accounter to use for controlling the deserialization limits. If {@code null}, the default quota are used.
     */
    public NBTDecoderPipeline(@AllowNull NbtAccounter accounter, boolean are_saved_as_gzip)
    {
        super(NbtOps.INSTANCE);
        this.saved_as_gzip = are_saved_as_gzip;
        this.accounter = (accounter == null) ? NbtAccounter.defaultQuota() : accounter;
    }

    @Override
    protected Tag DecodeFromStream(InputStream stream)
            throws IOException, SerializationException
    {
        try (
                var data_1 = saved_as_gzip ? new GZIPInputStream(stream) : stream;
                var data_2 = new DataInputStream(data_1)
        ) {
            return NbtIo.readAnyTag(data_2, accounter);
        }
    }
}
