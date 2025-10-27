package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientConnectedToServerEvent;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientDisconnectedFromServerEvent;

import net.minecraft.client.multiplayer.ClientPacketListener;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    @Unique
    private boolean hard_disconnection = false;

    @Accessor("connection")
    protected abstract Connection GetConnection();

    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void OnConnectedSuccessfully(ClientboundLoginPacket packet, CallbackInfo ci)
    {
        BaseModsLib.LOGGER.debug("EVENTS_MANAGER: Successfully connected to the server {}. Dispatching server connection event." , GetConnection().getRemoteAddress());
        BaseModsLib.GetEventsManager().FireEvent(new ClientConnectedToServerEvent());
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void OnDisconnected(Component reason, CallbackInfo ci)
    {
        hard_disconnection = true;
        BaseModsLib.LOGGER.debug("EVENTS_MANAGER: Received abrupt disconnection event. Dispatching server disconnection event.");
        BaseModsLib.GetEventsManager().FireEvent(new ClientDisconnectedFromServerEvent(reason));
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void OnPlayerDisconnectedAllOK(CallbackInfo ci)
    {
        if (hard_disconnection) {
            BaseModsLib.LOGGER.warn("EVENTS_MANAGER: Cowardly refusing to send successful disconnection event!");
            hard_disconnection = false;
        } else {
            BaseModsLib.LOGGER.debug("EVENTS_MANAGER: Received disconnection event. Dispatching server disconnection event.");
            BaseModsLib.GetEventsManager().FireEvent(new ClientDisconnectedFromServerEvent(null));
        }
    }
}
