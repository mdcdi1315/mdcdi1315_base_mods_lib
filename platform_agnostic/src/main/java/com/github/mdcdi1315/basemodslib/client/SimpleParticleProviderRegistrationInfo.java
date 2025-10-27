package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.client.particle.ParticleProvider;

public record SimpleParticleProviderRegistrationInfo<PO extends ParticleOptions>(
        Func1<ParticleType<PO>> particle_type,
        ParticleProvider<PO> particle_provider
) {
    public SimpleParticleProviderRegistrationInfo {
        ArgumentNullException.ThrowIfNull(particle_type, "particle_type");
        ArgumentNullException.ThrowIfNull(particle_provider, "particle_provider");
    }
}
