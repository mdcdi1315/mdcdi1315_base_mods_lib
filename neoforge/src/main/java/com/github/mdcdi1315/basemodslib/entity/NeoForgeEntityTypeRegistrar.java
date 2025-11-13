package com.github.mdcdi1315.basemodslib.entity;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.entity.sensing.SensorTypeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.entity.attributes.AttributeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.entity.memory.MemoryModuleTypeRegistrationInfo;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoForgeEntityTypeRegistrar
    implements IEntityTypeRegistrar
{
    private DeferredRegister<Attribute> ATTRIBUTE_REGISTER;
    private DeferredRegister.Entities ENTITY_TYPE_REGISTER;
    private DeferredRegister<SensorType<?>> SENSOR_TYPE_REGISTER;
    private DeferredRegister<MemoryModuleType<?>> MEM_MODULE_TYPE_REGISTER;

    public NeoForgeEntityTypeRegistrar(String mod_id)
    {
        ENTITY_TYPE_REGISTER = DeferredRegister.createEntities(mod_id);
        ATTRIBUTE_REGISTER = DeferredRegister.create(Registries.ATTRIBUTE , mod_id);
        SENSOR_TYPE_REGISTER = DeferredRegister.create(Registries.SENSOR_TYPE , mod_id);
        MEM_MODULE_TYPE_REGISTER = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE , mod_id);
    }

    @Override
    public <T extends Entity> void RegisterEntity(String name, EntityTypeRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        ENTITY_TYPE_REGISTER.register(name, info.entity_provider());
    }

    @Override
    public <T> void RegisterMemoryModuleType(String name, MemoryModuleTypeRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        MEM_MODULE_TYPE_REGISTER.register(name, new ElementSupplier<>(new MemoryModuleType<>(info.optional_codec())));
    }

    @Override
    public void RegisterEntityAttribute(String name, AttributeRegistrationInfo info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        ATTRIBUTE_REGISTER.register(name, info.attribute_getter());
    }

    @Override
    public <T extends Sensor<?>> void RegisterSensorType(String name, SensorTypeRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(info, "info");
        SENSOR_TYPE_REGISTER.register(name, info.sensor_type_getter());
    }

    public void RegisterToEventBus(IEventBus event_bus)
    {
        ATTRIBUTE_REGISTER.register(event_bus);
        ENTITY_TYPE_REGISTER.register(event_bus);
        SENSOR_TYPE_REGISTER.register(event_bus);
        MEM_MODULE_TYPE_REGISTER.register(event_bus);
        MEM_MODULE_TYPE_REGISTER = null;
        SENSOR_TYPE_REGISTER = null;
        ENTITY_TYPE_REGISTER = null;
        ATTRIBUTE_REGISTER = null;
    }
}
