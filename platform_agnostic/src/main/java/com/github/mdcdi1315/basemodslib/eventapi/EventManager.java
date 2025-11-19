package com.github.mdcdi1315.basemodslib.eventapi;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.mods.*;
import com.github.mdcdi1315.basemodslib.eventapi.server.*;
import com.github.mdcdi1315.basemodslib.utils.ReflectionUtils;
import com.github.mdcdi1315.basemodslib.eventapi.mods.registries.*;

import com.google.common.collect.ImmutableSet;

import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides the base API for managing events, that are reusable classes that hold actions to be executed when the instance is fired.
 */
public final class EventManager
{
    private volatile boolean finalized;
    private Map<Class<? extends IEvent>, List<Action1<? extends IEvent>>> actions;

    /**
     * Creates a new instance of the {@link EventManager} class. <br />
     * The events provided by this library are also registered.
     */
    public EventManager() {
        finalized = false;
        actions = new ConcurrentHashMap<>();

        // Initial events

        AddEventFast(CommonSetupEvent.class);
        AddEventFast(ServerStartedEvent.class);
        AddEventFast(ServerStartingEvent.class);
        AddEventFast(ServerStoppingEvent.class);
        AddEventFast(ServerReloadedEvent.class);
        AddEventFast(RegistryFinalizedEvent.class);
        AddEventFast(ModLoadingCompleteEvent.class);
        AddEventFast(ServerResourcesReloadedEvent.class);
        AddEventFast(NewPlayerConnectedToServerEvent.class);
        AddEventFast(PlayerDisconnectedFromServerEvent.class);
        // Registry finalized events.
        // Note that all the below events will be removed once the mod loading complete event is dispatched.
        AddEventFast(ItemRegistryFinalizedEvent.class);
        AddEventFast(BlockRegistryFinalizedEvent.class);
        AddEventFast(FluidRegistryFinalizedEvent.class);
        AddEventFast(MenuTypeRegistryFinalizedEvent.class);
        AddEventFast(EntityTypeRegistryFinalizedEvent.class);
        AddEventFast(BlockEntityTypeRegistryFinalizedEvent.class);
    }

    /**
     * Adds an event listener to listen for the associated event provided through type {@link TEvent}.
     * @param event_class The class object of the event to add the event listener to.
     * @param action The method to invoke when the event of type {@link TEvent} fires.
     * @param <TEvent> The type of the event to listen for.
     * @throws ArgumentNullException {@code action} was {@code null}.
     * @throws InvalidOperationException Event listeners cannot be added after mod loading is complete.
     */
    public <TEvent extends IEvent> void AddEventListener(Class<TEvent> event_class, Action1<TEvent> action)
            throws ArgumentNullException , InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(action, "action");
        ArgumentNullException.ThrowIfNull(event_class, "event_class");

        if (finalized) {
            throw new InvalidOperationException("Cannot add event listeners after mod loading is complete!");
        }

        List<Action1<? extends IEvent>> acts = actions.get(event_class);

        if (acts == null) {
            throw new InvalidOperationException(String.format("The event with type %s is not registered to this instance!", event_class.getName()));
        }

        synchronized (acts) {
            // We must be extremely careful when adding a new event handler to the list. Locking on the object is a relatively good idea.
            acts.Add(action);
        }
    }

    /**
     * Fires a previously added event.
     * @param event_data The event data to send among all the registered event listeners.
     * @param <TEvent> The type of the event to fire.
     * @throws ArgumentNullException {@code event_data} was {@code null}.
     * @throws InvalidOperationException The event type specified through {@code event_data} has not been registered yet with the {@link #AddEvent(Class)} method.
     */
    @SuppressWarnings("unchecked")
    public <TEvent extends IEvent> void FireEvent(TEvent event_data)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(event_data, "event_data");
        var list = actions.get(event_data.getClass());
        if (list == null) {
            throw new InvalidOperationException("Attempted to fire an event not yet registered!");
        }
        var e = list.GetEnumerator();
        try {
            while (e.MoveNext())
            {
                try {
                    ((Action1<TEvent>)e.getCurrent()).action(event_data);
                } catch (Exception ex) {
                    BaseModsLib.LOGGER.error("Cannot invoke event on one of the event handlers. Exception will be eaten." , ex);
                }
            }
        } finally {
            e.Dispose();
        }
    }

    // A variant for AddEvent method that just adds the event classes directly rather than checking whether those are actually registered.
    // This is only invoked in the events manager constructor.
    private <TEvent extends IEvent> void AddEventFast(Class<TEvent> cls)
    {
        ArgumentNullException.ThrowIfNull(cls, "cls");
        actions.put(cls, new List<>(4));
    }

    /**
     * Registers a new event class that can be subsequently fired.
     * @param cls The event class that can be considered as an event.
     * @param <TEvent> The type of the event to be added.
     * @throws ArgumentNullException {@code cls} is {@code null}.
     * @throws InvalidOperationException New event types cannot be registered after mod loading is completed.
     */
    public <TEvent extends IEvent> void AddEvent(Class<TEvent> cls)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(cls, "cls");
        if (finalized) {
            throw new InvalidOperationException("Cannot add event types after mod loading is complete!");
        }
        synchronized (actions) {
            // Typically, events are added by the library, but mods may add their own as well. So locking on the object avoids to double-register an existing event class.
            actions.computeIfAbsent(cls, EventManager::ListProvider);
        }
    }

    private static <T extends IEvent> List<Action1<? extends IEvent>> ListProvider(Class<T> cls) {
        return new List<>(4);
    }

    /**
     * Removes events from the event manager that have been marked with the {@link IDestroyableEvent} interface instead. <br />
     * This will be called by the mod loader; it is not to be called by your code.
     */
    @ApiStatus.Internal
    public void DestroyDestroyableEvents()
    {
        // Events can still be added before the mod loading completed event is completed.
        finalized = true;
        int removed = 0;
        Class<?> destroyable = IDestroyableEvent.class;
        for (var i : ImmutableSet.copyOf(actions.keySet())) { // Guava's copyOf is much better and faster than new HashSet<>(actions.keySet()).
            // Running the below function could be a VERY HEAVY OPERATION. However, each event object registered is unique, so we check each time for different class objects, so this is OK and acceptable.
            if (ReflectionUtils.ImplementsInterface(i , destroyable)) { actions.remove(i); removed++; }
        }
        BaseModsLib.LOGGER.info("EVENTS_MANAGER: Successfully removed {} destroyable events" , removed);
    }

    /**
     * Destroys this event manager. <br />
     * Called when the Minecraft App is closing. <br />
     * Do not call this by your user code.
     */
    @ApiStatus.Internal
    public void DestroyManager() {
        actions = null;
    }
}
