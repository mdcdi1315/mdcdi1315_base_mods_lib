package com.github.mdcdi1315.basemodslib.eventapi;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.mods.*;
import com.github.mdcdi1315.basemodslib.eventapi.server.*;
import com.github.mdcdi1315.basemodslib.eventapi.gameplay.*;
import com.github.mdcdi1315.basemodslib.utils.ReflectionUtils;
import com.github.mdcdi1315.basemodslib.eventapi.mods.registries.*;

import com.google.common.collect.ImmutableSet;

import org.jetbrains.annotations.ApiStatus;

/**
 * The actual events manager instance, used while the game is running. <br />
 * This manager is used by the mods, in fact.
 */
@ApiStatus.Internal
public class NormalEventsManager
    extends EventManagerBase
{
    private volatile boolean finalized;

    public NormalEventsManager() {
        super();
        finalized = false;
    }

    @Override
    protected boolean HasBeenFinalized() { return finalized; }

    protected void SetFinalized() {
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
        for (Class<?> i : ImmutableSet.copyOf(actions.keySet())) // Guava's copyOf is much better and faster than new HashSet<>(actions.keySet()).
        {
            // Running the below loop for each event could be a VERY HEAVY OPERATION. However, each event object registered is unique, so we check each time for different class objects, so this is OK and acceptable.
            for (Class<?> cls : ReflectionUtils.GetAllImplementedInterfaces(i))
            {
                if (cls == destroyable_if_unused) {
                    var list = actions.get(i);
                    if (list != null && list.getCount() == 0) {
                        actions.remove(i);
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

    private static <T extends IEvent> List<Action1<? extends IEvent>> ListProvider(Class<T> cls) {
        return new List<>(4);
    }

    public void HandEventsFromEarly(EarlyEventsManager early)
    {
        var c_actions = GetActions();
        synchronized (c_actions) {
            for (var kvp : early.GetActions().entrySet()) {
                c_actions.computeIfAbsent(kvp.getKey() , NormalEventsManager::ListProvider).AddRange(kvp.getValue());
            }
        }
    }
}
