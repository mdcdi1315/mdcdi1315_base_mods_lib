package com.github.mdcdi1315.basemodslib.eventapi.server;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent;

import net.minecraft.server.level.ServerPlayer;

/**
 * Event firing when a previously connected player has been diconnected from the current Minecraft server, either if this is an integrated or a dedicated server instance.
 * @param player The player that was disconnected.
 */
public record PlayerDisconnectedFromServerEvent(@NotNull ServerPlayer player) implements IDestroyableIfUnusedEvent { }
