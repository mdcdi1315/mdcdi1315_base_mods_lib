package com.github.mdcdi1315.basemodslib.eventapi.client;

import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent;

/**
 * Fired when the client has connected to a server, either if this is the integrated server or a multiplayer server.
 */
public record ClientConnectedToServerEvent() implements IDestroyableIfUnusedEvent {}
