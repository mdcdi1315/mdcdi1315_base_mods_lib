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
 * The event manager acknowledges that and deletes the event and any bound handlers to it to reduce memory footprint.
 */
package com.github.mdcdi1315.basemodslib.eventapi;