package com.github.mdcdi1315.basemodslib.registries;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

final class RegistryObjectSupplierInternal_2<T>
    extends RegistryObjectSupplier<T>
{
    private final Supplier<T> sup;

    public RegistryObjectSupplierInternal_2(Supplier<T> s) { sup = s; }

    @Override
    protected T Get(ResourceLocation location) { return sup.get(); }
}
