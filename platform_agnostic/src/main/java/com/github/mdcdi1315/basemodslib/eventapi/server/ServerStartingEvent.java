package com.github.mdcdi1315.basemodslib.eventapi.server;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent;

import net.minecraft.server.MinecraftServer;

/**
 * Event firing when a new Minecraft server is ready to start.
 * @param server The server object that is ready to start.
 */
public record ServerStartingEvent(@NotNull MinecraftServer server) implements IDestroyableIfUnusedEvent { }
