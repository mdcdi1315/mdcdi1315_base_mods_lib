package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.basemodslib.utils.annotations.MinecraftDependentFeature;

import net.minecraft.nbt.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Provides a Named Binary Tag encoder pipeline. <br />
 * Known issue: Any stream you provide will be implicitly
 * closed once you provide the stream to write the data into.
 */
@MinecraftDependentFeature
public final class NBTEncoderPipeline
    extends EncoderPipeline<Tag>
{
    private final boolean save_as_gzip;

    /**
     * Initializes a new instance of the {@link FBFEncoderPipeline} class.
     */
    public NBTEncoderPipeline() { this(true); }

    /**
     * Initializes a new instance of the {@link FBFEncoderPipeline} class.
     * @param save_as_gzip Whether all the produced streams/files should use GZIP compression or not.
     */
    public NBTEncoderPipeline(boolean save_as_gzip)
    {
        super(NbtOps.INSTANCE);
        this.save_as_gzip = save_as_gzip;
    }

    @Override
    protected void EncodeToStream(Tag value, OutputStream stream)
            throws IOException, SerializationException
    {
        try (
                OutputStream new_out = (save_as_gzip ? new GZIPOutputStream(stream) : stream);
                DataOutputStream dos = new DataOutputStream(new_out)
        ) {
            NbtIo.writeAnyTag(value, dos);
        }
    }
}
