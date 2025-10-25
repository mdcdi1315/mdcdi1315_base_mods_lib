package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.mojang.serialization.Codec;

import net.minecraft.tags.TagKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Defines the main mod loader abstraction for mod loader registries. <br />
 * Only basic operations can be performed on these registry objects.
 * @param <T> The type of the elements to hold into this registry.
 */
public interface IModLoaderRegistry<T>
    extends Iterable<T>
{
    /**
     * Returns a {@link Codec} to use for referencing registry's elements by their names.
     * @return The codec by-name to use.
     */
    default Codec<T> ByNameCodec() {
        return new ModLoaderRegistryByNameCodec<>(this);
    }

    /**
     * Gets a value whether the resource with the specified location is part of this registry object.
     * @param location The location of the resource.
     * @return A value whether the specified entry is part of this registry object.
     */
    boolean ContainsKey(ResourceLocation location);

    default boolean ContainsKey(ResourceKey<T> key)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(key , "key");
        return ContainsKey(key.location());
    }

    /**
     * Gets a serialization codec for JSON data for the current registry.
     * @return A serialization codec. It is not necessary to be implemented by the underlying registry object.
     */
    @MaybeNull // The registry might not have a codec implementation, in most times though it is required to have one.
    default Codec<T> GetSerializationCodec() {
        return null;
    }

    /**
     * Dynamically registers a new registry object to the current instance.
     * @param location The location that this object should be referenced as. Must not be null.
     * @param value The value of the new object. Must not be null.
     */
    void Register(@NotNull ResourceLocation location , @NotNull T value) throws ArgumentNullException;

    /**
     * Gets a resource key that identifies this registry.
     * @return A {@link ResourceKey} identifying <em>this</em> registry.
     */
    ResourceKey<Registry<T>> GetRegistryKey();

    /**
     * Gets all the keys of the entries comprising this registry.
     * @return A {@link Set} containing {@link ResourceLocation}s that is their identifiers for the actual entries.
     */
    Set<ResourceLocation> GetEntryKeys();

    /**
     * Gets the number of entries contained in this registry object.
     * @return The number of entries contained in the current registry.
     */
    default int GetSize() {
        return GetEntryKeys().size();
    }

    /**
     * Gets a value whether this registry does not contain objects.
     * @return A value whether this registry does not contain at least one object.
     */
    default boolean IsEmpty() {
        return GetSize() == 0;
    }

    /**
     * Gets the element's value, if the element is part of this registry object.
     * @param location The key of the element.
     * @return The element's value.
     * @throws ArgumentNullException <em>location</em> was null.
     */
    @NotNull
    Optional<T> GetElementValue(ResourceLocation location) throws ArgumentNullException;

    /**
     * Gets the resource key of the specified element.
     * @param element The element to get its resource key.
     * @return A {@link ResourceKey} representing the resource location of this element.
     * @throws ArgumentNullException <em>element</em> was null.
     */
    Optional<ResourceKey<T>> GetResourceKey(T element) throws ArgumentNullException;

    /**
     * Gets ALL the tags defined in the current registry object.
     * @return A {@link Stream} containing {@link ITag} values
     */
    Stream<ITag<T>> GetTags();

    /**
     * Gets the specified tag, or creates it on the fly if it does not exist. <br />
     * On any case, this method guarantees to not return null at any circumstance.
     * @param key The tag key of the tag to retrieve.
     * @return The created or retrieved tag.
     * @throws ArgumentNullException <em>key</em> was null.
     */
    @NotNull ITag<T> GetOrBindTag(TagKey<T> key) throws ArgumentNullException;

}
