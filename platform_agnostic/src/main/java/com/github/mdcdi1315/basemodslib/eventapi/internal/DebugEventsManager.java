package com.github.mdcdi1315.basemodslib.eventapi.internal;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.eventapi.*;
import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.ReflectionUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * This is a variant of the normal events manager that is used only in development environments.
 */
@ApiStatus.Internal
class DebugEventsManager
    extends NormalEventsManager
{
    private SingleLinkedListBasedRegister<Class<? extends IDestroyableIfUnusedEvent>> removed_events;

    /**
     * Creates a new instance of the debug events manager.
     */
    public DebugEventsManager()
    {
        super();
        removed_events = new SingleLinkedListBasedRegister<>();
    }

    private boolean CheckEventWasRegistered(Class<? extends IEvent> evt_class)
    {
        try (IEnumerator<Class<? extends IDestroyableIfUnusedEvent>> e = removed_events.GetEnumerator())
        {
            while (e.MoveNext()) { if (evt_class.equals(e.getCurrent())) { return true; } }
            return false;
        }
    }

    @StackTraceHidden
    private <TEvent extends IEvent> void FireEventInternal(TEvent evt, @MaybeNull Object actions)
    {
        if (actions == null)
        {
            // We are into the debug manager, so we need to check whether we fire correctly.
            // If we fire a destroyable event that was removed because no handlers were bound to, we need to verify that the event has been registered.
            if (evt instanceof IDestroyableIfUnusedEvent && CheckEventWasRegistered(evt.getClass())) {
                // The fire is correct, so we can return safely
                return;
            } else if (evt instanceof IDestroyedOnUseEvent) {
                // Single-use event, MUST NOT be dispatched a second time.
                throw new InvalidEventDispatchException(evt, "Attempted to dispatch an event that is meant to be dispatched only once!");
            } else {
                // This shouldn't happen, you have fired an unknown event.
                throw new InvalidEventDispatchException(evt, "Attempted to fire an event not yet registered!");
            }
        }
        CheckRecursiveEventFires(evt);
        FireEventHelper(evt, actions);
    }

    private static boolean StackWalkerCheckForRecursiveEventCall(Stream<StackWalker.StackFrame> stream)
    {
        boolean first_not_found = true;
        Iterator<StackWalker.StackFrame> iterator = stream.iterator();
        while (iterator.hasNext())
        {
            StackWalker.StackFrame frame = iterator.next();
            if (
                    EventManager.class.isAssignableFrom(frame.getDeclaringClass()) &&
                    frame.getMethodName().equals("FireEvent")
            ) {
                if (first_not_found) {
                    first_not_found = false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    @StackTraceHidden
    private static void CheckRecursiveEventFires(IEvent event)
    {
        if (event.getClass().isAnnotationPresent(PermitsRecursiveFiring.class)) { return; }
        if (StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(DebugEventsManager::StackWalkerCheckForRecursiveEventCall)) {
            throw new InvalidEventDispatchException(event, "Detected a recursive event firing sequence, which could lead to CPU starvation.");
        }
    }

    @Override
    public <TEvent extends IEvent> void FireEvent(TEvent event_data)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(event_data, "event_data");
        Class<? extends IEvent> event_class = event_data.getClass();
        BaseModsLib.LOGGER.info("EVENTS_MANAGER: Dispatching event of type {}." , event_class.getName());
        ByRefParameter<SingleLinkedListBasedRegister<Action1<? extends IEvent>>> acts = new ByRefParameter<>();
        Lock().lock();
        try {
            GetActions().TryGetValue(event_data.getClass(), acts);
        } finally {
            Lock().unlock();
        }
        FireEventInternal(event_data, acts.Value);
        IfDestroyedOnUseRemove(this, event_data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void DestroyDestroyableEvents()
    {
        // Events can still be added before the mod loading completed event has finished dispatching.
        SetFinalized();
        int removed = 0;
        var actions = GetActions();
        Class<?> destroyable = IDestroyableEvent.class,
                destroyable_if_unused = IDestroyableIfUnusedEvent.class;
        Lock().lock();
        try {
            SingleLinkedListBasedRegister<Class<? extends IEvent>> events_to_remove = new SingleLinkedListBasedRegister<>();
            try (var e = actions.GetEnumerator())
            {
                while (e.MoveNext())
                {
                    var ce = e.getCurrent();
                    var i = ce.getKey();
                    // Running the below loop for each event could be a VERY HEAVY OPERATION.
                    // However, each event object registered is unique, so we check each time for different class objects, so this is OK and acceptable.
                    for (Class<?> cls : ReflectionUtils.GetAllImplementedInterfaces(i))
                    {
                        if (cls == destroyable_if_unused) {
                            if ((!ce.getValue().HasItems())) { events_to_remove.Register(i); }
                            break;
                        } else if (cls == destroyable) {
                            events_to_remove.Register(i);
                            break;
                        }
                    }
                }
            }
            try (var e = events_to_remove.GetEnumerator())
            {
                while (e.MoveNext())
                {
                    var cls = e.getCurrent();
                    if (actions.Remove_Ordinal2(cls))
                    {
                        if (destroyable_if_unused.isAssignableFrom(cls))
                        {
                            removed_events.Register((Class<? extends IDestroyableIfUnusedEvent>)cls);
                        }
                        removed++;
                    }
                }
            }
            actions.TrimExcess();
        } finally {
            Lock().unlock();
        }
        BaseModsLib.LOGGER.info("EVENTS_MANAGER: Successfully removed {} destroyable events", removed);
    }

    @Override
    public void Dispose()
    {
        // Check whether we have memory leaks from non-dispatched single-use events.
        try (IEnumerator<Class<? extends IEvent>> e = GetActions().getKeys().GetEnumerator())
        {
            while (e.MoveNext())
            {
                var cls = e.getCurrent();
                if (IDestroyedOnUseEvent.class.isAssignableFrom(cls))
                {
                    BaseModsLib.LOGGER.warn("[DebugEventsManager] Detected memory leak on registered event class {}: This class was never dispatched in the lifetime of the event manager.", cls.getName());
                }
            }
        }
        super.Dispose();
        removed_events = null;
    }
}
