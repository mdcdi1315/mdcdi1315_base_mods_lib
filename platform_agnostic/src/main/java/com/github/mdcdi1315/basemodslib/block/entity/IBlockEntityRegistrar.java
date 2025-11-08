package com.github.mdcdi1315.basemodslib.block.entity;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.RegistryObjectNotFoundException;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Defines methods for making known new Minecraft block entities to the mod loader.
 */
public interface IBlockEntityRegistrar
{
    /**
     * Makes known a block entity with the specified factory that can create those block entities.
     * @param name The name of the block entity to register.
     * @param factory The factory for constructing block entities of type {@link T}.
     * @param <T> The type of the block entity to create.
     * @throws ArgumentNullException {@code name} and/or {@code factory} were {@code null}.
     */
    <T extends BlockEntity> void Register(String name, IBlockEntityFactory<T> factory) throws ArgumentNullException;

    /**
     * Gets a previously registered block entity type.
     * @param location A resource location specifying the location of the block entity type to get.
     * @return The registered block entity type.
     * @param <T> The type of the block entity that is specified in the block entity type factory.
     * @throws ArgumentNullException {@code location} is {@code null}.
     * @throws RegistryObjectNotFoundException The requested block entity type passed by {@code location} is non-existent.
     * @since 1.0.3
     */
    public static <T extends BlockEntity> BlockEntityType<T> GetBlockEntityType(ResourceLocation location)
        throws ArgumentNullException , RegistryObjectNotFoundException
    {
        return (BlockEntityType<T>) RegistryUtils.GetRegistryObjectChecked(BuiltInRegistries.BLOCK_ENTITY_TYPE , location);
    }
}
