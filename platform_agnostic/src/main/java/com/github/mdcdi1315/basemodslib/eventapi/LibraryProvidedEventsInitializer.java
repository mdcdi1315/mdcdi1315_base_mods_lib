package com.github.mdcdi1315.basemodslib.eventapi;

import com.github.mdcdi1315.DotNetLayer.System.Action1;

import com.github.mdcdi1315.basemodslib.eventapi.mods.*;
import com.github.mdcdi1315.basemodslib.eventapi.client.*;
import com.github.mdcdi1315.basemodslib.eventapi.server.*;
import com.github.mdcdi1315.basemodslib.eventapi.gameplay.*;
import com.github.mdcdi1315.basemodslib.eventapi.mods.registries.*;

// Provides a way to register all the events provided by the library.
final class LibraryProvidedEventsInitializer
{
    private LibraryProvidedEventsInitializer() {}

    public static void InitializeEvents(Action1<Class<? extends IEvent>> event_appender)
    {
        // Initial events
        event_appender.action(CommonSetupEvent.class);
        event_appender.action(ServerStartedEvent.class);
        event_appender.action(ServerStoppedEvent.class);
        event_appender.action(ServerStartingEvent.class);
        event_appender.action(ServerStoppingEvent.class);
        event_appender.action(ServerReloadedEvent.class);
        event_appender.action(RegistryFinalizedEvent.class);
        event_appender.action(ModLoadingCompleteEvent.class);
        event_appender.action(ServerResourcesReloadedEvent.class);
        event_appender.action(NewPlayerConnectedToServerEvent.class);
        event_appender.action(PlayerDisconnectedFromServerEvent.class);
        // Registry finalized events.
        // Note that all the below events will be removed once the mod loading complete event is dispatched.
        event_appender.action(ItemRegistryFinalizedEvent.class);
        event_appender.action(BlockRegistryFinalizedEvent.class);
        event_appender.action(FluidRegistryFinalizedEvent.class);
        event_appender.action(PotionRegistryFinalizedEvent.class);
        event_appender.action(MenuTypeRegistryFinalizedEvent.class);
        event_appender.action(EntityTypeRegistryFinalizedEvent.class);
        event_appender.action(SoundEventRegistryFinalizedEvent.class);
        event_appender.action(ParticleTypeRegistryFinalizedEvent.class);
        event_appender.action(EntityAttributeRegistryFinalizedEvent.class);
        event_appender.action(BlockEntityTypeRegistryFinalizedEvent.class);
        // Gameplay events.
        // Note that all the below events will be removed once the mod loading complete event is dispatched and mods are using them.
        event_appender.action(PlayerWasKilledEvent.class);
        event_appender.action(PlayerKilledEntityEvent.class);
        event_appender.action(PlayerRequestedRespawnEvent.class);
        event_appender.action(PlayerWillBeRewardedWithStatEvent.class);
    }

    public static void InitializeClientEvents(Action1<Class<? extends IEvent>> event_appender)
    {
        event_appender.action(ClientSetupEvent.class);
        event_appender.action(ClientStartedEvent.class);
        event_appender.action(ClientStoppingEvent.class);
        event_appender.action(ClientConnectedToServerEvent.class);
        event_appender.action(ClientDisconnectedFromServerEvent.class);
    }
}
