package com.github.mdcdi1315.basemodslib.eventapi.client;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.eventapi.IEvent;

import net.minecraft.network.chat.Component;

/**
 * Event that is fired when the current Minecraft client instance is disconnected from the attached server. <br />
 * If the disconnection was done due to an error, the {@link #reason} field will be filled out containing the error message.
 * @param reason The error message, if any. If {@code null}, it means that the client was diconnected at its own will.
 */
public record ClientDisconnectedFromServerEvent(@MaybeNull Component reason) implements IEvent { }
