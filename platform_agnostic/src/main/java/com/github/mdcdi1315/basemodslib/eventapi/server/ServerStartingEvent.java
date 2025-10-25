package com.github.mdcdi1315.basemodslib.eventapi.server;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.basemodslib.eventapi.IEvent;
import net.minecraft.server.MinecraftServer;

public record ServerStartingEvent(@NotNull MinecraftServer server) implements IEvent { }
