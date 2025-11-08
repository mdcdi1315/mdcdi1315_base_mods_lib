package com.github.mdcdi1315.basemodslib.block;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.RegistryObjectNotFoundException;

import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Defines methods for making known new Minecraft blocks to the mod loader.
 */
public interface IBlockRegistrar
{
    /**
     * Makes known a block with the specified block information that can create the in question block.
     * @param name The name of the block. It's resource location will be constructed by your mod's name as the namespace and this value as it's path.
     * @param info The block registration information to use.
     * @throws ArgumentNullException {@code name} or {@code creator} were {@code null}.
     */
    void Register(String name, BlockRegistrationInformation info)
            throws ArgumentNullException;

    /**
     * Gets any block by the specified location.
     * @param location The resource location that specifies the block to get.
     * @return The requested block, if that was found.
     * @param <T> The exact type of the block to return.
     * @throws ArgumentNullException {@code location} is {@code null}.
     * @throws RegistryObjectNotFoundException {@code location} was not found in the block registry.
     * @since 1.0.3
     */
    public static <T extends Block> T GetBlock(ResourceLocation location)
            throws ArgumentNullException, RegistryObjectNotFoundException
    {
        return (T) RegistryUtils.GetRegistryObjectChecked(BuiltInRegistries.BLOCK, location);
    }

    /**
     * Gets the location of the specified block in the block registry.
     * @param block_object The block object for which to request it's corresponding key in the block registry.
     * @return The requested key for {@code block_object}, if that was found; otherwise, the default key.
     * @param <T> The exact type of the block to request it's key.
     * @throws ArgumentNullException {@code block_object} is {@code null}.
     * @since 1.0.3
     */
    public static <T extends Block> ResourceLocation GetLocationForBlockOrDefault(T block_object)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(block_object, "block_object");
        return BuiltInRegistries.BLOCK.getKey(block_object);
    }
}
