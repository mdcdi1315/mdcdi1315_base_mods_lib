package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.ForgeUtils;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.registries.*;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ForgeRegistriesRegistrar
    implements IRegistryRegistrar
{
    private String mod_id;
    private List<DeferredRegister<?>> registers;
    private List<DatapackRegistryEntry<?>> datapack_registries;
    private List<RegistryEntry<?>> registries_to_create;

    public ForgeRegistriesRegistrar(String mod_id) {
        this.mod_id = mod_id;
        registers = new List<>();
        datapack_registries = new List<>();
        registries_to_create = new List<>();
    }

    private record ROSRegister<T>(RegistryObjectSupplier<T> ts , ResourceLocation location)
        implements Supplier<T>
    {
        @Override
        public T get() {
            return ts.Get(location);
        }
    }

    private record RegistryEntry<T>(ResourceKey<Registry<T>> resource_key, Action1<IModLoaderRegistry<T>> on_ready) {}

    private record DatapackRegistryEntry<T>(ResourceKey<Registry<T>> resource_key, Codec<T> element_codec) {}

    private record OnRegistryFilledCallback<T>(Action1<IModLoaderRegistry<T>> action)
        implements Consumer<IForgeRegistry<T>>
    {
        @Override
        public void accept(IForgeRegistry<T> ts) {
            action.action(new ForgeRegistryWrappedInRegistry<>(ts));
        }
    }

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

    public <T> void RegisterObject(ResourceKey<Registry<T>> registry, String name, RegistryObjectSupplier<T> supplier)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(supplier, "supplier");
        ArgumentNullException.ThrowIfNull(registry, "registry");
        CreateIfAbsentOrReturn(registry).register(name, new ROSRegister<>(supplier , ResourceLocation.tryBuild(mod_id, name)));
    }

    @Override
    public <T> void RegisterObject(ResourceKey<Registry<T>> registry, String name, Supplier<T> supplier)
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
        event.create(
                new RegistryBuilder<T>()
                    .allowModification()
                    .setName(entry.resource_key.location()),
                new OnRegistryFilledCallback<>(entry.on_ready)
        );
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

    private <T> void RegisterEntryInternal(DatapackRegistryEntry<T> ent , DataPackRegistryEvent.NewRegistry reg) {
        reg.dataPackRegistry(ent.resource_key() , ent.element_codec());
    }

    private void RegisterDatapackRegistries(DataPackRegistryEvent.NewRegistry dre)
    {
        IEnumerator<DatapackRegistryEntry<?>> e = datapack_registries.GetEnumerator();
        try {
            while (e.MoveNext()) {
                RegisterEntryInternal(e.getCurrent() , dre);
            }
        } finally {
            e.Dispose();
        }
        datapack_registries = null; // We can now sweep up memory.
    }

    public void RegisterToEventBus(IEventBus evb)
    {
        var en = registers.GetEnumerator();
        try {
            while (en.MoveNext()) {
                en.getCurrent().register(evb);
            }
        } finally {
            en.Dispose();
        }
        // We can cleanup this list once all registers have made it to be registered to the mod event bus.
        registers = null;
        ForgeUtils.AddListener(evb , NewRegistryEvent.class , this::CreateRegistries);
        ForgeUtils.AddListener(evb , DataPackRegistryEvent.NewRegistry.class , this::RegisterDatapackRegistries);
    }
}
