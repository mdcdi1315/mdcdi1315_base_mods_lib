package com.github.mdcdi1315.basemodslib.eventapi.client;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.eventapi.IEvent;

import net.minecraft.client.Minecraft;

public record ClientStartedEvent(@NotNull Minecraft minecraft) implements IEvent { }
