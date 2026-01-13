package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.Contract;

import com.github.mdcdi1315.basemodslib.RegistryNotFoundException;
import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provides methods for registering registry objects and registries to mod loaders.
 */
@Contract
public interface IRegistryRegistrar
{
    /**
     * Registers an object to an existing registry.
     * @param name The name of the object to register.
     * @param registry The registry under which to register the object. Note: Two different registries with the same backing type can exist.
     * @param supplier The instance providing registry objects of type {@link T}.
     * @param <T> The type of the objects supported by the backing registry.
     * @throws ArgumentNullException {@code registry} and/or {@code name} and/or {@code supplier} are {@code null}.
     */
    <T> void RegisterObject(ResourceKey<Registry<T>> registry, String name, RegistryObjectSupplier<T> supplier) throws ArgumentNullException;

    /**
     * Registers an object to an existing registry. <br />
     * Remarks: <br />
     * If the mod loaders have a faster alternative than wiring to the {@link #RegisterObject(ResourceKey, String, RegistryObjectSupplier)} method should override this method as well.
     * @param name The name of the object to register.
     * @param registry The registry under which to register the object. Note: Two different registries with the same backing type can exist.
     * @param supplier The instance providing registry objects of type {@link T}.
     * @param <T> The type of the objects supported by the backing registry.
     * @throws ArgumentNullException {@code registry} and/or {@code name} and/or {@code supplier} are {@code null}.
     * @since 1.0.13
     */
    default <T> void RegisterObject(ResourceKey<Registry<T>> registry, String name, Function<ResourceLocation, T> supplier)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(supplier, "supplier");
        RegisterObject(registry, name, new RegistryObjectSupplierInternal_1<>(supplier));
    }

    /**
     * Registers an object to an existing registry. <br />
     * Remarks: <br />
     * If the mod loaders have a faster alternative than wiring to the {@link #RegisterObject(ResourceKey, String, RegistryObjectSupplier)} method should override this method as well.
     * @param name The name of the object to register.
     * @param registry The registry under which to register the object. Note: Two different registries with the same backing type can exist.
     * @param supplier The instance providing registry objects of type {@link T}.
     * @param <T> The type of the objects supported by the backing registry.
     * @throws ArgumentNullException {@code registry} and/or {@code name} and/or {@code supplier} are {@code null}.
     * @since 1.0.13
     */
    default <T> void RegisterObject(ResourceKey<Registry<T>> registry, String name, Supplier<T> supplier)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(supplier, "supplier");
        RegisterObject(registry, name, new RegistryObjectSupplierInternal_2<>(supplier));
    }

    /**
     * Gets an instance of the {@link IBulkRegistryObjectRegister} class for cases that you want to add a lot of items to a specific Minecraft registry.
     * @param registry_resource_key The resource key of the registry that you wish to get a bulk register object for.
     * @return The bulk register instance for {@code registry_resource_key}.
     * @param <T> The type of objects the registry does store.
     * @throws ArgumentNullException {@code registry_resource_key} is {@code null}.
     * @since 1.0.15
     */
    <T> IBulkRegistryObjectRegister<T> GetBulkRegister(ResourceKey<? extends Registry<T>> registry_resource_key)
            throws ArgumentNullException, RegistryNotFoundException;

    /**
     * Registers a registry to the mod loader. <br />
     * The loaded registry will be returned once the method reference provided through {@code on_registry_ready} parameter.
     * @param registryResourceKey The resource key of the registry, representing the registry's location in the Minecraft logic.
     * @param on_registry_ready The method to invoke once the registry is ready.
     * @param <T> The type of registry elements to reference.
     * @throws ArgumentNullException {@code registryResourceKey} is {@code null}.
     */
    <T> void RegisterRegistry(ResourceKey<Registry<T>> registryResourceKey, Action1<IModLoaderRegistry<T>> on_registry_ready)
            throws ArgumentNullException;

    /**
     * Registers a data pack registry to Minecraft. <br />
     * The registry will be loaded when all the other modifiable registries are created as well.
     * @param registry_name The exact name and path of the newly created registry.
     * @param element_codec The codec that can encode and decode a single element of the registry to create.
     * @param <T> The type of the registry elements.
     * @throws ArgumentNullException {@code registry_name} and/or {@code element_codec} are {@code null}.
     */
    <T> void RegisterDatapackRegistry(ResourceKey<Registry<T>> registry_name, Codec<T> element_codec)
            throws ArgumentNullException;
}
