package com.github.mdcdi1315.basemodslib.eventapi.server;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent;

import net.minecraft.server.MinecraftServer;

/**
 * Fired when the server's resources have been reloaded.
 * @param server The server object that reloading was performed on.
 */
public record ServerReloadedEvent(@NotNull MinecraftServer server) implements IDestroyableIfUnusedEvent { }
