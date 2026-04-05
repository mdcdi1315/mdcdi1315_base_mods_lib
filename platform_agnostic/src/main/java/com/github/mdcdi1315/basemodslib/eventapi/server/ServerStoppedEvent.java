package com.github.mdcdi1315.basemodslib.eventapi.server;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent;

import net.minecraft.server.MinecraftServer;

/**
 * Further complements the {@link ServerStoppingEvent} by providing an event instance for when a server has shut down completely.
 * @param server The minecraft server object that is stopped.
 * @since 1.0.25
 */
public record ServerStoppedEvent(@NotNull MinecraftServer server) implements IDestroyableIfUnusedEvent { }
