package com.github.mdcdi1315.basemodslib.alchemy;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

import com.github.mdcdi1315.basemodslib.Contract;
import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.RegistryObjectNotFoundException;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Provides methods for registering alchemy-related things to Minecraft.
 */
@Contract
public interface IAlchemyRegistrar
{
    /**
     * Registers a new particle type to Minecraft.
     * @param name The name of the newly created particle type that will be registered.
     * @param info The particle type information that is used to register the particle type.
     * @param <T> The type of the particle options this particle type is bound to.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T extends ParticleOptions> void RegisterParticleType(@ConstantExpected String name, ParticleTypeRegistrationInfo<T> info) throws ArgumentNullException;

    /**
     * Registers a new potion to Minecraft.
     * @param name The name of the newly created potion that will be registered.
     * @param info The potion information that is used to register the potion type.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    void RegisterPotion(@ConstantExpected String name, PotionRegistrationInfo info) throws ArgumentNullException;

    /**
     * Gets a previously registered particle type.
     * @param location A resource location specifying the location of the particle type.
     * @return The registered particle type.
     * @param <T> The type of the particle that is specified in the particle type registration.
     * @throws ArgumentNullException {@code location} is {@code null}.
     * @throws RegistryObjectNotFoundException The requested particle type passed by {@code location} is non-existent.
     */
    public static <T extends ParticleOptions> ParticleType<T> GetParticleType(ResourceLocation location)
            throws ArgumentNullException, RegistryObjectNotFoundException
    {
        return (ParticleType<T>) RegistryUtils.GetRegistryObjectChecked(BuiltInRegistries.PARTICLE_TYPE , location);
    }

    /**
     * Gets a previously registered potion type.
     * @param location A resource location specifying the location of the potion type.
     * @return The registered potion type.
     * @param <T> The type of the potion that is specified in the potion type registration.
     * @throws ArgumentNullException {@code location} is {@code null}.
     * @throws RegistryObjectNotFoundException The requested potion type passed by {@code location} is non-existent.
     */
    public static <T extends Potion> T GetPotion(ResourceLocation location)
            throws ArgumentNullException, RegistryObjectNotFoundException
    {
        return (T) RegistryUtils.GetRegistryObjectChecked(BuiltInRegistries.POTION , location);
    }
}
