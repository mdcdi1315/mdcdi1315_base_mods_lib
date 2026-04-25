package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.ForgeUtils;
import com.github.mdcdi1315.basemodslib.utils.ElementSupplier;
import com.github.mdcdi1315.basemodslib.RegistryNotFoundException;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import net.minecraftforge.registries.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ForgeRegistriesRegistrar
    implements IRegistryRegistrar
{
    private String mod_id;
    private SingleLinkedListBasedRegister<DeferredRegister<?>> registers;
    private SingleLinkedListBasedRegister<RegistryEntry<?>> registries_to_create;
    private SingleLinkedListBasedRegister<DatapackRegistryEntry<?>> datapack_registries;
    private SingleLinkedListBasedRegister<PreparableReloadListener> data_reload_listeners;

    public ForgeRegistriesRegistrar(String mod_id)
    {
        this.mod_id = mod_id;
        registers = new SingleLinkedListBasedRegister<>();
        datapack_registries = new SingleLinkedListBasedRegister<>();
        registries_to_create = new SingleLinkedListBasedRegister<>();
        data_reload_listeners = new SingleLinkedListBasedRegister<>();
    }

    private record ROSRegister<T>(RegistryObjectSupplier<T> ts , ResourceLocation location)
        implements Supplier<T>
    {
        @Override
        public T get() { return ts.Get(location); }
    }

    private record RegistryEntry<T>(ResourceKey<Registry<T>> resource_key, Action1<IModLoaderRegistry<T>> on_ready) {}

    private record DatapackRegistryEntry<T>(ResourceKey<Registry<T>> resource_key, Codec<T> element_codec) {}

    private record OnRegistryFilledCallback<T>(Action1<IModLoaderRegistry<T>> action)
        implements Consumer<IForgeRegistry<T>>
    {
        @Override
        public void accept(IForgeRegistry<T> ts) { action.action(new ForgeRegistryWrappedInRegistry<>(ts)); }
    }

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

    public <T> void RegisterObject(ResourceKey<Registry<T>> registry, String name, RegistryObjectSupplier<T> supplier)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(supplier, "supplier");
        ArgumentNullException.ThrowIfNull(registry, "registry");
        CreateIfAbsentOrReturn(registry).register(name, new ROSRegister<>(supplier , RegistryUtils.ConstructResourceLocation(mod_id, name)));
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
        data_reload_listeners.Register(preparable_reload_listener);
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

    private static <T> void RegisterDataPackEntryInternal(DataPackRegistryEvent.NewRegistry reg, DatapackRegistryEntry<T> ent) { reg.dataPackRegistry(ent.resource_key() , ent.element_codec()); }

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
        ForgeUtils.AddEnumerableListener_DispatchOnce(evb, NewRegistryEvent.class, registries_to_create, ForgeRegistriesRegistrar::CreateRegistry);
        // With the above code, we can disown the reference.
        // The DispatchOnce method will ensure after the event's dispatch that it will destroy the reference.
        // See that method for more information.
        registries_to_create = null;
        ForgeUtils.AddEnumerableListener_DispatchOnce(evb, DataPackRegistryEvent.NewRegistry.class, datapack_registries, ForgeRegistriesRegistrar::RegisterDataPackEntryInternal);
        datapack_registries = null;
        // We can't sweep up memory here - the data may be reloaded many times.
        ForgeUtils.AddEnumerableListener(MinecraftForge.EVENT_BUS, AddReloadListenerEvent.class, data_reload_listeners, AddReloadListenerEvent::addListener);
        data_reload_listeners = null; // However, we can disown the reference.
        mod_id = null;
    }
}
