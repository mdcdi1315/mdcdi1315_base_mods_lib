package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.function.Predicate;

/**
 * This holder implementation acts as the {@link Optional} class, except that it is wrapped into a {@link Holder} interface. <br />
 * It is typically used when you want such interoperability with the Minecraft API's.
 * @param <T> The type of the value for this holder to hold.
 */
public final class OptionalDirectHolder<T>
    implements Holder<T>
{
    private final T val;

    private OptionalDirectHolder(T value)
    {
        val = value;
    }

    /**
     * Creates a new optional direct holder instance. <br />
     * The object is a special one, and you should not cast it to {@link OptionalDirectHolder},
     * because this one is subject to change at any time. This method, however, is public API.
     * @param value The value that this holder will contain.
     * @return A new {@link Holder} implementation.
     * @param <T> The type of the object that this holder will contain.
     */
    public static <T> Holder<T> Create(@AllowNull T value)
    {
        return new OptionalDirectHolder<>(value);
    }

    @Override
    @MaybeNull
    public T value() {
        return val;
    }

    @Override
    public boolean isBound() {
        return val != null;
    }

    @Override
    public boolean is(ResourceLocation resourceLocation) {
        return false;
    }

    @Override
    public boolean is(ResourceKey<T> resourceKey) {
        return false;
    }

    @Override
    public boolean is(Predicate<ResourceKey<T>> predicate) {
        return false;
    }

    @Override
    public boolean is(TagKey<T> tagKey) {
        return false;
    }

    @Override
    public boolean is(Holder<T> holder) { return holder == this; }

    @Override
    public Stream<TagKey<T>> tags() {
        return Stream.empty();
    }

    @Override
    @MaybeNull
    public Either<ResourceKey<T>, T> unwrap() {
        return Either.right(val);
    }

    @Override
    public Optional<ResourceKey<T>> unwrapKey() { return Optional.empty(); }

    @Override
    public Kind kind() { return Kind.DIRECT; }

    @Override
    public boolean canSerializeIn(HolderOwner<T> holderOwner) {
        return true;
    }
}
