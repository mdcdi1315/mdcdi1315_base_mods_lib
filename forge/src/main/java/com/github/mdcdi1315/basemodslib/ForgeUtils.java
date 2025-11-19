package com.github.mdcdi1315.basemodslib;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;

/**
 * Some utilities for Forge. This is not to be used by your code.
 */
public final class ForgeUtils
{
    // Do not let anyone instantiate this class.
    private ForgeUtils() {}

    // Specifying directly the event class to register avoids doing a very computationally expensive class type lookup.
    public static <T extends Event> void AddListener(IEventBus event_bus, Class<T> event_class, Consumer<T> consumer)  {
        event_bus.addListener(EventPriority.NORMAL , false , event_class, consumer);
    }
}
