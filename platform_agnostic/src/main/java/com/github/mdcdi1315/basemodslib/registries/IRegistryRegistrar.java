package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.Contract;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

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
     * @throws ArgumentNullException {@code registry} and/or {@code name} and/or {@code supplier} were {@code null}.
     */
    <T> void RegisterObject(ResourceKey<Registry<T>> registry, String name, RegistryObjectSupplier<T> supplier) throws ArgumentNullException;

    /**
     * Registers a registry to the mod loader. <br />
     * The loaded registry will be returned once the method reference provided through {@code on_registry_ready} parameter.
     * @param registryResourceKey The resource key of the registry, representing the registry's location in the Minecraft logic.
     * @param on_registry_ready The method to invoke once the registry is ready.
     * @param <T> The type of registry elements to reference.
     * @throws ArgumentNullException {@code registryResourceKey} was {@code null}.
     */
    <T> void RegisterRegistry(ResourceKey<Registry<T>> registryResourceKey, Action1<IModLoaderRegistry<T>> on_registry_ready)
            throws ArgumentNullException;

    /**
     * Registers a data pack registry to Minecraft. <br />
     * The registry will be loaded when all the other modifiable registries are created as well.
     * @param registry_name The exact name and path of the newly created registry.
     * @param element_codec The codec that can encode and decode a single element of the registry to create.
     * @param <T> The type of the registry elements.
     * @throws ArgumentNullException {@code registry_name} and/or {@code element_codec} were {@code null}.
     */
    <T> void RegisterDatapackRegistry(ResourceKey<Registry<T>> registry_name, Codec<T> element_codec)
            throws ArgumentNullException;
}
