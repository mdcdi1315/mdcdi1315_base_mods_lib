package com.github.mdcdi1315.basemodslib.sounds;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.Contract;
import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.RegistryObjectNotFoundException;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Provides a way to register sound events to Minecraft. <br />
 * @since 1.0.15
 */
@Contract
public interface ISoundRegistrar
{
    /**
     * Registers a new sound event to Minecraft.
     * @param event The sound event to be registered.
     * @param name The name of the newly created sound event.
     * @throws ArgumentNullException {@code event} and/or {@code name} are {@code null}.
     */
    void RegisterSoundEvent(SoundEvent event, String name) throws ArgumentNullException;

    /**
     * Gets a previously registered sound event.
     * @param location The resource location of the sound event in the sound event registry.
     * @return The {@link SoundEvent} corresponding to {@code location}.
     * @throws ArgumentNullException {@code location} is {@code null}.
     * @throws RegistryObjectNotFoundException The requested sound event passed by {@code location} is non-existent.
     */
    @NotNull
    public static SoundEvent GetSoundEvent(ResourceLocation location)
            throws ArgumentNullException, RegistryObjectNotFoundException
    {
        return RegistryUtils.GetRegistryObjectChecked(BuiltInRegistries.SOUND_EVENT, location);
    }
}
