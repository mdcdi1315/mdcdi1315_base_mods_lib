package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.EventManager;
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
        EventManager.FireEventSafe(new ClientConnectedToServerEvent());
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void OnPlayerDisconnected(CallbackInfo ci)
    {
        // There is a rough edge case that the Minecraft instance will not have been destroyed after the lib was shut down.
        // In such case, we simply ignore altogether the dispatch of the event since we are already in a tear-down state.
        // Special thanks to @Gbergz for finding this. GitHub issue: #1.
        EventManager manager = BaseModsLib.GetEventsManager();
        if (manager != null)
        {
            var details = ((ClientPacketListener) (Object)this).getConnection().getDisconnectionDetails();
            if (details == null || details.report().isEmpty()) {
                BaseModsLib.LOGGER.debug("EVENTS_MANAGER: Received disconnection event. Dispatching server disconnection event.");
                manager.FireEvent(new ClientDisconnectedFromServerEvent(null));
            } else {
                BaseModsLib.LOGGER.debug("EVENTS_MANAGER: Received abrupt disconnection event. Dispatching server disconnection event.");
                manager.FireEvent(new ClientDisconnectedFromServerEvent(details.reason()));
            }
        }
    }
}
