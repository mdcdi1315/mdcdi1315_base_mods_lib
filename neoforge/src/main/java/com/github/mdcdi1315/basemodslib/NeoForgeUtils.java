package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import com.github.mdcdi1315.basemodslib.registries.IModLoaderRegistry;
import com.github.mdcdi1315.basemodslib.eventapi.mods.RegistryFinalizedEvent;

import net.minecraft.core.Registry;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.EventPriority;

import java.util.function.Consumer;

public final class NeoForgeUtils
{
    // Do not let anyone instantiate this class.
    private NeoForgeUtils() {}

    // Associates a 'registry bake' callback to a BML registry finalized event.
    public static <T> void AddRegistryBakeCallback(Registry<T> registry, Func2<IModLoaderRegistry<T>, RegistryFinalizedEvent<T>> registry_event_to_be_invoked) {
        registry.addCallback(new NFGUtils_BaseBakeCallbackImplementation<>(registry_event_to_be_invoked));
    }

    // Specifying directly the event class to register avoids doing a very computationally expensive class type lookup.
    public static <T extends Event> void AddListener(IEventBus event_bus, Class<T> event_class, Consumer<T> consumer)  {
        event_bus.addListener(EventPriority.NORMAL , false , event_class, consumer);
    }
}
