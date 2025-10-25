package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;
import java.util.stream.Stream;

public class ForgeRegistryWrappedInRegistry<T>
    implements IModLoaderRegistry<T>
{
    private final IForgeRegistry<T> reg;

    public ForgeRegistryWrappedInRegistry(IForgeRegistry<T> fg)
    {
        ArgumentNullException.ThrowIfNull(fg , "fg");
        reg = fg;
    }

    @Override
    public boolean ContainsKey(ResourceLocation location) {
        return reg.containsKey(location);
    }

    @Override
    public Codec<T> GetSerializationCodec() {
        return reg.getCodec();
    }

    @Override
    public void Register(ResourceLocation location, T value)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(location , "location");
        ArgumentNullException.ThrowIfNull(value , "value");
        reg.register(location , value);
    }

    @Override
    public ResourceKey<Registry<T>> GetRegistryKey() {
        return reg.getRegistryKey();
    }

    @Override
    public Set<ResourceLocation> GetEntryKeys() {
        return reg.getKeys();
    }

    @Override
    public Optional<ResourceKey<T>> GetResourceKey(T element) throws ArgumentNullException {
        ArgumentNullException.ThrowIfNull(element , "element");
        return reg.getResourceKey(element);
    }

    @Override
    public Iterator<T> iterator() {
        return reg.iterator();
    }

    @Override
    public int GetSize() {
        return reg.getKeys().size();
    }

    @Override
    public Optional<T> GetElementValue(ResourceLocation location)
            throws ArgumentNullException
    {
        return Optional.ofNullable(reg.getValue(location));
    }

    @Override
    public Stream<ITag<T>> GetTags() {
        var t = reg.tags();
        if (t == null)
        {
            throw new InvalidOperationException("Cannot retrieve the Forge tag manager for this registry object");
        }
        return t.stream().map(ForgeRegistryTag::new);
    }

    @Override
    public ITag<T> GetOrBindTag(TagKey<T> key)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        var t = reg.tags();
        if (t == null)
        {
            throw new InvalidOperationException("Cannot retrieve the Forge tag manager for this registry object");
        }
        return new ForgeRegistryTag<>(t.getTag(key));
    }
}
