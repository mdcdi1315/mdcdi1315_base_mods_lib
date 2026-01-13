package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

/**
 * A convenience object that allows to add multiple registry objects in bulk. <br />
 * This should be instead used for registries not being wrapped by the Base Mods Library.
 * @since 1.0.15
 */
public interface IBulkRegistryObjectRegister<T>
{
    /**
     * Adds an object to the registry that this instance is bound to.
     * @param name The name of the object.
     * @param object The object to associate to the registry.
     * @throws ArgumentNullException {@code name} is {@code null}.
     */
    void Add(String name, T object) throws ArgumentNullException;
}
