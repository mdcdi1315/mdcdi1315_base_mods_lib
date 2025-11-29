package com.github.mdcdi1315.basemodslib.entity;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.entity.effect.MobEffectRegistrationInfo;
import com.github.mdcdi1315.basemodslib.entity.sensing.SensorTypeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.entity.attributes.AttributeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.entity.memory.MemoryModuleTypeRegistrationInfo;

import com.mojang.serialization.Codec;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.Optional;

public final class ForgeEntityTypeRegistrar
    implements IEntityTypeRegistrar
{
    private DeferredRegister<Attribute> ATTRIBUTES;
    private DeferredRegister<MobEffect> MOB_EFFECTS;
    private DeferredRegister<EntityType<?>> ENTITY_TYPES;
    private DeferredRegister<SensorType<?>> SENSOR_TYPES;
    private DeferredRegister<MemoryModuleType<?>> MEM_MODULE_TYPES;

    public ForgeEntityTypeRegistrar(String mod_id)
    {
        ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE , mod_id);
        MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, mod_id);
        SENSOR_TYPES = DeferredRegister.create(Registries.SENSOR_TYPE , mod_id);
        ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE , mod_id);
        MEM_MODULE_TYPES = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE , mod_id);
    }

    @Override
    public <T extends Entity> void RegisterEntity(String name, EntityTypeRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        ENTITY_TYPES.register(name , info.entity_provider());
    }

    private record MemoryModuleTypeCreator<T>(Optional<Codec<T>> optional_codec)
        implements Func1<MemoryModuleType<T>>
    {
        @Override
        public MemoryModuleType<T> function() {
            return new MemoryModuleType<>(optional_codec);
        }
    }

    @Override
    public <T> void RegisterMemoryModuleType(String name, MemoryModuleTypeRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        MEM_MODULE_TYPES.register(name, new MemoryModuleTypeCreator<>(info.optional_codec()));
    }

    @Override
    public void RegisterEntityAttribute(String name, AttributeRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        ATTRIBUTES.register(name, info.attribute_getter());
    }

    @Override
    public <T extends Sensor<?>> void RegisterSensorType(String name, SensorTypeRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        SENSOR_TYPES.register(name , info.sensor_type_getter());
    }

    @Override
    public void RegisterMobEffect(String name, MobEffectRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        MOB_EFFECTS.register(name, info.effect_getter());
    }

    public void RegisterToEventBus(IEventBus bus)
    {
        ATTRIBUTES.register(bus);
        MOB_EFFECTS.register(bus);
        ENTITY_TYPES.register(bus);
        SENSOR_TYPES.register(bus);
        MEM_MODULE_TYPES.register(bus);
        ATTRIBUTES = null;
        MOB_EFFECTS = null;
        SENSOR_TYPES = null;
        ENTITY_TYPES = null;
        MEM_MODULE_TYPES = null;
    }
}
