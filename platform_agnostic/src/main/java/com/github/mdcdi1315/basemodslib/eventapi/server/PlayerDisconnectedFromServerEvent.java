package com.github.mdcdi1315.basemodslib.eventapi.server;

import com.github.mdcdi1315.basemodslib.eventapi.IEvent;

import net.minecraft.server.level.ServerPlayer;

public record PlayerDisconnectedFromServerEvent(ServerPlayer player) implements IEvent { }
