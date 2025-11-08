package com.github.mdcdi1315.basemodslib.eventapi.client;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.eventapi.IEvent;

import net.minecraft.client.Minecraft;

/**
 * Event that is fired when the Minecraft client is about to close. <br />
 * Be noted that the instance is still valid when this event is fired.
 * @param minecraft The Minecraft client object instance.
 */
public record ClientStoppingEvent(@NotNull Minecraft minecraft) implements IEvent { }
