package com.github.mdcdi1315.basemodslib.item;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.basemodslib.item.datacomponents.DataComponentTypeRegistrationInformation;

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

    /**
     * Registers a new data component type to Minecraft.
     * @param name The name of the data component type to register.
     * @param info The data component type registration information to use for this registration.
     * @param <T> The type of the data component to create.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T> void RegisterDataComponentType(String name , DataComponentTypeRegistrationInformation<T> info) throws ArgumentNullException;
}

