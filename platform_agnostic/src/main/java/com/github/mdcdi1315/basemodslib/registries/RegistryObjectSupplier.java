package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import net.minecraft.resources.ResourceLocation;

/**
 * Provides the base class for registering registry objects.
 * @param <T> The registry object type to supply.
 */
public abstract class RegistryObjectSupplier<T>
    implements Func2<ResourceLocation,T>
{
    /**
     * Gets the value provided by this registry object supplier.
     * @return The registry object of type {@link T} to return.
     */
    protected abstract T Get(@DisallowNull ResourceLocation location);

    @Override
    public T function(ResourceLocation input) { return Get(input); }

    @Override
    public T apply(ResourceLocation location) { return Get(location); }
}
