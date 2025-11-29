package com.github.mdcdi1315.basemodslib.menu;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

import com.github.mdcdi1315.basemodslib.Contract;
import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.RegistryObjectNotFoundException;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Provides a way for registering new container menus to Minecraft.
 * @since 1.0.6
 */
@Contract
public interface IMenuTypeRegistrar
{
    /**
     * Registers a new menu type to Minecraft.
     * @param name The name of the menu type to register.
     * @param info The menu type registration information to use.
     * @param <T> The type of the container menu to create each time.
     * @throws ArgumentNullException {@code name} and/or {@code info} are {@code null}.
     */
    <T extends AbstractContainerMenu> void Register(@ConstantExpected String name, MenuTypeRegistrationInfo<T> info) throws ArgumentNullException;

    /**
     * Gets a {@link MenuType} registered to this Minecraft instance.
     * @param location The resource location that specifies the menu type to get.
     * @return The existing menu type instance, if lookup was successful.
     * @param <T> The exact type of the container menu to return.
     * @throws ArgumentNullException {@code location} is {@code null}.
     * @throws RegistryObjectNotFoundException {@code location} was not found in the menu type registry.
     */
    public static <T extends AbstractContainerMenu> MenuType<T> GetRegisteredMenuType(ResourceLocation location)
        throws ArgumentNullException, RegistryObjectNotFoundException
    {
        return (MenuType<T>) RegistryUtils.GetRegistryObjectChecked(BuiltInRegistries.MENU, location);
    }
}
