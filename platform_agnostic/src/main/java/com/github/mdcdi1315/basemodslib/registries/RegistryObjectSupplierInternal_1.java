package com.github.mdcdi1315.basemodslib.registries;

import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

final class RegistryObjectSupplierInternal_1<T>
    extends RegistryObjectSupplier<T>
{
    private final Function<ResourceLocation , T> function;

    public RegistryObjectSupplierInternal_1(Function<ResourceLocation, T> f) { function = f; }

    @Override
    protected T Get(ResourceLocation location) {
        return function.apply(location);
    }
}
