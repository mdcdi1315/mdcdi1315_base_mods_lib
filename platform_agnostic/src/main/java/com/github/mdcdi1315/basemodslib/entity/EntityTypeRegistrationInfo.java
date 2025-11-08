package com.github.mdcdi1315.basemodslib.entity;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * Registers a new entity type to Minecraft, that is providing new entities!
 * @param entity_provider The function that upon calling, provides the entity type to register.
 * @param <T> The type of the entity to create.
 */
public record EntityTypeRegistrationInfo<T extends Entity>(
      @NotNull Func1<EntityType<T>> entity_provider
)
{
    /**
     * Constructs a new instance of the {@link EntityTypeRegistrationInfo} class from the specified function that provides the entity type to register.
     * @param entity_provider The function that upon calling, provides the entity type to register.
     * @throws ArgumentNullException {@code entity_provider} is {@code null}.
     */
    public EntityTypeRegistrationInfo {
        ArgumentNullException.ThrowIfNull(entity_provider , "entity_provider");
    }
}
