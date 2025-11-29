package com.github.mdcdi1315.basemodslib.entity;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

import com.github.mdcdi1315.basemodslib.Contract;
import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.RegistryObjectNotFoundException;
import com.github.mdcdi1315.basemodslib.entity.effect.MobEffectRegistrationInfo;
import com.github.mdcdi1315.basemodslib.entity.sensing.SensorTypeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.entity.attributes.AttributeRegistrationInfo;
import com.github.mdcdi1315.basemodslib.entity.memory.MemoryModuleTypeRegistrationInfo;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

/**
 * Provides a way for registering entity types and more!
 * @since 1.0.3
 */
@Contract
public interface IEntityTypeRegistrar
{
    /**
     * Registers a new entity type to Minecraft.
     * @param name The name of the newly created entity type that will be registered.
     * @param info The entity type information that is used to register the entity.
     * @param <T> The type of the entity to register.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T extends Entity> void RegisterEntity(@ConstantExpected String name , EntityTypeRegistrationInfo<T> info) throws ArgumentNullException;

    /**
     * Registers a new memory module type to Minecraft. <br />
     * It is typically used in {@link net.minecraft.world.entity.ai.Brain} instances for defining entity memories.
     * @param name The name of the newly created memory module type that will be registered.
     * @param info The memory module type information that is used to register the new memory module type.
     * @param <T> The data type that this memory module type saves and reads.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T> void RegisterMemoryModuleType(@ConstantExpected String name , MemoryModuleTypeRegistrationInfo<T> info) throws ArgumentNullException;

    /**
     * Registers a new attribute to Minecraft.
     * @param name The name of the newly created attribute that will be registered.
     * @param info The attribute information that is used to register the attribute.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    void RegisterEntityAttribute(@ConstantExpected String name, AttributeRegistrationInfo info) throws ArgumentNullException;

    /**
     * Registers a new sensor type to Minecraft.
     * @param name The name of the newly created sensor type that will be registered.
     * @param info The sensor type information that is used to register the sensor type.
     * @param <T> The type of the sensor to register.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    <T extends Sensor<?>> void RegisterSensorType(@ConstantExpected String name, SensorTypeRegistrationInfo<T> info) throws ArgumentNullException;

    /**
     * Registers a new mob effect to Minecraft.
     * @param name The name of the newly created mob effect type that will be registered.
     * @param info The mob effect information that is used to register the mob effect type.
     * @throws ArgumentNullException {@code info} is {@code null}.
     */
    void RegisterMobEffect(@ConstantExpected String name, MobEffectRegistrationInfo info) throws ArgumentNullException;

    /**
     * Gets a previously registered entity type.
     * @param location A resource location specifying the location of the entity type.
     * @return The registered entity type.
     * @param <T> The type of the entity that is specified in the entity type registration.
     * @throws ArgumentNullException {@code location} is {@code null}.
     * @throws RegistryObjectNotFoundException The requested entity type passed by {@code location} is non-existent.
     */
    public static <T extends Entity> EntityType<T> GetEntityType(ResourceLocation location)
            throws ArgumentNullException, RegistryObjectNotFoundException
    {
        return (EntityType<T>) RegistryUtils.GetRegistryObjectChecked(BuiltInRegistries.ENTITY_TYPE , location);
    }

    /**
     * Gets a previously registered entity memory module type.
     * @param location A resource location specifying the location of the entity memory module type.
     * @return The registered entity type.
     * @param <T> The type of the entity that is specified in the entity type registration.
     * @throws ArgumentNullException {@code location} is {@code null}.
     * @throws RegistryObjectNotFoundException The requested entity type passed by {@code location} is non-existent.
     */
    public static <T> MemoryModuleType<T> GetMemoryModuleType(ResourceLocation location)
            throws ArgumentNullException, RegistryObjectNotFoundException
    {
        return (MemoryModuleType<T>) RegistryUtils.GetRegistryObjectChecked(BuiltInRegistries.MEMORY_MODULE_TYPE, location);
    }
}
