package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

public class ForgeRegistryTag<T>
    implements ITag<T>
{
    private net.minecraftforge.registries.tags.ITag<T> t;

    public ForgeRegistryTag(net.minecraftforge.registries.tags.ITag<T> ft)
    {
        ArgumentNullException.ThrowIfNull(ft , "ft");
        t = ft;
    }

    @Override
    public TagKey<T> getKey() {
        return t.getKey();
    }

    @Override
    public Stream<T> stream() {
        return t.stream();
    }

    @Override
    public int getSize() {
        return t.size();
    }

    @Override
    public boolean Contains(T element) {
        return t.contains(element);
    }

    @Override
    public Optional<T> GetRandomElement(RandomSource source)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(source , "source");
        return t.getRandomElement(source);
    }

    @Override
    public boolean IsBound() {
        return t.isBound();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return t.iterator();
    }
}

