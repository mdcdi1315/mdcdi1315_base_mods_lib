package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public final class MinecraftWrappedModLoaderRegistry<T>
    implements IModLoaderRegistry<T>
{
    private Registry<T> reg;

    public MinecraftWrappedModLoaderRegistry(Registry<T> registry)
    {
        ArgumentNullException.ThrowIfNull(registry , "registry");
        reg = registry;
    }

    @Override
    public boolean ContainsKey(ResourceLocation location) {
        return reg.containsKey(location);
    }

    @Override
    public void Register(ResourceLocation location, T value) throws ArgumentNullException {
        Registry.register(reg , location , value);
    }

    @Override
    public ResourceKey<Registry<T>> GetRegistryKey() {
        return (ResourceKey<Registry<T>>) reg.key();
    }

    @Override
    public Set<ResourceLocation> GetEntryKeys() {
        return reg.keySet();
    }

    @Override
    public Optional<T> GetElementValue(ResourceLocation location) throws ArgumentNullException {
        return reg.getOptional(location);
    }

    @Override
    public Optional<ResourceKey<T>> GetResourceKey(T element) throws ArgumentNullException {
        return reg.getResourceKey(element);
    }

    private static <T> MinecraftWrappedITag<T> TagMapper(HolderSet.Named<T> set) {
        return new MinecraftWrappedITag<>(set);
    }

    @Override
    public Stream<ITag<T>> GetTags() {
        return reg.getTags().map(MinecraftWrappedModLoaderRegistry::TagMapper);
    }

    @Override
    public ITag<T> GetOrBindTag(TagKey<T> key)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key , "key");
        var t = reg.get(key);
        if (t.isEmpty()) {
            if (reg instanceof WritableRegistry<T> d) {
                d.bindTag(key , new ArrayList<>(0));
            }
            return new MinecraftWrappedITag<>(reg.getOrThrow(key));
        } else {
            return new MinecraftWrappedITag<>(t.get());
        }
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return reg.iterator();
    }
}