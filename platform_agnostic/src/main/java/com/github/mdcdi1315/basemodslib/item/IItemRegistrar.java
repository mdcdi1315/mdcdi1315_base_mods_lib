package com.github.mdcdi1315.basemodslib.item;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

/**
 * Defines methods for making known new Minecraft items to the mod loader.
 */
public interface IItemRegistrar
{
    /**
     * Registers a new item with the specified name and creator.
     * @param name The name of the item to register.
     * @param info The class instance providing the item to register.
     * @throws ArgumentNullException {@code name} and/or {@code creator} were {@code null}.
     */
    void Register(String name , ItemRegistrationInformation info) throws ArgumentNullException;
}

