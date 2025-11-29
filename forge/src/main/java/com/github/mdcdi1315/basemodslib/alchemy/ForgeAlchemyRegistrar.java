package com.github.mdcdi1315.basemodslib.alchemy;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

public final class ForgeAlchemyRegistrar
    implements IAlchemyRegistrar
{
    private DeferredRegister<Potion> POTION_REGISTER;
    private DeferredRegister<ParticleType<?>> PARTICLE_TYPE_REGISTER;

    public ForgeAlchemyRegistrar(String mod_id)
    {
        POTION_REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, mod_id);
        PARTICLE_TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES , mod_id);
    }

    @Override
    public <T extends ParticleOptions> void RegisterParticleType(String name, ParticleTypeRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        PARTICLE_TYPE_REGISTER.register(name , info.particle_type_getter());
    }

    @Override
    public void RegisterPotion(String name, PotionRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        POTION_REGISTER.register(name, info.potion_getter());
    }

    public void RegisterToEventBus(IEventBus event_bus)
    {
        POTION_REGISTER.register(event_bus);
        PARTICLE_TYPE_REGISTER.register(event_bus);
        PARTICLE_TYPE_REGISTER = null;
        POTION_REGISTER = null;
    }
}
