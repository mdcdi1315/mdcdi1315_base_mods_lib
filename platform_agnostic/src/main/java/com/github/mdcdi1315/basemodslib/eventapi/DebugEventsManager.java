package com.github.mdcdi1315.basemodslib.eventapi;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.ReflectionUtils;

import com.google.common.collect.ImmutableSet;

import org.jetbrains.annotations.ApiStatus;

/**
 * This is a variant of the normal events manager that is used only in development environments.
 */
@ApiStatus.Internal
public final class DebugEventsManager
    extends NormalEventsManager
{
    private List<Class<? extends IDestroyableIfUnusedEvent>> removed_events;

    /**
     * Creates a new instance of the debug events manager.
     */
    public DebugEventsManager() {
        super();
        removed_events = new List<>();
    }

    private record CheckEventWasRegisteredPredicate(Class<? extends IEvent> evt_class)
        implements Predicate<Class<? extends IDestroyableIfUnusedEvent>>
    {
        @Override
        public boolean predicate(Class<? extends IDestroyableIfUnusedEvent> obj) {
            return evt_class.equals(obj);
        }
    }

    @StackTraceHidden
    @SuppressWarnings("unchecked")
    private <TEvent extends IEvent> void FireEventInternal(TEvent evt, @MaybeNull Object actions)
    {
        if (actions == null) {
            // We are into the debug manager, so we need to check whether we fire correctly.
            // If we fire a destroyable event that was removed because no handlers were bound to, we need to verify that the event has been registered.
            if (evt instanceof IDestroyableIfUnusedEvent && removed_events.FindIndex(new CheckEventWasRegisteredPredicate(evt.getClass())) > -1)
            {
                // The fire is correct, so we can return safely
                return;
            } else {
                // This shouldn't happen, you have fired an unknown event.
                throw new InvalidOperationException("Attempted to fire an event not yet registered!");
            }
        }
        var e = ((List<Action1<TEvent>>)actions).GetEnumerator();
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
        BaseModsLib.LOGGER.debug("EVENTS_MANAGER: Dispatching event of type {}." , event_data.getClass().getName());
        FireEventInternal(event_data , GetActions().get(event_data.getClass()));
    }

    @Override
    public void DestroyDestroyableEvents()
    {
        // Events can still be added before the mod loading completed event has finished dispatching.
        SetFinalized();
        int removed = 0;
        var actions = GetActions();
        Class<?> destroyable = IDestroyableEvent.class,
                destroyable_if_unused = IDestroyableIfUnusedEvent.class;
        for (Class<?> i : ImmutableSet.copyOf(actions.keySet())) // Guava's copyOf is much better and faster than new HashSet<>(actions.keySet()).
        {
            // Running the below loop for each event could be a VERY HEAVY OPERATION. However, each event object registered is unique, so we check each time for different class objects, so this is OK and acceptable.
            for (Class<?> cls : ReflectionUtils.GetAllImplementedInterfaces(i))
            {
                if (cls == destroyable_if_unused) {
                    var list = actions.get(i);
                    if (list != null && list.getCount() == 0) {
                        actions.remove(i);
                        removed_events.Add((Class<? extends IDestroyableIfUnusedEvent>) i);
                        removed++;
                    }
                    break;
                } else if (cls == destroyable) {
                    actions.remove(i);
                    removed++;
                    break;
                }
            }
        }
        BaseModsLib.LOGGER.info("EVENTS_MANAGER: Successfully removed {} destroyable events" , removed);
    }

    @Override
    public void DestroyManager() {
        super.DestroyManager();
        removed_events = null;
    }
}
