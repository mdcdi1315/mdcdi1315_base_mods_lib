package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.EventManager;
import com.github.mdcdi1315.basemodslib.eventapi.server.NewPlayerConnectedToServerEvent;
import com.github.mdcdi1315.basemodslib.eventapi.server.PlayerDisconnectedFromServerEvent;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.network.CommonListenerCookie;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin
{
    // Inject this on tail to avoid possible race conditions if an event handler dispatches disconnection from this.
    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void OnNewPlayerConnected(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci)
    {
        BaseModsLib.LOGGER.debug("EVENTS_MANAGER: A new player was successfully connected. Dispatching player connection event.");
        EventManager.FireEventSafe(new NewPlayerConnectedToServerEvent(player));
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void OnPlayerDisconnected(ServerPlayer player, CallbackInfo ci)
    {
        BaseModsLib.LOGGER.debug("EVENTS_MANAGER: A player was disconnected. Dispatching player disconnection event.");
        EventManager.FireEventSafe(new PlayerDisconnectedFromServerEvent(player));
    }
}
