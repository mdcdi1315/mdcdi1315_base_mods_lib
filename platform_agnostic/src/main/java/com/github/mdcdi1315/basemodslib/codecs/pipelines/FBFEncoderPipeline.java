package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.basemodslib.fastbinaryformat.BinaryFormatEntry;
import com.github.mdcdi1315.basemodslib.fastbinaryformat.FastBinaryFormatIO;
import com.github.mdcdi1315.basemodslib.fastbinaryformat.FastBinaryFormatOps;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Provides a Fast Binary Format encoder pipeline.
 */
public final class FBFEncoderPipeline
    extends EncoderPipeline<BinaryFormatEntry>
{
    private final boolean save_as_gzip;

    /**
     * Initializes a new instance of the {@link FBFEncoderPipeline} class.
     */
    public FBFEncoderPipeline() { this(true); }

    /**
     * Initializes a new instance of the {@link FBFEncoderPipeline} class.
     * @param save_as_gzip Whether all the produced streams/files should use GZIP compression or not.
     */
    public FBFEncoderPipeline(boolean save_as_gzip)
    {
        super(FastBinaryFormatOps.INSTANCE);
        this.save_as_gzip = save_as_gzip;
    }

    @Override
    protected void EncodeToStream(BinaryFormatEntry value, OutputStream stream)
            throws IOException, SerializationException
    {
        if (save_as_gzip) {
            FastBinaryFormatIO.SaveGZIPCompressed(stream, value);
        } else {
            FastBinaryFormatIO.Save(stream, value);
        }
    }
}
