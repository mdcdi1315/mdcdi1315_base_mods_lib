package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.core.Registry;

public final class FabricBridgedBulkRegister<T>
    implements IBulkRegistryObjectRegister<T>
{
    private final String mod_id;
    private final Registry<T> registry;

    public FabricBridgedBulkRegister(String mod_id, Registry<T> registry) {
        this.mod_id = mod_id;
        this.registry = registry;
    }

    @Override
    public void Add(String name, T object)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        Registry.register(registry, RegistryUtils.ConstructResourceLocation(mod_id, name), object);
    }
}
