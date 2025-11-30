package com.github.mdcdi1315.basemodslib.item;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

import com.github.mdcdi1315.basemodslib.Contract;
import com.github.mdcdi1315.basemodslib.item.datacomponents.DataComponentTypeRegistrationInformation;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;

/**
 * Defines methods for making known new Minecraft items to the mod loader. <br />
 * Additionally, it provides a utility method for registering new Creative Mode Tabs to Minecraft.
 */
@Contract
public interface IItemRegistrar
{
    /**
     * Registers a new item with the specified name and creator.
     * @param name The name of the item to register.
     * @param info The class instance providing the item to register.
     * @throws ArgumentNullException {@code name} and/or {@code creator} were {@code null}.
     */
    void Register(@ConstantExpected String name , ItemRegistrationInformation info) throws ArgumentNullException;

    /**
     * Registers a new data component type to Minecraft.
     * @param name The name of the data component type to register.
     * @param info The data component type registration information to use for this registration.
     * @param <T> The type of the data component to create.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T> void RegisterDataComponentType(@ConstantExpected String name , DataComponentTypeRegistrationInformation<T> info) throws ArgumentNullException;

    /**
     * Registers a previously and custom-created Minecraft Creative Mode tab.
     * @param name The name of the newly created creative mode tab.
     * @param tab The new custom tab to register.
     * @throws ArgumentNullException {@code name} and/or {@code tab} were {@code null}.
     * @since 1.0.5
     */
    void RegisterCreativeModeTab(@ConstantExpected String name, CreativeModeTab tab) throws ArgumentNullException;

    /**
     * Provides a way to append custom-created item stacks to a creative mode tab.
     * @param tab The creative mode tab to register the item stack to.
     * @param stack The function that, upon invoking it, it provides an item stack to register to the creative mode tab.
     * @throws ArgumentNullException {@code tab} and/or {@code stack} are {@code null}.
     * @since 1.0.12
     */
    void RegisterCreativeModeTabStack(CreativeModeTab tab , Func1<ItemStack> stack) throws ArgumentNullException;
}

