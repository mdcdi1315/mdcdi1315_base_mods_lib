package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.ICollection;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

import com.github.mdcdi1315.basemodslib.eventapi.mods.RegistryFinalizedEvent;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import net.minecraft.core.Registry;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public final class NeoForgeUtils
{
    // Do not let anyone instantiate this class.
    private NeoForgeUtils() {}

    // Associates a 'registry bake' callback to a BML registry finalized event.
    public static <T> void AddRegistryBakeCallback(Registry<T> registry, Func2<Registry<T>, RegistryFinalizedEvent<T>> registry_event_to_be_invoked) {
        registry.addCallback(new NFGUtils_BaseBakeCallbackImplementation<>(registry_event_to_be_invoked));
    }

    // Specifying directly the event class to register avoids doing a very computationally expensive class type lookup.
    public static <T extends Event> void AddListener(IEventBus event_bus, Class<T> event_class, Consumer<T> consumer)  {
        event_bus.addListener(EventPriority.NORMAL , false , event_class, consumer);
    }

    // Registers the DeferredRegister instance if and only if we have objects to register - otherwise, it is useless and we shouldn't add it.
    public static <T> void DeferredRegister_RegisterIfHasItems(IEventBus event_bus, DeferredRegister<T> register)
    {
        if (!register.getEntries().isEmpty()) { register.register(event_bus); }
    }

    private static <T> boolean AddEnumerableListener_ShouldRegister(IEnumerable<T> enumerable)
    {
        boolean register = true;
        if (enumerable instanceof SingleLinkedListBasedRegister<T> reg) { register = reg.HasItems(); }
        else if (enumerable instanceof ICollection<T> c) { register = c.getCount() > 0; }
        else if (enumerable instanceof ITraversableCollection<?> t) { register = t.GetCount() > 0; }
        return register;
    }

    // Enumerates through a collection and adds the elements of that collection to the event.
    // That transformation is covered by the input action which does add all the elements to it.
    // The method is also intelligent: if it can be proved that the enumerable does not contain any elements,
    // it will not add the event listener, and as such avoiding allocating additional memory for the event dispatch.
    // This does also clean the code somewhat in the registrars.
    public static <TEvent extends Event, TI> void AddEnumerableListener(IEventBus bus, Class<TEvent> event_class, IEnumerable<TI> enumerable, Action2<TEvent, TI> action)
    {
        if (AddEnumerableListener_ShouldRegister(enumerable)) {
            AddListener(bus, event_class, new NFGUtils_AddAllEnumerableElementsEventTransformer<>(enumerable, action));
        }
    }

    // Same as the above, but the above is for events that are dispatching for multiple times -
    // if you are using an event that dispatches only once, this will also clear out the collection and the action reference.
    // So, a memory optimization is again applied.
    public static <TEvent extends Event, TI> void AddEnumerableListener_DispatchOnce(IEventBus bus, Class<TEvent> event_class, IEnumerable<TI> enumerable, Action2<TEvent, TI> action)
    {
        if (AddEnumerableListener_ShouldRegister(enumerable)) {
            AddListener(bus, event_class, new NFGUtils_AddAllEnumerableElementsEventTransformer_Dispose<>(enumerable, action));
        }
    }
}
