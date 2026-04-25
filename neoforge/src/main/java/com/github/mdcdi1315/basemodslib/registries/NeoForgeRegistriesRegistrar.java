package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.NeoForgeUtils;
import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.RegistryNotFoundException;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import java.util.function.Function;

public final class NeoForgeRegistriesRegistrar
        implements IRegistryRegistrar
{
    private String mod_id;
    private SingleLinkedListBasedRegister<DeferredRegister<?>> registers;
    private SingleLinkedListBasedRegister<RegistryEntry<?>> registries_to_create;
    private SingleLinkedListBasedRegister<DatapackRegistryEntry<?>> datapack_registries;
    private SingleLinkedListBasedRegister<Pair<ResourceLocation, PreparableReloadListener>> data_reload_listeners;

    public NeoForgeRegistriesRegistrar(String mod_id) {
        this.mod_id = mod_id;
        registers = new SingleLinkedListBasedRegister<>();
        datapack_registries = new SingleLinkedListBasedRegister<>();
        registries_to_create = new SingleLinkedListBasedRegister<>();
        data_reload_listeners = new SingleLinkedListBasedRegister<>();
    }

    private record RegistryEntry<T>(ResourceKey<Registry<T>> resource_key, Action1<IModLoaderRegistry<T>> on_ready) {}

    private record DatapackRegistryEntry<T>(ResourceKey<Registry<T>> resource_key, Codec<T> element_codec) {}

    @SuppressWarnings("unchecked")
    private <T> DeferredRegister<T> CreateIfAbsentOrReturn(ResourceKey<? extends Registry<T>> registry_key)
    {
        var en = registers.GetEnumerator();
        try {
            DeferredRegister<?> register;
            while (en.MoveNext()) {
                if ((register = en.getCurrent()).getRegistryKey().equals(registry_key)) {
                    return (DeferredRegister<T>) register;
                }
            }
        } finally {
            en.Dispose();
        }
        // Enumeration finished and no register was found. Create a new one instead.
        DeferredRegister<T> t = DeferredRegister.create(registry_key , mod_id);
        registers.Register(t);
        return t;
    }

    private record DeferredRegisterImplementedBulkRegistryRegister<T>(DeferredRegister<T> reg)
        implements IBulkRegistryObjectRegister<T>
    {
        @Override
        public void Add(String name, T object)
                throws ArgumentNullException
        {
            ArgumentNullException.ThrowIfNull(name, "name");
            reg.register(name, new ElementSupplier<>(object));
        }
    }

    @Override
    public <T> IBulkRegistryObjectRegister<T> GetBulkRegister(ResourceKey<? extends Registry<T>> registry_resource_key)
            throws ArgumentNullException, RegistryNotFoundException
    {
        ArgumentNullException.ThrowIfNull(registry_resource_key, "registry_resource_key");
        return new DeferredRegisterImplementedBulkRegistryRegister<>(CreateIfAbsentOrReturn(registry_resource_key));
    }

    @Override
    public <T> void RegisterObject(ResourceKey<Registry<T>> registry, String name, RegistryObjectSupplier<T> supplier)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(supplier, "supplier");
        ArgumentNullException.ThrowIfNull(registry, "registry");
        CreateIfAbsentOrReturn(registry).register(name , supplier);
    }

    @Override
    public <T> void RegisterObject(ResourceKey<Registry<T>> registry, String name, Function<ResourceLocation, T> supplier)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(supplier, "supplier");
        ArgumentNullException.ThrowIfNull(registry, "registry");
        CreateIfAbsentOrReturn(registry).register(name , supplier);
    }

    @Override
    public <T> void RegisterRegistry(ResourceKey<Registry<T>> registryResourceKey, Action1<IModLoaderRegistry<T>> on_registry_ready)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(on_registry_ready, "on_registry_ready");
        ArgumentNullException.ThrowIfNull(registryResourceKey, "registryResourceKey");
        registries_to_create.Register(new RegistryEntry<>(registryResourceKey, on_registry_ready));
    }

    @Override
    public <T> void RegisterDatapackRegistry(ResourceKey<Registry<T>> registry_name, Codec<T> element_codec)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(registry_name, "registry_name");
        ArgumentNullException.ThrowIfNull(element_codec, "element_codec");
        datapack_registries.Register(new DatapackRegistryEntry<>(registry_name, element_codec));
    }

    @Override
    public void RegisterResourceReloadListener(String name, PreparableReloadListener preparable_reload_listener)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(preparable_reload_listener, "preparable_reload_listener");
        data_reload_listeners.Register(new Pair<>(RegistryUtils.ConstructResourceLocation(mod_id, name), preparable_reload_listener));
    }

    private static <T> void CreateRegistry(NewRegistryEvent event, RegistryEntry<T> entry)
    {
        Registry<T> registry = event.create(new RegistryBuilder<>(entry.resource_key).sync(false));
        entry.on_ready.action(new MinecraftWrappedModLoaderRegistry<>(registry));
    }

    private static <T> void CreateDatapackRegistry(DataPackRegistryEvent.NewRegistry event , DatapackRegistryEntry<T> entry)
    {
        event.dataPackRegistry(entry.resource_key , entry.element_codec);
    }

    private static void AddResourceReloadListener(AddServerReloadListenersEvent event, Pair<ResourceLocation, PreparableReloadListener> entry) { event.addListener(entry.first(), entry.second()); }

    public void RegisterToEventBus(IEventBus bus)
    {
        var registers_en = registers.GetEnumerator();
        try {
            while (registers_en.MoveNext()) {
                registers_en.getCurrent().register(bus);
            }
        } finally {
            registers_en.Dispose();
        }
        registers = null; // We can now sweep up memory.
        NeoForgeUtils.AddEnumerableListener(NeoForge.EVENT_BUS, AddServerReloadListenersEvent.class, data_reload_listeners, NeoForgeRegistriesRegistrar::AddResourceReloadListener);
        data_reload_listeners = null;
        NeoForgeUtils.AddEnumerableListener_DispatchOnce(bus, NewRegistryEvent.class, registries_to_create, NeoForgeRegistriesRegistrar::CreateRegistry);
        registries_to_create = null;
        NeoForgeUtils.AddEnumerableListener_DispatchOnce(bus, DataPackRegistryEvent.NewRegistry.class, datapack_registries, NeoForgeRegistriesRegistrar::CreateDatapackRegistry);
        datapack_registries = null;
        mod_id = null;
    }
}
