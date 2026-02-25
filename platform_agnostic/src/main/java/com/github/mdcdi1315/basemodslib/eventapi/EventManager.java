package com.github.mdcdi1315.basemodslib.eventapi;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides the base API for managing events, that are reusable classes that hold actions to be executed when the instance is fired. <br />
 * The class has now became abstract in order to avoid loading issues with the layer itself.
 */
public abstract class EventManager
    implements ISynchronized
{
    /**
     * Adds an event listener to listen for the associated event provided through type {@link TEvent}.
     * @param event_class The class object of the event to add the event listener to.
     * @param action The method to invoke when the event of type {@link TEvent} fires.
     * @param <TEvent> The type of the event to listen for.
     * @throws ArgumentNullException {@code action} was {@code null}.
     * @throws InvalidOperationException Event listeners cannot be added after mod loading is complete.
     */
    public abstract <TEvent extends IEvent> void AddEventListener(Class<TEvent> event_class, Action1<TEvent> action) throws ArgumentNullException , InvalidOperationException;

    /**
     * Fires a previously added event. <br />
     * For events marked with the {@link IDestroyableIfUnusedEvent} class, they will be ignored if they are removed. <br />
     * Additionally, be careful using them: The event manager cannot detect which events were removed. So check whether registration is actually done.
     * @param event_data The event data to send among all the registered event listeners.
     * @param <TEvent> The type of the event to fire.
     * @throws ArgumentNullException {@code event_data} was {@code null}.
     * @throws InvalidOperationException The event type specified through {@code event_data} has not been registered yet with the {@link #AddEvent(Class)} method.
     */
    public abstract <TEvent extends IEvent> void FireEvent(TEvent event_data) throws ArgumentNullException, InvalidOperationException;

    /**
     * Registers a new event class that can be subsequently fired.
     * @param cls The event class that can be considered as an event.
     * @param <TEvent> The type of the event to be added.
     * @throws ArgumentNullException {@code cls} is {@code null}.
     * @throws InvalidOperationException New event types cannot be registered after mod loading is completed.
     */
    public abstract <TEvent extends IEvent> void AddEvent(Class<TEvent> cls) throws ArgumentNullException, InvalidOperationException;

    /**
     * Removes events from the event manager that can be destroyed. This helps to better manage the game's memory footprint. <br />
     * This will be called by the mod loader; it is not to be called by your code.
     */
    @ApiStatus.Internal
    public abstract void DestroyDestroyableEvents();

    /**
     * Destroys this event manager. <br />
     * Called when the Minecraft App is closing. <br />
     * Do not call this by your user code.
     */
    @ApiStatus.Internal
    public abstract void DestroyManager();
}
