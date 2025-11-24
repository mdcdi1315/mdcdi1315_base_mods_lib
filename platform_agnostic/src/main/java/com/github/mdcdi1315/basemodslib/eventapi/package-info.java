/**
 * Provides the Base Mods Library Event API.
 * <h3>What is the Event API?</h3>
 *
 * The Event API provides a way to register and listen to events - that is, something
 * important happening during a Minecraft session and it is useful to listen to - for
 * example, when a player connects to a Minecraft server. <br />
 *
 * Most of the API is implemented through the {@link com.github.mdcdi1315.basemodslib.eventapi.EventManager} class,
 * that manages the creation and dispatch of an event. <br />
 *
 * Apart from the default and provided events, you can also
 * register to and listen to your own events, if you deem you need them. <br />
 *
 * To implement your own event, you need to create a class extending
 * from the marker interface {@link com.github.mdcdi1315.basemodslib.eventapi.IEvent}. <br />
 * Important is to note down that no events can be registered once the mod loading completed event has been fired and completed. <br />
 *
 * There is also the {@link com.github.mdcdi1315.basemodslib.eventapi.IDestroyableEvent} interface,
 * another marker interface extending from the {@code IEvent} interface to indicate that the event
 * and it's handlers are not needed after mod loading is complete. <br />
 * It is useful for cases where you want to dispatch events before mod loading but after loading is
 * complete, you no longer fire the event.
 * The event manager acknowledges that and deletes the event and any bound handlers to it to reduce memory footprint. <br /> <br />
 *
 * Starting from 1.0.11, a new event interface was added: The {@link com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent} interface. <br />
 * That particular interface works and has the same effects as the {@link com.github.mdcdi1315.basemodslib.eventapi.IDestroyableEvent} interface,
 * but with one important distinction: The event will be removed if and only if no handlers are bound to it. <br />
 * However, this creates an important source of new issues: <br />
 * Events of such type cannot be detected that are actually registered.
 * Just because it will not throw an exception when dispatching an event of the type to avoid hard failures,
 * there is no way to detect this without putting more memory overhead. <br />
 * If you find yourself into the issue where your event is not dispatching, and you are implementing from the interface,
 * temporarily switch to the good and old {@link com.github.mdcdi1315.basemodslib.eventapi.IEvent} class and this should
 * pinpoint whether your event is actually registered at some time or not. <br />
 * Note: If you are using one of the library's events that implement the interface, assume that they are not plagued by the above issue. <br />
 * Additionally, in development environments a modified events manager takes action to actually perform the check and avoid the issue being propagated to production environments.
 */
package com.github.mdcdi1315.basemodslib.eventapi;