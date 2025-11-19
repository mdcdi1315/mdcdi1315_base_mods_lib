package com.github.mdcdi1315.basemodslib.eventapi.mods.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.registries.IModLoaderRegistry;
import com.github.mdcdi1315.basemodslib.eventapi.mods.RegistryFinalizedEvent;

import net.minecraft.core.Registry;
import net.minecraft.world.level.material.Fluid;

/**
 * The event that is fired when the fluid registry is considered finalized by the underlying mod loader.
 * @since 1.0.9
 */
public final class FluidRegistryFinalizedEvent
    extends RegistryFinalizedEvent<Fluid>
{
    /**
     * Constructs a new instance of the {@link FluidRegistryFinalizedEvent} class by providing the mod loader registry that was finalized.
     * @param registry The mod loader registry that was finalized.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public FluidRegistryFinalizedEvent(IModLoaderRegistry<Fluid> registry) throws ArgumentNullException { super(registry); }

    /**
     * Constructs a new instance of the {@link FluidRegistryFinalizedEvent} class by providing the registry that was finalized.
     * @param registry The registry that was finalized.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public FluidRegistryFinalizedEvent(Registry<Fluid> registry) throws ArgumentNullException { super(registry); }
}
