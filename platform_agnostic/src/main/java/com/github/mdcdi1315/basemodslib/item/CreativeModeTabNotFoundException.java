package com.github.mdcdi1315.basemodslib.item;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLibraryException;

import net.minecraft.resources.ResourceLocation;

/**
 * Exception class that is thrown when the specified creative mode tab is not found. <br />
 * Thrown by the {@link ItemHelpers#GetMinecraftCreativeModeTabChecked(String)} and {@link ItemHelpers#GetCreativeModeTabChecked(ResourceLocation)} methods.
 */
public final class CreativeModeTabNotFoundException
        extends BaseModsLibraryException
{
    @AllowNull
    private final ResourceLocation location;

    /**
     * Constructs a new instance of the {@link CreativeModeTabNotFoundException} class, specifying the resource location of the tab that was not found.
     * @param location The location of the creative mode tab that was not found.
     */
    public CreativeModeTabNotFoundException(@MaybeNull ResourceLocation location) {
        super(String.format("The specified creative mode tab was not found: %s" , location));
        this.location = location;
    }

    /**
     * Gets the resource location of the requested creative mode tab that was not found in the creative mode tabs registry.
     * @return The resource location that was requested.
     */
    @MaybeNull
    public ResourceLocation GetLocation() {
        return location;
    }
}
