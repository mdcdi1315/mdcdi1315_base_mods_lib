package com.github.mdcdi1315.basemodslib.network.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.fastbinaryformat.BinaryFormatEntry;
import com.github.mdcdi1315.basemodslib.fastbinaryformat.FastBinaryFormatIO;
import com.github.mdcdi1315.basemodslib.fastbinaryformat.FastBinaryFormatOps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;

import io.netty.buffer.ByteBuf;

/**
 * Provides an implementation of the {@link CodecBasedStreamCodec} class by using the Fast Binary Format for encoding the data into bytes.
 * @param <T> The type of the object to be de/encoded.
 * @since 1.0.21
 */
public final class FBFCodecBasedStreamCodec<T>
    extends CodecBasedStreamCodec<T, BinaryFormatEntry>
{
    private final boolean use_gzip;

    /**
     * Constructs a new instance of the {@link FBFCodecBasedStreamCodec} class from the specified {@link Codec} that can de/encode the specified object of type {@link T}.
     * @param codec The {@link Codec} able to de/serialize objects of type {@link T}.
     * @throws ArgumentNullException {@code codec} is {@code null}.
     */
    public FBFCodecBasedStreamCodec(Codec<T> codec) throws ArgumentNullException { super(codec); use_gzip = false; }

    /**
     * Constructs a new instance of the {@link FBFCodecBasedStreamCodec} class from the specified {@link Codec} that can de/encode the specified object of type {@link T}.
     * @param codec The {@link Codec} able to de/serialize objects of type {@link T}.
     * @param use_gzip A value whether Gzip compression should be used as well.
     * @throws ArgumentNullException {@code codec} is {@code null}.
     */
    public FBFCodecBasedStreamCodec(Codec<T> codec, boolean use_gzip) throws ArgumentNullException { super(codec); this.use_gzip = use_gzip; }

    @Override
    protected DynamicOps<BinaryFormatEntry> GetOpsToUse() { return FastBinaryFormatOps.INSTANCE; }

    @Override
    protected BinaryFormatEntry LoadFromByteBuf(ByteBuf buf)
    {
        return use_gzip ?
                FastBinaryFormatIO.LoadGZIPCompressedFromNettyBuffer(buf) :
                FastBinaryFormatIO.LoadFromNettyBuffer(buf);
    }

    @Override
    protected void SaveToByteBuf(ByteBuf buf, BinaryFormatEntry value)
    {
        if (use_gzip) {
            FastBinaryFormatIO.SaveGZIPCompressedToNettyBuffer(buf, value);
        } else {
            FastBinaryFormatIO.SaveToNettyBuffer(buf, value);
        }
    }
}
