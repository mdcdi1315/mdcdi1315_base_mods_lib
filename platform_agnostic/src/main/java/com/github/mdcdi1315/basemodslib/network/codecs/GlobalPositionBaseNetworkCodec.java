package com.github.mdcdi1315.basemodslib.network.codecs;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.StreamCodec;

import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides the base scaffolding for the {@link GlobalPositionNetworkCodec} and {@link GlobalPrecisePositionNetworkCodec} classes.
 * @param <T> The type of the global position to be de/encoded.
 * @since 1.0.18
 * @apiNote mdcdi1315 reserves the right to modify this class as deem appropriate. Do not use it by your own code.
 */
@ApiStatus.Internal
public abstract class GlobalPositionBaseNetworkCodec<T>
    implements StreamCodec<ByteBuf, T>
{
    /**
     * Provides a {@link StreamCodec} that can de/encode {@link ResourceKey}s of {@link Registries#DIMENSION} key.
     */
    protected static final StreamCodec<ByteBuf, ResourceKey<Level>> resource_key_codec;

    static {
        resource_key_codec = ResourceKey.streamCodec(Registries.DIMENSION);
    }
}
