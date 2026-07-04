package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.core.Registry;

public record FabricBridgedBulkRegister<T>(String mod_id, Registry<T> registry)
    implements IBulkRegistryObjectRegister<T>
{
    @Override
    public void Add(String name, T object)
            throws ArgumentNullException
    {
        // 'name' parameter validation is handled by RegistryUtils#ConstructResourceLocation method.
        Registry.register(registry, RegistryUtils.ConstructResourceLocation(mod_id, name), object);
    }
}
