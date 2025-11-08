package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.registries.IModLoaderRegistry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Exception class relating to throwing exceptions when certain registry objects are required, but not found. <br />
 * Deriving classes can specify for custom, specific registries that need to.
 */
public class RegistryObjectNotFoundException
        extends BaseModsLibraryException
{
    @NotNull
    private final ResourceKey<?> object;

    /**
     * Creates a new instance of the {@link RegistryObjectNotFoundException} class by specifying a resource key of the registry object that was not found.
     * @param resource_key The resource key describing the object that was not found.
     * @throws ArgumentNullException {@code resource_key} is {@code null}.
     */
    public RegistryObjectNotFoundException(ResourceKey<?> resource_key)
            throws ArgumentNullException
    {
        super();
        if (resource_key == null) {
            throw new ArgumentNullException("resource_key", "Resource Key is null while trying to create a registry object not found exception.");
        }
        object = resource_key;
    }

    /**
     * Creates a new instance of the {@link RegistryObjectNotFoundException} class by specifying the registry where the lookup was failed and the location of the object that was requested but not found.
     * @param registry The registry where the registry object was not found.
     * @param location The location of the non-existent registry object.
     * @throws ArgumentNullException {@code registry} and/or {@code location} are {@code null}.
     */
    public RegistryObjectNotFoundException(Registry<?> registry , ResourceLocation location)
            throws ArgumentNullException
    {
        super();
        if (registry == null) {
            throw new ArgumentNullException("registry", "Originating registry is null while trying to create a registry object not found exception.");
        }
        if (location == null) {
            throw new ArgumentNullException("location", "The not found object location is null while trying to create a registry object not found exception.");
        }
        object = ResourceKey.create(registry.key() , location);
    }

    /**
     * Creates a new instance of the {@link RegistryObjectNotFoundException} class by specifying the registry where the lookup was failed and the location of the object that was requested but not found.
     * @param registry The registry where the registry object was not found.
     * @param location The location of the non-existent registry object.
     * @throws ArgumentNullException {@code registry} and/or {@code location} are {@code null}.
     */
    public RegistryObjectNotFoundException(IModLoaderRegistry<?> registry , ResourceLocation location)
            throws ArgumentNullException
    {
        super();
        if (registry == null) {
            throw new ArgumentNullException("registry", "Originating registry is null while trying to create a registry object not found exception.");
        }
        if (location == null) {
            throw new ArgumentNullException("location", "The not found object location is null while trying to create a registry object not found exception.");
        }
        object = ResourceKey.create(registry.GetRegistryKey() , location);
    }

    /**
     * Gets the resource key of the object that was not found.
     * @return A resource key instance designating the registry object that was not found.
     */
    @NotNull
    public ResourceKey<?> GetObjectResourceKey() {
        return object;
    }

    /**
     * Returns a suitable description for this exception type.
     * @return A description string describing the registry object that was not found.
     */
    @Override
    public String getMessage() {
        return String.format("""
The specified registry object was not found in the specified registry!
Registry looked up: %s
Registry object requested: %s""",
                object.registry(),
                object.location()
        );
    }
}
