package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import net.minecraft.resources.Identifier;

import java.util.function.Function;

/**
 * Provides the base class for registering registry objects.
 * @param <T> The registry object type to supply.
 */
public abstract class RegistryObjectSupplier<T>
    implements Function<Identifier, T>, Func2<Identifier,T>
{
    /**
     * Gets the value provided by this registry object supplier.
     * @return The registry object of type {@link T} to return.
     */
    protected abstract T Get(@DisallowNull Identifier location);

    @Override
    public T function(Identifier input) {
        return Get(input);
    }

    @Override
    public T apply(Identifier location) {
        return Get(location);
    }
}
