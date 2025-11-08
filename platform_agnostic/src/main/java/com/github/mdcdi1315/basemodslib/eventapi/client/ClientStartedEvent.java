package com.github.mdcdi1315.basemodslib.eventapi.client;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.eventapi.IEvent;

import net.minecraft.client.Minecraft;

/**
 * Event that is fired when the Minecraft client has been started up successfully.
 * @param minecraft The Minecraft client object instance.
 */
public record ClientStartedEvent(@NotNull Minecraft minecraft) implements IEvent { }
