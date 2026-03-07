package com.github.mdcdi1315.basemodslib.eventapi.mods.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.registries.IModLoaderRegistry;
import com.github.mdcdi1315.basemodslib.eventapi.mods.RegistryFinalizedEvent;

import net.minecraft.core.Registry;
import net.minecraft.world.item.alchemy.Potion;

/**
 * The event that is fired when the potion registry is considered finalized by the underlying mod loader.
 * @since 1.0.20
 */
public final class PotionRegistryFinalizedEvent
    extends RegistryFinalizedEvent<Potion>
{
    /**
     * Constructs a new instance of the {@link PotionRegistryFinalizedEvent} class by providing the registry that was finalized.
     * @param registry The registry that was finalized.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public PotionRegistryFinalizedEvent(Registry<Potion> registry) throws ArgumentNullException { super(registry); }

    /**
     * Constructs a new instance of the {@link PotionRegistryFinalizedEvent} class by providing the mod loader registry that was finalized.
     * @param registry The mod loader registry that was finalized.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public PotionRegistryFinalizedEvent(IModLoaderRegistry<Potion> registry) throws ArgumentNullException { super(registry); }
}
