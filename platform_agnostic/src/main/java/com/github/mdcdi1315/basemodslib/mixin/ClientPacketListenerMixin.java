package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientConnectedToServerEvent;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientDisconnectedFromServerEvent;

import net.minecraft.client.multiplayer.ClientPacketListener;

import net.minecraft.network.protocol.game.ClientboundLoginPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public final class ClientPacketListenerMixin
{
    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void OnConnectedSuccessfully(ClientboundLoginPacket packet, CallbackInfo ci)
    {
        BaseModsLib.LOGGER.debug("EVENTS_MANAGER: Successfully connected to the server {}. Dispatching server connection event." , ((ClientPacketListener) (Object)this).getConnection().getRemoteAddress());
        BaseModsLib.GetEventsManager().FireEvent(new ClientConnectedToServerEvent());
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void OnPlayerDisconnected(CallbackInfo ci)
    {
        var details = ((ClientPacketListener) (Object)this).getConnection().getDisconnectionDetails();
        if (details == null) {
            BaseModsLib.LOGGER.debug("EVENTS_MANAGER: Received disconnection event. Dispatching server disconnection event.");
            BaseModsLib.GetEventsManager().FireEvent(new ClientDisconnectedFromServerEvent(null));
        } else {
            BaseModsLib.LOGGER.debug("EVENTS_MANAGER: Received abrupt disconnection event. Dispatching server disconnection event.");
            BaseModsLib.GetEventsManager().FireEvent(new ClientDisconnectedFromServerEvent(details.reason()));
        }
    }
}
