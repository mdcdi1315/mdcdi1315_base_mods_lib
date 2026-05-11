package com.github.mdcdi1315.basemodslib.codecs;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.registries.ResourceLocationConstructionException;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Provides a {@link Codec} for {@link ResourceKey} instances. <br />
 * Unlike the game-provided codec, this one provides a detailed error message on decoding failures.
 * @param <T> The type of the registry item managed by the de/encoded resource key(s).
 * @since 1.0.31
 */
public final class ResourceKeyCodec<T>
    implements Codec<ResourceKey<T>>
{
    private final ResourceKey<? extends Registry<T>> registry_key;

    /**
     * Initializes a new instance of the {@link ResourceKeyCodec} class by specifying which registry the constructed objects will refer to.
     * @param registry_key The registry under which all the de/encoded objects will refer to.
     * @throws ArgumentNullException {@code registry_key} is {@code null}.
     */
    public ResourceKeyCodec(ResourceKey<? extends Registry<T>> registry_key)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.registry_key = registry_key, "registry_key");
    }

    @Override
    public <TO> DataResult<Pair<ResourceKey<T>, TO>> decode(DynamicOps<TO> ops, TO input)
    {
        DataResult<String> s = ops.getStringValue(input);

        if (s.isError()) {
            return DataResult.error(s.error().get().messageSupplier());
        } else {
            try {
                return DataResult.success(
                        Pair.of(
                                RegistryUtils.ParseResourceKey(s.result().get(), registry_key),
                                input
                        )
                );
            } catch (ResourceLocationConstructionException e) {
                return CodecUtils.CreateDotNetFormattedErrorDataResult("Error parsing resource location\n{0}: {1}", e.getMessage(), e.GetCause().getMessage());
            }
        }
    }

    @Override
    public <TO> DataResult<TO> encode(ResourceKey<T> input, DynamicOps<TO> ops, TO prefix)
    {
        return DataResult.success(
                ops.createString(input.location().toString())
        );
    }
}
