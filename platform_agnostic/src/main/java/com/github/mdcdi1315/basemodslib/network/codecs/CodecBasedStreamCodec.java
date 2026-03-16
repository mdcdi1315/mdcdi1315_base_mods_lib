package com.github.mdcdi1315.basemodslib.network.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import net.minecraft.network.codec.StreamCodec;

/**
 * Provides a {@link StreamCodec} that wraps a {@link Codec} and performs de/encodes based on that one. <br />
 * Implementation varies based on the type of the object that the derived class encodes to, as well as how de/encoding is performed.
 * @param <T> Type of the object that is finally de/encoded.
 * @param <TOPS> Type of the object that handles the intermediary encoding.
 * @since 1.0.21
 */
public abstract class CodecBasedStreamCodec<T, TOPS>
        implements StreamCodec<ByteBuf, T>
{
    private final Codec<T> codec;

    public CodecBasedStreamCodec(Codec<T> codec)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.codec = codec, "codec");
    }

    /**
     * Gets the {@link DynamicOps} object to use for de/serialization to type {@link T}.
     * @return The {@link DynamicOps} object to use for de/serialization of {@link T}.
     */
    @NotNull
    protected abstract DynamicOps<TOPS> GetOpsToUse();

    /**
     * Loads an intermediary object of type {@link TOPS} from the specified byte buffer.
     * @param buf The byte buffer to load the object from.
     * @return The intermediary object to be transformed through the provided {@link Codec} instance.
     */
    @NotNull
    protected abstract TOPS LoadFromByteBuf(@DisallowNull ByteBuf buf);

    /**
     * Saves an intermediary object of type {@link TOPS} to the specified byte buffer.
     * @param buf The byte buffer to save the object to.
     * @param value The intermediary object of type {@link TOPS} to save.
     */
    protected abstract void SaveToByteBuf(@DisallowNull ByteBuf buf, @DisallowNull TOPS value);

    @NotNull
    @Override
    public final T decode(ByteBuf bbf)
    {
        TOPS o = LoadFromByteBuf(bbf);
        if (o == null) {
            throw new DecoderException("Can't decode the byte buffer!");
        } else {
            DataResult<Pair<T, TOPS>> dr = codec.decode(GetOpsToUse(), o);
            if (dr.isSuccess()) {
                return dr.result().get().getFirst();
            } else {
                throw new DecoderException("Can't decode the byte buffer: " + dr.error().get().message());
            }
        }
    }

    @Override
    public final void encode(ByteBuf output, T to_encode)
    {
        DataResult<TOPS> e = codec.encode(to_encode, GetOpsToUse(), GetOpsToUse().empty());
        if (e.isSuccess()) {
            SaveToByteBuf(output, e.result().get());
        } else {
            throw new EncoderException("Can't encode the byte buffer: " + e.error().get().message());
        }
    }

    @NotNull
    public final Codec<T> GetCodec() { return codec; }
}
