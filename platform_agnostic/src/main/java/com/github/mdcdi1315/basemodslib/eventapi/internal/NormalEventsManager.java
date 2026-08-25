package com.github.mdcdi1315.basemodslib.eventapi.internal;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.Action1;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.IEvent;
import com.github.mdcdi1315.basemodslib.utils.ReflectionUtils;
import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableEvent;
import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

import org.jetbrains.annotations.ApiStatus;

/**
 * The actual events manager instance, used while the game is running. <br />
 * This manager is used by the mods, in fact.
 */
@ApiStatus.Internal
class NormalEventsManager
    extends EventManagerBase
{
    private volatile boolean finalized;

    public NormalEventsManager() { super(); finalized = false; }

    protected void SetFinalized()
    {
        // Events can still be added before the mod loading completed event has finished dispatching.
        finalized = true;
    }

    /**
     * Removes events from the event manager that can be destroyed. This helps to better manage the game's memory footprint. <br />
     * This will be called by the mod loader; it is not to be called by your code.
     */
    @ApiStatus.Internal
    public void DestroyDestroyableEvents()
    {
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
                    if (actions.Remove_Ordinal2(e.getCurrent()))
                    {
                        removed++;
                    }
                }
            }
            actions.TrimExcess();
        } finally {
            Lock().unlock();
        }
        BaseModsLib.LOGGER.info("EVENTS_MANAGER: Successfully removed {} destroyable events" , removed);
    }

    public void HandEventsFromEarly(EarlyEventsManager early)
    {
        var c_actions = GetActions();
        Lock().lock();
        try {
            ByRefParameter<SingleLinkedListBasedRegister<Action1<? extends IEvent>>> p = new ByRefParameter<>();
            SingleLinkedListBasedRegister<Action1<? extends IEvent>> actions;
            try (var en = early.GetActions().GetEnumerator())
            {
                while (en.MoveNext())
                {
                    var kvp = en.getCurrent();
                    var event = kvp.getKey();
                    if (c_actions.TryGetValue(event, p)) {
                        actions = p.Value;
                    } else {
                        c_actions.Add(event, actions = new SingleLinkedListBasedRegister<>());
                    }
                    actions.RegisterRange(kvp.getValue());
                }
            }
        } finally {
            Lock().unlock();
        }
    }

    @Override
    protected boolean HasBeenFinalized() { return finalized; }
}
