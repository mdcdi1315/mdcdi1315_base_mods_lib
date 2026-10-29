package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.mojang.datafixers.util.Pair;

import net.minecraft.tags.TagKey;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Provides an {@link IModLoaderRegistry} implementation by wrapping {@link Registry} objects.
 * @param <T> The type of the items that are managed by the mod loader registry.
 */
public final class MinecraftWrappedModLoaderRegistry<T>
    implements IModLoaderRegistry<T>
{
    private final Registry<T> reg;

    /**
     * Initializes a new instance of the {@link MinecraftWrappedModLoaderRegistry} class by the specified Minecraft registry object to wrap.
     * @param registry The registry object to be wrapped.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public MinecraftWrappedModLoaderRegistry(Registry<T> registry)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(registry , "registry");
        reg = registry;
    }

    @Override
    public Iterator<T> iterator() { return reg.iterator(); }

    @Override
    public Set<ResourceLocation> GetEntryKeys() { return reg.keySet(); }

    @Override
    public boolean ContainsKey(ResourceLocation location) { return reg.containsKey(location); }

    @Override
    @SuppressWarnings("unchecked")
    public ResourceKey<Registry<T>> GetRegistryKey() { return (ResourceKey<Registry<T>>) reg.key(); }

    @Override
    public Stream<ITag<T>> GetTags() { return reg.getTags().map(MinecraftWrappedModLoaderRegistry::TagMapper); }

    private static <T> MinecraftWrappedITag<T> TagMapper(Pair<TagKey<T>, HolderSet.Named<T>> pair) { return new MinecraftWrappedITag<>(pair.getSecond()); }

    @Override
    public void Register(ResourceLocation location, T value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(value, "value");
        ArgumentNullException.ThrowIfNull(location, "location");
        Registry.register(reg , location , value);
    }

    @NotNull
    @Override
    public Optional<T> GetElementValue(ResourceLocation location)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(location, "location");
        return reg.getOptional(location);
    }

    @NotNull
    @Override
    public Optional<ResourceKey<T>> GetResourceKey(T element)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(element, "element");
        return reg.getResourceKey(element);
    }

    @NotNull
    @Override
    public ITag<T> GetOrBindTag(TagKey<T> key)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key , "key");
        return new MinecraftWrappedITag<>(reg.getOrCreateTag(key));
    }
}