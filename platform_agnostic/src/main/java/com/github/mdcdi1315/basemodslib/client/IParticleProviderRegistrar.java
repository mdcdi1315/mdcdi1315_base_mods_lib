package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.Contract;

import net.minecraft.core.particles.ParticleOptions;

/**
 * CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY CLIENT-ONLY <br />
 * Provides a way for registering particle providers to Minecraft.
 */
@Contract
public interface IParticleProviderRegistrar
{
    /**
     * Registers a particle provider.
     * @param info The simple particle provider registration information.
     * @param <T> The particle options to apply on the particle type itself.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T extends ParticleOptions> void Register(SimpleParticleProviderRegistrationInfo<T> info) throws ArgumentNullException;

    /**
     * Registers a particle provider.
     * @param info The advanced particle provider registration information.
     * @param <T> The particle options to apply on the particle type itself.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T extends ParticleOptions> void Register(AdvancedParticleProviderRegistrationInfo<T> info) throws ArgumentNullException;
}
