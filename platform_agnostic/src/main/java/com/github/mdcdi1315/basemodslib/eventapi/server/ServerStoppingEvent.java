package com.github.mdcdi1315.basemodslib.eventapi.server;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.basemodslib.eventapi.IEvent;
import net.minecraft.server.MinecraftServer;

/**
 * Fired when the Minecraft Server has started shutting down.
 * @param server The server that has started shutting down.
 */
public record ServerStoppingEvent(@NotNull MinecraftServer server) implements IEvent { }
