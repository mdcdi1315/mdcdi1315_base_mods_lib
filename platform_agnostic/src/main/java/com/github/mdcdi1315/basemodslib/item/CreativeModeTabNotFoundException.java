package com.github.mdcdi1315.basemodslib.item;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.RegistryObjectNotFoundException;

import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

/**
 * Exception class that is thrown when the specified creative mode tab is not found. <br />
 * Thrown by the {@link ItemHelpers#GetMinecraftCreativeModeTabChecked(String)} and {@link ItemHelpers#GetCreativeModeTabChecked(ResourceLocation)} methods.
 */
public final class CreativeModeTabNotFoundException
        extends RegistryObjectNotFoundException
{
    /**
     * Constructs a new instance of the {@link CreativeModeTabNotFoundException} class, specifying the resource location of the tab that was not found.
     * @param location The location of the creative mode tab that was not found.
     */
    public CreativeModeTabNotFoundException(@MaybeNull ResourceLocation location) {
        super(ResourceKey.create(Registries.CREATIVE_MODE_TAB , location));
    }

    public String getMessage() {
        return String.format("The specified creative mode tab was not found: %s" , GetObjectResourceKey().location());
    }
}
