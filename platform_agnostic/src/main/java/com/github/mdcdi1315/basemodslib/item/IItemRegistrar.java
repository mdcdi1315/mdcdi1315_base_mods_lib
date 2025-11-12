package com.github.mdcdi1315.basemodslib.item;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.world.item.CreativeModeTab;

/**
 * Defines methods for making known new Minecraft items to the mod loader. <br />
 * Additionally, it provides a utility method for registering new Creative Mode Tabs to Minecraft.
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
     * Registers a previously and custom-created Minecraft Creative Mode tab.
     * @param name The name of the newly created creative mode tab.
     * @param tab The new custom tab to register.
     * @throws ArgumentNullException {@code name} and/or {@code tab} were {@code null}.
     * @since 1.0.5
     */
    void RegisterCreativeModeTab(String name, CreativeModeTab tab) throws ArgumentNullException;
}

