package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.utils.StringSupplier;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Defines a {@link Codec} implementation for de/encoding {@link IModLoaderRegistry} elements.
 * @param <TElement> The type of the registry's elements.
 */
public final class ModLoaderRegistryByNameCodec<TElement>
    implements Codec<TElement>
{
    private final IModLoaderRegistry<TElement> registry;

    /**
     * Creates a new instance of the {@link ModLoaderRegistryByNameCodec} class, with the specified registry object to read and write entries.
     * @param registry The registry object to use.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public ModLoaderRegistryByNameCodec(IModLoaderRegistry<TElement> registry)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(registry , "registry");
        this.registry = registry;
    }

    /**
     * Decodes a registry object from the data.
     * @param ops The data provider to use.
     * @param input The input state to use.
     * @return A result whether the object was decoded successfully or not.
     * @param <T> The input state type.
     */
    @Override
    @SuppressWarnings("all")
    public <T> DataResult<Pair<TElement, T>> decode(DynamicOps<T> ops, T input)
    {
        DataResult<Pair<ResourceLocation , T>> ld = ResourceLocation.CODEC.decode(ops , input);

        var err = ld.error();

        if (err.isPresent()) {
            return DataResult.error(new StringSupplier(err.get().message()));
        } else {
            var result = ld.result().get();

            Optional<TElement> opt = registry.GetElementValue(result.getFirst());

            return opt.isEmpty() ?
                    DataResult.error(StringSupplier.FromFormatted("Cannot find element %s in registry %s because it does not exist." , result.getFirst() , registry.GetRegistryKey())) :
                    DataResult.success(Pair.of(opt.get() , result.getSecond()));
        }
    }

    @Override
    public <T> DataResult<T> encode(TElement input, DynamicOps<T> ops, T prefix)
    {
        Optional<ResourceKey<TElement>> r = registry.GetResourceKey(input);

        return r.isEmpty() ?
                DataResult.error(StringSupplier.FromFormatted("Cannot find the element in the registry %s. Element: %s Hash code: %d" , registry.GetRegistryKey() , input , input.hashCode())) :
                ResourceLocation.CODEC.encode(r.get().location() , ops , prefix);
    }
}
