package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;


import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class FabricBridgedBulkRegister<T>
    implements IBulkRegistryObjectRegister<T>
{
    private final String mod_id;
    private final Registry<T> registry;

    public FabricBridgedBulkRegister(String mod_id, Registry<T> registry) {
        this.mod_id = mod_id;
        this.registry = registry;
    }

    // Keep this in sync with the FabricCommonRegistryItemsRegistrar class.
    private ResourceLocation BuildAndValidateLocation(String path)
    {
        ResourceLocation ret = ResourceLocation.tryBuild(mod_id, path);

        if (ret == null) {
            throw new RuntimeException("Could not create the resource location!");
        }

        return ret;
    }

    @Override
    public void Add(String name, T object)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        Registry.register(registry, BuildAndValidateLocation(name), object);
    }
}
