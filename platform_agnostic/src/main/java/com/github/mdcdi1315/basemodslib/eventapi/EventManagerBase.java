package com.github.mdcdi1315.basemodslib.eventapi;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.mods.*;
import com.github.mdcdi1315.basemodslib.eventapi.server.*;
import com.github.mdcdi1315.basemodslib.eventapi.gameplay.*;
import com.github.mdcdi1315.basemodslib.eventapi.mods.registries.*;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedList;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides base stuff used by all the deriving event managers. Not to be used by your code.
 */
@ApiStatus.Internal
abstract class EventManagerBase
    extends EventManager
{
    private Map<Class<? extends IEvent>, SingleLinkedList<Action1<? extends IEvent>>> actions;

    /**
     * Initializes a new instance of the {@link EventManagerBase} class.
     */
    public EventManagerBase()
    {
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
        AddEventFast(PotionRegistryFinalizedEvent.class);
        AddEventFast(MenuTypeRegistryFinalizedEvent.class);
        AddEventFast(EntityTypeRegistryFinalizedEvent.class);
        AddEventFast(SoundEventRegistryFinalizedEvent.class);
        AddEventFast(ParticleTypeRegistryFinalizedEvent.class);
        AddEventFast(EntityAttributeRegistryFinalizedEvent.class);
        AddEventFast(BlockEntityTypeRegistryFinalizedEvent.class);
        // Gameplay events.
        // Note that all the below events will be removed once the mod loading complete event is dispatched and mods are using them.
        AddEventFast(PlayerWasKilledEvent.class);
        AddEventFast(PlayerKilledEntityEvent.class);
        AddEventFast(PlayerRequestedRespawnEvent.class);
        AddEventFast(PlayerWillBeRewardedWithStatEvent.class);
    }

    // A variant for AddEvent method that just adds the event classes directly rather than checking whether those are actually registered.
    // This is only invoked in the events manager constructor.
    private <TEvent extends IEvent> void AddEventFast(Class<TEvent> cls)
    {
        ArgumentNullException.ThrowIfNull(cls, "cls");
        actions.put(cls, new SingleLinkedList<>());
    }

    @Override
    public <TEvent extends IEvent> void AddEventListener(Class<TEvent> event_class, Action1<TEvent> action)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(action, "action");
        ArgumentNullException.ThrowIfNull(event_class, "event_class");

        if (HasBeenFinalized()) {
            throw new InvalidOperationException("Cannot add event listeners after mod loading is complete!");
        }

        SingleLinkedList<Action1<? extends IEvent>> acts = actions.get(event_class);

        if (acts == null) {
            throw new InvalidOperationException(String.format("The event with type %s is not registered to this instance!", event_class.getName()));
        }

        synchronized (acts) {
            // We must be extremely careful when adding a new event handler to the list. Locking on the object is a relatively good idea.
            acts.Add(action);
        }
    }

    @StackTraceHidden
    @SuppressWarnings("unchecked")
    private <TEvent extends IEvent> void FireEventInternal(TEvent evt, @MaybeNull Object actions)
    {
        if (actions == null) {
            if (evt instanceof IDestroyableIfUnusedEvent) {
                // Will be null, meaning that the event was removed. We cannot throw.
                return;
            } else {
                // This shouldn't happen, you have fired an unknown event.
                throw new InvalidOperationException("Attempted to fire an event not yet registered!");
            }
        }
        var e = ((SingleLinkedList<Action1<TEvent>>)actions).GetEnumerator();
        try {
            while (e.MoveNext())
            {
                try {
                    e.getCurrent().action(evt);
                } catch (Exception ex) {
                    BaseModsLib.LOGGER.error("Cannot invoke event on one of the event handlers. Exception will be eaten." , ex);
                }
            }
        } finally {
            e.Dispose();
        }
    }

    @Override
    public <TEvent extends IEvent> void FireEvent(TEvent event_data)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(event_data, "event_data");
        FireEventInternal(event_data , actions.get(event_data.getClass()));
    }

    @Override
    public <TEvent extends IEvent> void AddEvent(Class<TEvent> cls)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(cls, "cls");
        if (HasBeenFinalized()) {
            throw new InvalidOperationException("Cannot add event types after mod loading is complete!");
        } else {
            synchronized (actions) {
                // Typically, events are added by the library, but mods may add their own as well. So locking on the object avoids to double-register an existing event class.
                actions.computeIfAbsent(cls, EventManagerBase::ListProvider);
            }
        }
    }

    private static <T extends IEvent> SingleLinkedList<Action1<? extends IEvent>> ListProvider(Class<T> cls) { return new SingleLinkedList<>(); }

    /**
     * Gets the map that is used to register actions. Used to gain access of the registered stuff for other event manager classes.
     * @return The backing map.
     */
    protected Map<Class<? extends IEvent>, SingleLinkedList<Action1<? extends IEvent>>> GetActions() { return actions; }

    /**
     * Gets a value whether the extending event manager instance has been finalized. Typically happens after the mod loading complete event has been fired.
     * @return A value whether the extending event manager instance has been finalized.
     */
    protected abstract boolean HasBeenFinalized();

    @Override
    public void DestroyManager() { actions = null; }
}
