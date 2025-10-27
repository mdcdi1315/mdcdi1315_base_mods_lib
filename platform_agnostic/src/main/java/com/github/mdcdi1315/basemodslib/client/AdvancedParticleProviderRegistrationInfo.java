package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.client.particle.ParticleProvider;

public record AdvancedParticleProviderRegistrationInfo<PO extends ParticleOptions>(
        Func1<ParticleType<PO>> particle_type,
        Func2<SpriteSet , ParticleProvider<PO>> particle_provider_creater
) {
    public AdvancedParticleProviderRegistrationInfo {
        ArgumentNullException.ThrowIfNull(particle_type, "particle_type");
        ArgumentNullException.ThrowIfNull(particle_provider_creater, "particle_provider_creater");
    }
}
