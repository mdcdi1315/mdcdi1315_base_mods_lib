package com.github.mdcdi1315.basemodslib.network.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtAccounter;

import java.io.IOException;

/**
 * Provides an implementation of the {@link CodecBasedStreamCodec} class by using the Named Binary Tag format for encoding the data into bytes.
 * @param <T> The type of the object to be de/encoded.
 * @since 1.0.21
 */
public final class NBTCodecBasedStreamCodec<T>
    extends CodecBasedStreamCodec<T, Tag>
{
    /**
     * Constructs a new instance of the {@link NBTCodecBasedStreamCodec} class from the specified {@link Codec} that can de/encode the specified object of type {@link T}.
     * @param codec The {@link Codec} able to de/serialize objects of type {@link T}.
     * @throws ArgumentNullException {@code codec} is {@code null}.
     */
    public NBTCodecBasedStreamCodec(Codec<T> codec) throws ArgumentNullException { super(codec); }

    @Override
    protected DynamicOps<Tag> GetOpsToUse() { return NbtOps.INSTANCE; }

    @Override
    protected Tag LoadFromByteBuf(ByteBuf buf)
    {
        try (ByteBufInputStream bis = new ByteBufInputStream(buf, false)) {
            return NbtIo.readAnyTag(bis, NbtAccounter.create(2097152L));
        } catch (IOException ioex) {
            throw new DecoderException(ioex);
        }
    }

    @Override
    protected void SaveToByteBuf(ByteBuf buf, Tag value)
    {
        try (ByteBufOutputStream bos = new ByteBufOutputStream(buf)) {
            NbtIo.writeUnnamedTag(value, bos);
        } catch (IOException ioex) {
            throw new EncoderException(ioex);
        }
    }
}
