package com.github.mdcdi1315.basemodslib.registries;

import java.util.function.Function;
import net.minecraft.resources.Identifier;

final class RegistryObjectSupplierInternal_1<T>
    extends RegistryObjectSupplier<T>
{
    private final Function<Identifier , T> function;

    public RegistryObjectSupplierInternal_1(Function<Identifier, T> f) { function = f; }

    @Override
    protected T Get(Identifier location) {
        return function.apply(location);
    }
}
