package com.github.mdcdi1315.basemodslib.registries;

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
    public FriendlyByteBuf decode(RegistryFriendlyByteBuf buffer) {
        FriendlyByteBuf ffb = new FriendlyByteBuf(Unpooled.buffer());
        buffer.readBytes(ffb);
        return ffb;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, FriendlyByteBuf source_data) {
        buffer.writeBytes(source_data);
        source_data.release();
    }
}