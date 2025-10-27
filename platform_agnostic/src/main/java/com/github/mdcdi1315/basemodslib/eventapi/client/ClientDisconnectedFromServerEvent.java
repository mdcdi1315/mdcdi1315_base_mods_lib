package com.github.mdcdi1315.basemodslib.eventapi.client;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.eventapi.IEvent;

import net.minecraft.network.chat.Component;

public record ClientDisconnectedFromServerEvent(@MaybeNull Component reason) implements IEvent { }
