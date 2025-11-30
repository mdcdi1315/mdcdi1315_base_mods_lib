package com.github.mdcdi1315.basemodslib.alchemy;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions;

/**
 * Provides information for registering new particle types to Minecraft.
 * @param particle_type_getter The function that upon invoking, it returns the particle type to register.
 * @param <T> The options of the particle.
 */
public record ParticleTypeRegistrationInfo<T extends ParticleOptions>(
        @NotNull Func1<ParticleType<T>> particle_type_getter
) {
    /**
     * Creates a new instance of the {@link ParticleTypeRegistrationInfo} class.
     * @param particle_type_getter The function that upon invoking, it returns the particle type to register.
     * @throws ArgumentNullException {@code particle_type_getter} is {@code null}.
     */
    public ParticleTypeRegistrationInfo {
        ArgumentNullException.ThrowIfNull(particle_type_getter, "particle_type_getter");
    }
}
