package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.basemodslib.network.NetworkHelpers;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

public final class MenuCreaterExStreamCodec
        implements StreamCodec<RegistryFriendlyByteBuf, FriendlyByteBuf>
{
    private MenuCreaterExStreamCodec() {}

    public static final MenuCreaterExStreamCodec INSTANCE = new MenuCreaterExStreamCodec();

    @Override
    public FriendlyByteBuf decode(RegistryFriendlyByteBuf buffer)
    {
        FriendlyByteBuf ffb = new FriendlyByteBuf(Unpooled.buffer());
        try {
            NetworkHelpers.CopyBufferUnsafe(buffer, ffb);
        } catch (Exception ex) {
            // Reliably free the buffer on error
            ffb.release();
            throw ex;
        }
        return ffb;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, FriendlyByteBuf source_data)
    {
        try {
            NetworkHelpers.CopyBufferUnsafe(source_data, buffer);
        } finally {
            source_data.release();
        }
    }
}