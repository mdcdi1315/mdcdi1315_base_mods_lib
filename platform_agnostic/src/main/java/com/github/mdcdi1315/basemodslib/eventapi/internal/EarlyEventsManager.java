package com.github.mdcdi1315.basemodslib.eventapi.internal;

import org.jetbrains.annotations.ApiStatus;

/**
 * This is the early events manager class. <br />
 * The events and their handlers are then handed out to the normal events manager once the mod loader layer has been initialized. <br />
 * Note: This is used by the mod loader layers if they need to listen to events. It is for the library's internal use.
 */
@ApiStatus.Internal
final class EarlyEventsManager
    extends EventManagerBase
    implements IBMLEventManager
{
    /**
     * Default constructor to ensure that super constructor is called in
     */
    public EarlyEventsManager() { super(); }

    // The below does nothing. That happens only by the normal events manager.
    @Override
    public void DestroyDestroyableEvents() { }

    @Override
    protected boolean HasBeenFinalized() { return false; }
}
