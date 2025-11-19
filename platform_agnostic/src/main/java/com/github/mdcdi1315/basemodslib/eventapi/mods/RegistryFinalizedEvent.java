package com.github.mdcdi1315.basemodslib.eventapi.mods;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableEvent;
import com.github.mdcdi1315.basemodslib.registries.IModLoaderRegistry;
import com.github.mdcdi1315.basemodslib.RegistryObjectNotFoundException;
import com.github.mdcdi1315.basemodslib.registries.MinecraftWrappedModLoaderRegistry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Provides an event that provides the registry, once the registry is considered complete by the mod loader. <br />
 * Although that this event can be fired by the events manager, raw instances of this are not dispatched by the library. <br />
 * More specific variants of this event, however, like {@link com.github.mdcdi1315.basemodslib.eventapi.mods.registries.BlockRegistryFinalizedEvent} are normally dispatched. See the documentation for them.
 * @param <T> The type of the elements that the registry is holding.
 * @since 1.0.9
 */
public class RegistryFinalizedEvent<T>
    implements IDestroyableEvent
{
    private final IModLoaderRegistry<T> registry;

    /**
     * Constructs a new instance of the {@link RegistryFinalizedEvent} class by providing the mod loader registry that was finalized.
     * @param registry The mod loader registry that was finalized.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public RegistryFinalizedEvent(IModLoaderRegistry<T> registry)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(registry, "registry");
        this.registry = registry;
    }

    /**
     * Constructs a new instance of the {@link RegistryFinalizedEvent} class by providing the registry that was finalized.
     * @param registry The registry that was finalized.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public RegistryFinalizedEvent(Registry<T> registry)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(registry, "registry");
        this.registry = new MinecraftWrappedModLoaderRegistry<>(registry);
    }

    /**
     * Gets the registry that was finalized.
     * @return The registry that was finalized. It is guaranteed that the returned object will never, but never, be {@code null}.
     */
    @NotNull
    public IModLoaderRegistry<T> GetRegistry() { return registry; }

    /**
     * Retrieves a registry object from the finalized registry. This works like {@link com.github.mdcdi1315.basemodslib.registries.RegistryUtils#GetRegistryObjectChecked(IModLoaderRegistry, ResourceLocation)}.
     * @param location The location of the object inside this registry.
     * @return The registered registry object, if that was successfully found.
     * @param <TO> The more specific type of object you require.
     * @throws ArgumentNullException {@code location} is {@code null}.
     * @throws ClassCastException {@link T} cannot be cast to {@link TO} for a reason.
     * @throws RegistryObjectNotFoundException {@code location} is not a valid registry object in the current registry.
     */
    @NotNull
    public <TO extends T> TO GetRegistryObjectChecked(@NotNull ResourceLocation location)
        throws ArgumentNullException, ClassCastException, RegistryObjectNotFoundException
    {
        ArgumentNullException.ThrowIfNull(location, "location");
        Optional<T> reg_object = registry.GetElementValue(location);
        if (reg_object.isPresent()) {
            return (TO) reg_object.get();
        } else {
            throw new RegistryObjectNotFoundException(registry , location);
        }
    }
}
