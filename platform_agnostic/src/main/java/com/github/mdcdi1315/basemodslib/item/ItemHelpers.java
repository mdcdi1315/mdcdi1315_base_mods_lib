package com.github.mdcdi1315.basemodslib.item;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Utility class providing utilities around Minecraft Items, and how to create them.
 */
public final class ItemHelpers
{
    private ItemHelpers() {}

    /**
     * Gets a Minecraft creative mode tab for the specified name.
     * @param name The name of the creative mode tab to retrieve.
     * @return The creative mode tab object. Will return {@code null} if not found.
     */
    @MaybeNull
    public static CreativeModeTab GetMinecraftCreativeModeTab(@ConstantExpected String name) {
        ArgumentNullException.ThrowIfNull(name, "name");
        return BuiltInRegistries.CREATIVE_MODE_TAB.get(ResourceLocation.tryBuild(ResourceLocation.DEFAULT_NAMESPACE , name));
    }

    /**
     * Gets a Creative mode tab by the specified location that designates the tab ID.
     * @param tab_location The location identifying the tab.
     * @return The creative mode tab object. Will return {@code null} if not found.
     */
    @MaybeNull
    public static CreativeModeTab GetCreativeModeTab(ResourceLocation tab_location) {
        ArgumentNullException.ThrowIfNull(tab_location, "tab_location");
        return BuiltInRegistries.CREATIVE_MODE_TAB.get(tab_location);
    }

    /**
     * Gets the specified Minecraft creative mode tab, throwing exceptions on any possible programming mistake found.
     * @param name The name of the Creative Mode tab to retrieve. Be careful; only the name of the tab is required; the namespace is appended by the function.
     * @return The object associated with {@code name}, if it was found and it is valid.
     * @throws ArgumentException {@code name} is {@code null} or the empty string ("").
     * @throws CreativeModeTabNotFoundException The constructed creative mode tab was not found.
     */
    public static CreativeModeTab GetMinecraftCreativeModeTabChecked(@ConstantExpected String name)
            throws ArgumentException, CreativeModeTabNotFoundException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ResourceLocation rl = ResourceLocation.tryBuild(ResourceLocation.DEFAULT_NAMESPACE, name);
        if (rl == null) {
            throw new ArgumentException("The resource location could not be constructed.");
        } else {
            CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.get(rl);
            if (tab == null) {
                throw new CreativeModeTabNotFoundException(rl);
            } else {
                return tab;
            }
        }
    }

    /**
     * Gets the specified creative mode tab, throwing exceptions on any possible programming mistake found.
     * @param location The resource location of the Creative Mode tab to retrieve.
     * @return The object associated with {@code location}, if it was found and it is valid.
     * @throws ArgumentNullException {@code location} is {@code null}.
     * @throws CreativeModeTabNotFoundException The constructed creative mode tab was not found.
     */
    public static CreativeModeTab GetCreativeModeTabChecked(ResourceLocation location)
            throws ArgumentNullException, CreativeModeTabNotFoundException
    {
        ArgumentNullException.ThrowIfNull(location, "location");
        CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.get(location);
        if (tab == null) {
            throw new CreativeModeTabNotFoundException(location);
        } else {
            return tab;
        }
    }

    /**
     * Provides a mechanism for getting an item through it's block and that block's resource location. <br />
     * It is also suitable to be passed as a method reference to the {@link com.github.mdcdi1315.basemodslib.block.BlockRegistrationInformation#item_for_block_getter()} field.
     * @param b The block in question to create it's corresponding item.
     * @param location The location of the block in {@code b} parameter in the block registry.
     * @return The constructed item.
     */
    public static Item GetItemForBlockSimple(Block b , ResourceLocation location) {
        return new BlockItem(b, new Item.Properties());
    }
}
