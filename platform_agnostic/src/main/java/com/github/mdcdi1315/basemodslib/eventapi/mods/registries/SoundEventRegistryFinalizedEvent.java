package com.github.mdcdi1315.basemodslib.eventapi.mods.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.registries.IModLoaderRegistry;
import com.github.mdcdi1315.basemodslib.eventapi.mods.RegistryFinalizedEvent;

import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;

/**
 * The event that is fired when the sound event registry is considered finalized by the underlying mod loader.
 * @since 1.0.15
 */
public final class SoundEventRegistryFinalizedEvent
    extends RegistryFinalizedEvent<SoundEvent>
{
    /**
     * Constructs a new instance of the {@link SoundEventRegistryFinalizedEvent} class by providing the mod loader registry that was finalized.
     * @param registry The mod loader registry that was finalized.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public SoundEventRegistryFinalizedEvent(IModLoaderRegistry<SoundEvent> registry) throws ArgumentNullException { super(registry); }

    /**
     * Constructs a new instance of the {@link SoundEventRegistryFinalizedEvent} class by providing the registry that was finalized.
     * @param registry The registry that was finalized.
     * @throws ArgumentNullException {@code registry} is {@code null}.
     */
    public SoundEventRegistryFinalizedEvent(Registry<SoundEvent> registry) throws ArgumentNullException { super(registry); }
}
