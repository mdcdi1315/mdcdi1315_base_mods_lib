package com.github.mdcdi1315.basemodslib.eventapi.internal;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.Dictionary;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.eventapi.*;
import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.ModdingEnvironment;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Provides base stuff used by all the deriving event managers. Not to be used by your code.
 */
@ApiStatus.Internal
abstract class EventManagerBase
    extends EventManager
{
    private final ReentrantLock lock;
    private Dictionary<Class<? extends IEvent>, SingleLinkedListBasedRegister<Action1<? extends IEvent>>> actions;

    /**
     * Initializes a new instance of the {@link EventManagerBase} class.
     */
    public EventManagerBase()
    {
        lock = new ReentrantLock();
        actions = new Dictionary<>();

        if (this instanceof IBMLEventManager)
        {
            EventAPIHelpers.InitializeEvents(this::AddEventFast);

            if (BaseModsLib.GetEnvironment() == ModdingEnvironment.CLIENT) {
                EventAPIHelpers.InitializeClientEvents(this::AddEventFast);
            }
        }
    }

    // A variant for AddEvent method that just adds the event classes directly rather than checking whether those are actually registered.
    // This is only invoked in the events manager constructor.
    private <TEvent extends IEvent> void AddEventFast(Class<TEvent> cls)
    {
        ArgumentNullException.ThrowIfNull(cls, "cls");
        actions.Add(cls, new SingleLinkedListBasedRegister<>());
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

        SingleLinkedListBasedRegister<Action1<? extends IEvent>> acts;
        lock.lock();
        try {
            ByRefParameter<SingleLinkedListBasedRegister<Action1<? extends IEvent>>> delegate_list = new ByRefParameter<>();
            if (!actions.TryGetValue(event_class, delegate_list))
            {
                throw new InvalidOperationException(String.format("The event with type %s is not registered to this instance!", event_class.getName()));
            }
            acts = delegate_list.Value;
        } finally {
            lock.unlock();
        }

        synchronized (acts) {
            // We must be extremely careful when adding a new event handler to the list. Locking on the object is a relatively good idea.
            acts.Register(action);
        }
    }

    @StackTraceHidden
    private static <TEvent extends IEvent> void FireEventInternal(TEvent evt, @MaybeNull Object actions)
    {
        if (actions == null)
        {
            if (evt instanceof IDestroyableIfUnusedEvent) {
                // This means that the event was removed. We cannot throw.
                return;
            } else if (evt instanceof IDestroyedOnUseEvent) {
                // Single-use event, MUST NOT be dispatched a second time.
                throw new InvalidEventDispatchException(evt, "Attempted to dispatch an event that is meant to be dispatched only once!");
            } else {
                // This shouldn't happen, you have fired an unknown event.
                throw new InvalidEventDispatchException(evt, "Attempted to fire an event not yet registered!");
            }
        }
        FireEventHelper(evt, actions);
    }

    @StackTraceHidden
    @SuppressWarnings("unchecked")
    public static <TEvent extends IEvent> void FireEventHelper(TEvent evt, @DisallowNull Object actions)
    {
        try (var e = ((SingleLinkedListBasedRegister<Action1<TEvent>>)actions).GetEnumerator())
        {
            while (e.MoveNext())
            {
                try {
                    e.getCurrent().action(evt);
                } catch (Exception ex) {
                    BaseModsLib.LOGGER.error("Cannot invoke event on one of the event handlers. Exception will be eaten." , ex);
                }
            }
        }
    }

    public static void IfDestroyedOnUseRemove(EventManagerBase manager, IEvent evt)
    {
        boolean result;
        manager.lock.lock();
        try {
            result = manager.actions.Remove_Ordinal2(evt.getClass());
        } finally {
            manager.lock.unlock();
        }
        if (evt instanceof IDestroyedOnUseEvent && (!result))
        {
            throw new InvalidEventDispatchException(evt, "Attempted to dispatch an event that was already used once!");
        }
    }

    @Override
    public <TEvent extends IEvent> void FireEvent(TEvent event_data)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(event_data, "event_data");
        ByRefParameter<SingleLinkedListBasedRegister<Action1<? extends IEvent>>> acts = new ByRefParameter<>();
        lock.lock();
        try {
            GetActions().TryGetValue(event_data.getClass(), acts);
        } finally {
            lock.unlock();
        }
        FireEventInternal(event_data, acts.Value);
        IfDestroyedOnUseRemove(this, event_data);
    }

    @Override
    public <TEvent extends IEvent> void AddEvent(Class<TEvent> cls)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(cls, "cls");
        if (HasBeenFinalized()) {
            throw new InvalidOperationException("Cannot add event types after mod loading is complete!");
        } else {
            lock.lock();
            try {
                // Typically, events are added by the library, but mods may add their own as well. So locking on the object avoids to double-register an existing event class.
                actions.TryAdd(cls, new SingleLinkedListBasedRegister<>());
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Gets the {@link ReentrantLock} that this event manager uses to ensure thread safety.
     * @return The {@link ReentrantLock} that this event manager is using.
     */
    protected Lock Lock() { return lock; }

    /**
     * Gets the map that is used to register actions. Used to gain access of the registered stuff for other event manager classes.
     * @return The backing map.
     */
    protected Dictionary<Class<? extends IEvent>, SingleLinkedListBasedRegister<Action1<? extends IEvent>>> GetActions() { return actions; }

    /**
     * Gets a value whether the extending event manager instance has been finalized. Typically happens after the mod loading complete event has been fired.
     * @return A value whether the extending event manager instance has been finalized.
     */
    protected abstract boolean HasBeenFinalized();

    @Override
    public void Dispose() { actions = null; }
}
