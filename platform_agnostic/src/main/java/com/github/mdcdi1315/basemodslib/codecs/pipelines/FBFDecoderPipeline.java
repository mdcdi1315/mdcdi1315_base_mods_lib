package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.github.mdcdi1315.basemodslib.fastbinaryformat.BinaryFormatEntry;
import com.github.mdcdi1315.basemodslib.fastbinaryformat.FastBinaryFormatIO;
import com.github.mdcdi1315.basemodslib.fastbinaryformat.FastBinaryFormatOps;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides a Fast Binary Format decoder pipeline.
 */
public final class FBFDecoderPipeline
    extends DecoderPipeline<BinaryFormatEntry>
{
    private final boolean saved_as_gzip;

    /**
     * Initializes a new instance of the {@link FBFDecoderPipeline} class.
     */
    public FBFDecoderPipeline() { this(true); }

    /**
     * Initializes a new instance of the {@link FBFEncoderPipeline} class.
     * @param are_saved_as_gzip Whether all the read streams/files are using GZIP compression or not.
     */
    public FBFDecoderPipeline(boolean are_saved_as_gzip)
    {
        super(FastBinaryFormatOps.INSTANCE);
        this.saved_as_gzip = are_saved_as_gzip;
    }

    @Override
    protected BinaryFormatEntry DecodeFromStream(InputStream stream)
            throws IOException, SerializationException
    {
        if (saved_as_gzip) {
            return FastBinaryFormatIO.LoadGZIPCompressed(stream);
        } else {
            return FastBinaryFormatIO.Load(stream);
        }
    }
}
