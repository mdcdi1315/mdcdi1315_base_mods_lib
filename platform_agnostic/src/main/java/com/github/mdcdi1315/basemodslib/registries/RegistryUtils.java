package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.RegistryNotFoundException;
import com.github.mdcdi1315.basemodslib.RegistryObjectNotFoundException;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Optional;

/**
 * Defines static, helper methods for interacting with Minecraft's registries.
 */
public final class RegistryUtils
{
    // Do not let anyone instantiate this class.
    private RegistryUtils() {}

    /**
     * Gets a registry object from the specified registry. <br />
     * This method will fail with {@link RegistryObjectNotFoundException} if the requested object cannot be found.
     * @param registry The registry from which to pull the registry object.
     * @param location The location of the object to pull.
     * @return The requested object, if that was found.
     * @param <T> The type of the object to retrieve from the registry.
     * @throws ArgumentNullException {@code registry} and/or {@code location} are {@code null}.
     * @throws RegistryObjectNotFoundException The passed {@code location} does not represent a valid registry item within the provided registry.
     */
    @NotNull
    public static <T> T GetRegistryObjectChecked(Registry<T> registry, ResourceLocation location)
            throws ArgumentNullException, RegistryObjectNotFoundException
    {
        ArgumentNullException.ThrowIfNull(registry,"registry");
        ArgumentNullException.ThrowIfNull(location,"location");
        Optional<T> to = registry.getOptional(location);
        if (to.isEmpty()) {
            throw new RegistryObjectNotFoundException(registry , location);
        }
        return to.get();
    }

    /**
     * Gets a registry object from the specified mod loader registry. <br />
     * This method will fail with {@link RegistryObjectNotFoundException} if the requested object cannot be found.
     * @param registry The registry from which to pull the registry object.
     * @param location The location of the object to pull.
     * @return The requested object, if that was found.
     * @param <T> The type of the object to retrieve from the registry.
     * @throws ArgumentNullException {@code registry} and/or {@code location} are {@code null}.
     * @throws RegistryObjectNotFoundException The passed {@code location} does not represent a valid registry item within the provided registry.
     */
    @NotNull
    public static <T> T GetRegistryObjectChecked(IModLoaderRegistry<T> registry, ResourceLocation location)
            throws ArgumentNullException, RegistryObjectNotFoundException
    {
        ArgumentNullException.ThrowIfNull(registry,"registry");
        ArgumentNullException.ThrowIfNull(location,"location");
        Optional<T> to = registry.GetElementValue(location);
        if (to.isEmpty()) {
            throw new RegistryObjectNotFoundException(registry , location);
        }
        return to.get();
    }

    /**
     * From a resource key, gets the specified registry object.
     * @param key The resource key that perfectly describes the object to retrieve, and from which registry.
     * @return The requested registry object.
     * @param <T> The registry object type to be returned.
     * @throws ArgumentNullException {@code key} is {@code null}.
     * @throws RegistryObjectNotFoundException The requested registry object was not found.
     */
    @NotNull
    public static <T> T GetRegistryObjectFromResourceKey(ResourceKey<T> key)
            throws ArgumentNullException, RegistryObjectNotFoundException
    {
        ArgumentNullException.ThrowIfNull(key, "key");
        ResourceLocation registry_loc = key.registry();
        if (registry_loc.equals(Registries.ROOT_REGISTRY_NAME)) {
            // A registry was requested. We need to behave differently.
            var ro = BuiltInRegistries.REGISTRY.getOptional(key.location());
            if (ro.isPresent()) {
                return (T) ro.get();
            } else {
                throw new RegistryObjectNotFoundException(key); // We can throw this as a resource key, since we are looking a registry into the root registry.
            }
        } else {
            // Typical case where there is a lookup of a specific object in a specific registry.
            var opt_registry = BuiltInRegistries.REGISTRY.getOptional(registry_loc);
            if (opt_registry.isPresent()) {
                var ro = opt_registry.get().getOptional(key.location());
                if (ro.isPresent()) {
                    return (T) ro.get();
                } else {
                    throw new RegistryObjectNotFoundException(key);
                }
            } else {
                throw new RegistryNotFoundException(registry_loc);
            }
        }
    }

    /**
     * Gets a Minecraft registry by the specified resource key. <br />
     * See the {@link Registries} class for which Minecraft registries can be returned by this function.
     * @param resource_key The resource key that points to a Minecraft registry.
     * @return The {@link Registry} object for {@code resource_key}
     * @param <T> The type of the objects the returned registry object retains.
     * @since 1.0.15
     * @throws ArgumentNullException {@code resource_key} is {@code null}.
     * @throws RegistryNotFoundException {@code resource_key} does not point to a valid Minecraft registry.
     */
    public static <T> Registry<T> GetRootRegistry(ResourceKey<? extends Registry<T>> resource_key)
        throws ArgumentNullException, RegistryNotFoundException
    {
        ArgumentNullException.ThrowIfNull(resource_key, "resource_key");
        if (resource_key.registry().equals(Registries.ROOT_REGISTRY_NAME)) {
            var ro = BuiltInRegistries.REGISTRY.getOptional(resource_key.location());
            if (ro.isPresent()) {
                return (Registry<T>) ro.get();
            } else {
                throw new RegistryNotFoundException(resource_key.location());
            }
        } else {
            throw new InvalidOperationException("The specified resource key does not represent a root registry key: " + resource_key);
        }
    }
}
