package com.github.mdcdi1315.basemodslib.mods;

import com.github.mdcdi1315.basemodslib.eventapi.EventManager;

/**
 * Defines the client-side mod instance. <br />
 * The mod instance is expected to be initialized and finally destroyed by using the {@link #Dispose()} method.
 */
public interface IClientModInstance
    extends IModInstance
{
    /**
     * Registers events to be listened on the current client mod instance.
     * @param manager The events manager object to use.
     */
    default void RegisterEvents(EventManager manager) {}
}
