package com.github.mdcdi1315.basemodslib.eventapi.mods.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.registries.IModLoaderRegistry;
import com.github.mdcdi1315.basemodslib.eventapi.mods.RegistryFinalizedEvent;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.attributes.Attribute;

/**
 * The event that is fired when the entity attribute registry is considered finalized by the underlying mod loader.
 * @since 1.0.20
 */
public final class EntityAttributeRegistryFinalizedEvent
    extends RegistryFinalizedEvent<Attribute>
{
    /**
     * Constructs a new instance of the {@link EntityAttributeRegistryFinalizedEvent} class by providing the registry that was finalized.
     * @param registry The registry that was finalized.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public EntityAttributeRegistryFinalizedEvent(Registry<Attribute> registry) throws ArgumentNullException { super(registry); }

    /**
     * Constructs a new instance of the {@link EntityAttributeRegistryFinalizedEvent} class by providing the mod loader registry that was finalized.
     * @param registry The mod loader registry that was finalized.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public EntityAttributeRegistryFinalizedEvent(IModLoaderRegistry<Attribute> registry) throws ArgumentNullException { super(registry); }
}
