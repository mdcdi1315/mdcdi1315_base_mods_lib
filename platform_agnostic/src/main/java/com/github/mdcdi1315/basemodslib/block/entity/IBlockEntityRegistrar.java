package com.github.mdcdi1315.basemodslib.block.entity;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.world.level.block.entity.BlockEntity;

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
}
