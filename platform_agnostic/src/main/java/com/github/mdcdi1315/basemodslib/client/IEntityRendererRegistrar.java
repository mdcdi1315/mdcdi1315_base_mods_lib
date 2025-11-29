package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.Contract;

import net.minecraft.world.entity.Entity;

/**
 * CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY <br />
 * Provides a way for registering entity renderers to Minecraft.
 */
@Contract
public interface IEntityRendererRegistrar
{
    /**
     * Registers an entity renderer to the Minecraft client.
     * @param info The entity renderer registration information.
     * @param <T> The type of the block entity this renderer applies to.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T extends Entity> void Register(EntityRendererRegistrationInfo<T> info) throws ArgumentNullException;
}
