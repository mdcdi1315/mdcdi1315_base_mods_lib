package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class NeoForgeRegistriesRegistrar
        implements IRegistryRegistrar
{
    private String mod_id;
    private List<DeferredRegister<?>> registers;
    private List<DatapackRegistryEntry<?>> datapack_registries;
    private List<RegistryEntry<?>> registries_to_create;

    public NeoForgeRegistriesRegistrar(String mod_id) {
        this.mod_id = mod_id;
        registers = new List<>();
        datapack_registries = new List<>();
        registries_to_create = new List<>();
    }

    private record RegistryEntry<T>(ResourceKey<Registry<T>> resource_key, Action1<IModLoaderRegistry<T>> on_ready) {}

    private record DatapackRegistryEntry<T>(ResourceKey<Registry<T>> resource_key, Codec<T> element_codec) {}

    @SuppressWarnings("unchecked")
    private <T> DeferredRegister<T> CreateIfAbsentOrReturn(ResourceKey<Registry<T>> registry_key)
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
        registers.Add(t);
        return t;
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
    public <T> void RegisterRegistry(ResourceKey<Registry<T>> registryResourceKey, Action1<IModLoaderRegistry<T>> on_registry_ready)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(registryResourceKey, "registryResourceKey");
        registries_to_create.Add(new RegistryEntry<>(registryResourceKey, on_registry_ready));
    }

    @Override
    public <T> void RegisterDatapackRegistry(ResourceKey<Registry<T>> registry_name, Codec<T> element_codec)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(registry_name, "registry_name");
        ArgumentNullException.ThrowIfNull(element_codec, "element_codec");
        datapack_registries.Add(new DatapackRegistryEntry<>(registry_name, element_codec));
    }

    private static <T> void CreateRegistry(NewRegistryEvent event, RegistryEntry<T> entry)
    {
        Registry<T> registry = event.create(new RegistryBuilder<>(entry.resource_key).sync(false));
        var on_ready_act = entry.on_ready;
        if (on_ready_act != null) {
            on_ready_act.action(new MinecraftWrappedModLoaderRegistry<>(registry));
        }
    }

    private void CreateRegistries(NewRegistryEvent nre)
    {
        var e = registries_to_create.GetEnumerator();
        try {
            while (e.MoveNext()) {
                CreateRegistry(nre, e.getCurrent());
            }
        } finally {
            e.Dispose();
        }
        registries_to_create = null; // We can now sweep up memory.
    }

    private static <T> void CreateDatapackRegistry(DataPackRegistryEvent.NewRegistry event , DatapackRegistryEntry<T> entry)
    {
        event.dataPackRegistry(entry.resource_key , entry.element_codec);
    }

    private void DatapackRegistries(DataPackRegistryEvent.NewRegistry event)
    {
        var datapacks_en = datapack_registries.GetEnumerator();
        try {
            while (datapacks_en.MoveNext()) {
                CreateDatapackRegistry(event , datapacks_en.getCurrent());
            }
        } finally {
            datapacks_en.Dispose();
        }
        datapack_registries = null; // We can now sweep up memory.
    }

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
        bus.addListener(this::CreateRegistries);
        bus.addListener(this::DatapackRegistries);
    }
}
